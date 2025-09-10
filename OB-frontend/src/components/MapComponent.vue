<template>
  <div class="map-container" ref="mapContainer"></div>
</template>

<script>
import 'ol/ol.css';
import { Map, View } from 'ol';
import TileLayer from 'ol/layer/Tile';
import OSM from 'ol/source/OSM';
import { fromLonLat, transform } from 'ol/proj';
import { defaults as defaultControls, ScaleLine } from 'ol/control';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import Feature from 'ol/Feature';
import Point from 'ol/geom/Point';
import { Icon, Style, Circle as CircleStyle, Fill, Stroke } from 'ol/style';
import bluePin from '@/images/bluepin.png';
import greenPin from '@/images/greenPin.png';
import axios from 'axios';


export default {
  props: {
    initialLongitude: {
      type: Number,
      required: true,
      default: 20.465976243346777,
    },
    initialLatitude: {
      type: Number,
      required: true,
      default: 44.80920324246111,
    },
  },
  data() {
    return {
      map: null,
      vectorSource: null,
      isMarkerUpdateEnabled: true,
    };
  },
  mounted() {
    this.initMap();
  },
  methods: {
    initMap() {
      const coordinates = fromLonLat([this.initialLongitude, this.initialLatitude]);
      this.vectorSource = new VectorSource();

      const vectorLayer = new VectorLayer({
        source: this.vectorSource,
      });

      this.map = new Map({
        target: this.$refs.mapContainer,
        layers: [
          new TileLayer({
            source: new OSM(),
          }),
          vectorLayer,
        ],
        view: new View({
          center: coordinates,
          zoom: 7,
        }),
        controls: defaultControls().extend([new ScaleLine()]),
      });

      // Klik na mapu za izbor lokacije (ne dira markere)
      this.map.on('click', (e) => {
        const coordinates = e.coordinate;
        const lonLat = transform(coordinates, 'EPSG:3857', 'EPSG:4326');
        const lon = lonLat[0];
        const lat = lonLat[1];

        if (this.isMarkerUpdateEnabled) {
          const url = `https://nominatim.openstreetmap.org/reverse?format=json&lon=${lon}&lat=${lat}`;
          fetch(url)
            .then((response) => response.json())
            .then((data) => {
              if (data && data.address) {
                this.$emit('location-selected', {
                  longitude: lon,
                  latitude: lat,
                  address: data.address,
                });
              }
            })
            .catch((error) => console.log('Error: ', error));
        }
      });

      // Detektovanje klika na marker
      this.map.on('singleclick', (event) => {console.log('Test 1:');
        this.map.forEachFeatureAtPixel(event.pixel, (feature) => {
          const postId = feature.get('postId');
          console.log('Test 2:');
          if (postId) {
            // Emituje event ka parent komponenti
            this.$router.push({ name: 'PostDetails', params: { postId } });
          }
          const description = feature.get('description');
          if (description) {
            console.log('Care location clicked with description:', description);
            alert(`Care Location: ${description}`);
          }
        });
      });
    },

    addMarker(coordinate, iconUrl = null, postId = null) {
      const marker = new Feature({
        geometry: new Point(coordinate),
      });

      if (postId != null) {
        marker.setStyle(
          new Style({
            image: new Icon({
              src: bluePin,
              scale: 0.08,
              anchor: [0.5, 1],
            }),
          })
        );

        if (postId) {
          marker.set('postId', postId); // čuvamo postId u feature
        }
      } else {
        // Default marker (npr. crvena tačka za lokaciju korisnika)
        marker.setStyle(
          new Style({
            image: new CircleStyle({
              radius: 6,
              fill: new Fill({ color: 'red' }),
              stroke: new Stroke({ color: 'white', width: 2 }),
            }),
          })
        );
      }

      this.vectorSource.addFeature(marker);
    },

    removeAllMarkers() {
      this.vectorSource.clear();
    },

    updateMap(longitude, latitude) {
      if (this.isMarkerUpdateEnabled) {
        this.map.getView().setCenter(fromLonLat([longitude, latitude]));
        this.map.getView().setZoom(15);
        this.addMarker(fromLonLat([longitude, latitude])); // crvena tačka za korisnika
      }
    },

    setMarkerUpdateEnabled(enabled) {
      this.isMarkerUpdateEnabled = enabled;
    },

    addCareMarker(coordinate, careId = null, description = null) {
      const marker = new Feature({
        geometry: new Point(coordinate),
      });

      marker.setStyle(
        new Style({
          image: new Icon({
            src: greenPin, // koristi drugu ikonicu
            scale: 0.09,
            anchor: [0.5, 1],
          }),
        })
      );

      if (careId) {
        marker.set('careId', careId);
      }

      if (description) {
        marker.set('description', description);
      }

      this.vectorSource.addFeature(marker);
    },




  },
};
</script>

<style>
.map-container {
  width: 100%;
  height: 200px;
  margin-top: 10px;
  margin-bottom: 10px;
}
</style>
