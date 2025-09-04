<template>
  <div>
    <div class="controls">
      <button @click="increaseRadius">Increase radius (+1km)</button>
      <button @click="decreaseRadius" :disabled="radius <= 1">Decrease radius (-1km)</button>
      <p>
        Current radius: {{ radius }} km</p>
      <router-link to="/Profile" class="profile-btn">Profile</router-link>
    </div>
    
    <!-- Dodali smo klasu custom-map -->
    <MapComponent 
      ref="mapComponent" 
      :initialLongitude="userLongitude" 
      :initialLatitude="userLatitude"
      class="custom-map">
    </MapComponent>
  </div>
</template>

<script>
import 'ol/ol.css';
import { fromLonLat, toLonLat } from 'ol/proj';
import Point from 'ol/geom/Point';
import CircleGeom from 'ol/geom/Circle';
import Feature from 'ol/Feature';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import { Style, Stroke } from 'ol/style';
import CircleStyle from 'ol/style/Circle';
import Fill from 'ol/style/Fill';
import axios from 'axios';
import MapComponent from '@/components/MapComponent.vue';

export default {
  components: {
    MapComponent
  },
  data() {
    return {
      userAddress: '',
      radius: 1, // Početni radius u km
      posts: [], // Lista postova
      postsFromLoggedUser: [],
      userLatitude: 44.80920324246111, // Default latitude (Beograd)
      userLongitude: 20.465976243346777, // Default longitude
      circleLayer: null,
      circleSource: null,
      addressLatitude: null,
      addressLongitude: null,
      addressSource: null,
      addressLayer: null,
      loggedInUserId: null
    };
  },
  mounted() {
    // Sačekamo da dobijemo user-a i da mapa bude ready, pa tek onda inicijalizujemo layer-e i geokodiramo
    Promise.all([this.getLoggedUser(), this.waitForMapReady()])
      .then(() => {
        // mapa je spremna (ili smo timeout-ovali) — napravimo layer-e i evente
        this.initCircleLayer();
        this.initAddressLayer();
       // this.setupMapClickListener();

        // Ako imamo adresu iz profila, geokodiraj je i centriraj
        if (this.userAddress) {
          this.geocodeAddress();
        } else {
          // inače postavi default view / crtaj krug
          this.updateCenterAndCircle();
        }

        // Učitaj postove
        this.fetchPosts();
      })
      .catch(err => {
        console.error('Greška prilikom inicijalizacije mape:', err);
      });
  },

  methods: {
    // Čeka da this.$refs.mapComponent.map postoji (polling). Timeout nakon 3s.
    waitForMapReady(timeout = 3000) {
      return new Promise((resolve) => {
        const interval = 100;
        let waited = 0;
        const iv = setInterval(() => {
          if (this.$refs.mapComponent && this.$refs.mapComponent.map) {
            clearInterval(iv);
            resolve();
          } else {
            waited += interval;
            if (waited >= timeout) {
              clearInterval(iv);
              // resolve iako nije pronađena mapa (čisto da app ne visi) — u tom slučaju neke stvari neće raditi
              resolve();
            }
          }
        }, interval);
      });
    },

    // vraća Promise (getLoggedUser je async i vraća Promise)
    async getLoggedUser(){
      const token = localStorage.getItem('token');
      if (token) {
        try {
          const response = await axios.post("http://localhost:8081/api/authentication/jwt/decode", { token }, {
            headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
          });
          if (response.status === 200) {
            const { id } = response.data;
            this.loggedInUserId = parseInt(id);

            const accountResponse = await axios.get(`http://localhost:8081/api/accounts/getById/${this.loggedInUserId}`, {
              headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` }
            });

            if (accountResponse.status === 200) {
              const { address } = accountResponse.data;
              this.userAddress = address || '';
            }
            console.log('Address: ' + this.userAddress, response.data);
          }
        } catch (error) {
          console.error('Failed to decode token or fetch account:', error);
        }
      } else {
        this.loggedInUserId = null;
        console.log('User id is: ' + this.loggedInUserId);
      }
    },

    async geocodeAddress() {
      if (!this.userAddress) {
        this.updateCenterAndCircle();
        return;
      }

      try {
        const response = await axios.get("https://nominatim.openstreetmap.org/search", {
          params: {
            q: this.userAddress,
            format: "json",
            limit: 1
          }
        });
        if (response.data && response.data.length > 0) {
          const location = response.data[0];

          this.addressLatitude = parseFloat(location.lat);
          this.addressLongitude = parseFloat(location.lon);

          // Centriraj mapu na adresu (userLatitude/userLongitude koristimo kao "center")
          this.userLatitude = this.addressLatitude;
          this.userLongitude = this.addressLongitude;

          // Dodaj adresni marker u dedicated layer (nećemo ga brisati)
          this.showAddressMarker();

          // Centriraj view i iscrtaj krug
          this.updateCenterAndCircle();
          // Osveži markere (postovi)
          this.updateMarkers();
        } else {
          console.warn("Adresa nije pronađena, koristi se default lokacija.");
          this.updateCenterAndCircle();
          this.updateMarkers();
        }
      } catch (error) {
        console.error("Greška pri geokodiranju adrese:", error);
        this.updateCenterAndCircle();
        this.updateMarkers();
      }
    },

    async fetchPosts() {
      try {
        const response = await axios.get("http://localhost:8081/api/posts/getAll", {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
        });
        this.posts = response.data.filter(post => post.latitude && post.longitude &&  post.account.id !== this.loggedInUserId);

        //TO DO: obrisati svoje objave iz prikazivanja

        
        // filtriramo po radiusu
        this.posts = this.posts.filter(post => {
          const distance = this.calculateDistance(
          this.userLatitude, this.userLongitude,
          post.latitude, post.longitude,
          console.log('user id from POSTS: ' + post.account.id),
      );
      return distance <= this.radius; // vraća samo one unutar radiusa
      });

        this.updateMarkers();
        console.log("Fetched posts:", this.posts);
      } catch (error) {
        console.error("Error fetching posts:", error);
      }
    },

    getUserLocation() {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            this.userLatitude = position.coords.latitude;
            this.userLongitude = position.coords.longitude;
            this.updateCenterAndCircle();
            this.updateMarkers();
          },
          (error) => {
            console.error("Error getting location:", error);
            this.updateCenterAndCircle();
            this.updateMarkers();
          }
        );
      } else {
        console.error("Geolocation is not supported by this browser.");
        this.updateCenterAndCircle();
        this.updateMarkers();
      }
    },

    initCircleLayer() {
      if (!this.$refs.mapComponent || !this.$refs.mapComponent.map) return;
      this.circleSource = new VectorSource();
      this.circleLayer = new VectorLayer({
        source: this.circleSource,
        style: new Style({
          stroke: new Stroke({
            color: 'red',
            width: 2
          })
        })
      });
      this.$refs.mapComponent.map.addLayer(this.circleLayer);
    },

    initAddressLayer() {
      if (!this.$refs.mapComponent || !this.$refs.mapComponent.map) return;
      // Layer za adresu — ne brišemo ga kod updateMarkers
      this.addressSource = new VectorSource();
      this.addressLayer = new VectorLayer({
        source: this.addressSource,
        zIndex: 999 // da je iznad ostalih markera
      });
      this.$refs.mapComponent.map.addLayer(this.addressLayer);

      // Ako već imamo adresu (npr. iz profila), prikaži je odmah
      if (this.addressLatitude && this.addressLongitude) {
        this.showAddressMarker();
      }
    },

    showAddressMarker() {
      if (!this.addressSource) return;
      // očistimo prethodni feature (ako postoji) i dodamo novi
      this.addressSource.clear();
      if (!this.addressLatitude || !this.addressLongitude) return;

      const addrFeature = new Feature({
        geometry: new Point(fromLonLat([this.addressLongitude, this.addressLatitude]))
      });

      // jednostavan stil za marker (možeš zameniti ikonama)
      const addrStyle = new Style({
        image: new CircleStyle({
          radius: 8,
          fill: new Fill({ color: '#d9534f' })
        })
      });
      addrFeature.setStyle(addrStyle);
      this.addressSource.addFeature(addrFeature);
    },

    setupMapClickListener() {
  
      if (!this.$refs.mapComponent || !this.$refs.mapComponent.map) return;
      this.$refs.mapComponent.map.on('click', (e) => {
        const lonLat = toLonLat(e.coordinate);
        this.userLongitude = lonLat[0];
        this.userLatitude = lonLat[1];
        // centriraj novi centar i iscrtaj krug, ali NE menjaj adresu
        this.updateCenterAndCircle();
        this.updateMarkers();
      });
    },

    updateCenterAndCircle() {
      if (!this.$refs.mapComponent) return;
      // centriraj view (MapComponent ima updateMap)
      if (this.$refs.mapComponent.updateMap) {
        this.$refs.mapComponent.updateMap(this.userLongitude, this.userLatitude);
      }
      // Ne dodajemo marker ovde (adresni marker imamo u posebnom layer-u)
      this.drawCircle();
    },

    drawCircle() {
      if (!this.circleSource) return;
      this.circleSource.clear();

      const center = fromLonLat([this.userLongitude, this.userLatitude]);
      const circle = new CircleGeom(center, this.radius * 1200); // radius u metrima
      const feature = new Feature(circle);
      this.circleSource.addFeature(feature);
    },

    increaseRadius() {
      this.radius += 1;
      this.drawCircle();
      this.updateMarkers();
      this.fetchPosts(); // ponovo fetch-ujemo postove da bismo uključili nove unutar većeg radiusa
    },

    decreaseRadius() {
      if (this.radius > 1) {
        this.radius -= 1;
        this.drawCircle();
        this.updateMarkers();
        this.fetchPosts(); // ponovo fetch-ujemo postove da bismo uključili nove unutar manjeg radiusa
      }
    },

    updateMarkers() {
      if (!this.$refs.mapComponent) return;

      // Očisti prethodne markere osim CRVENE tačke (korisnikove adrese)
      this.$refs.mapComponent.vectorSource.clear();

      // Crvena tačka korisnika ostaje
      if (this.addressLatitude && this.addressLongitude) {
        this.$refs.mapComponent.addMarker(
        fromLonLat([this.addressLongitude, this.addressLatitude]),
        "red" // boja crvena za korisnika
      );
      } //sakrivam ikonicu klika sa ovim.

      // Dodavanje markera za svaki post iz this.posts
      this.posts.forEach(post => {
        console.log('Post add id: ', post);
        this.$refs.mapComponent.addMarker(
          fromLonLat([post.longitude, post.latitude]), post.imagePath, post.id
        );
        console.log('Added marker for post at:', post.latitude, post.longitude);
      });
    },

    calculateDistance(lat1, lon1, lat2, lon2) {
      const R = 6371;
      const dLat = this.deg2rad(lat2 - lat1);
      const dLon = this.deg2rad(lon2 - lon1);
      const a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(this.deg2rad(lat1)) * Math.cos(this.deg2rad(lat2)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);
      const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
      return R * c;
    },

    deg2rad(deg) {
      return deg * (Math.PI / 180);
    },
  }
};
</script>

<style>
.controls {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.profile-btn {
  padding: 10px 20px;
  background-color: #e56b6b;
  color: white;
  text-decoration: none;
  border-radius: 5px;
}

.profile-btn:hover {
  background-color: #d75555;
}

/* nova visina mape - zauzima 65% visine ekrana */
.custom-map {
  display: block;
  height: 65vh;
}
.custom-map .map-container {
  height: 100% !important;
}
</style>
