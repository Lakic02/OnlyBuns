<template>
    <div class="map-container" ref="mapContainer"></div>
  </template>
  
  <script>
  import 'ol/ol.css';
  import { Map, View } from 'ol';
  import TileLayer from 'ol/layer/Tile';
  import OSM from 'ol/source/OSM';
  import { fromLonLat, toLonLat, transform } from 'ol/proj';
  import { defaults as defaultControls, ScaleLine } from 'ol/control';
  import VectorLayer from 'ol/layer/Vector';
  import VectorSource from 'ol/source/Vector';
  import Feature from 'ol/Feature';
  import Point from 'ol/geom/Point';
  import { Icon, Style } from 'ol/style';
  import axios from 'axios';
  
  export default {
    props: {
      initialLongitude: {
        type: Number,
        required: true,
        default:20.465976243346777
      },
      initialLatitude: {
        type: Number,
        required: true,
        default:44.80920324246111
      }
    },
    data() {
      return {
        map: null,
        vectorSource: null,
        isMarkerUpdateEnabled: true
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
          style: new Style({
            image: new Icon({
              src: 'https://openlayers.org/en/latest/examples/data/icon.png',
              anchor: [0.5, 1],
            }),
          }),
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
  
        this.map.on('click', (e) => {
          var coordinates = e.coordinate;
          //console.log('Coordinates: ', coordinates);
  
          // Transformišite koordinate iz EPSG:3857 u EPSG:4326 format
          const lonLat = transform(coordinates, 'EPSG:3857', 'EPSG:4326');
          const lon = lonLat[0];
          const lat = lonLat[1];
          //console.log(lon);
          //console.log(lat);
  
          // Dodajte oznaku na mapu
          if(this.isMarkerUpdateEnabled){
            this.addMarker(coordinates);
          }
  
          const url = 'https://nominatim.openstreetmap.org/reverse?format=json&lon=' + lon + '&lat=' + lat;
  
          fetch(url)
            .then(response => response.json())
            .then(data => {
              if (data && data.address) {
                //console.log('Address: ', data.address);
                const country = data.address.country;
                const city = data.address.city || data.address.town || data.address.village;
                //console.log('Country: ', country);
                //console.log('City: ', city);
                this.$emit('location-selected', {
                  longitude: lon,
                  latitude: lat,
                  address: data.address
                });
              } else {
                console.log('Address: not found');
              }
            })
            .catch(error => console.log('Error: ', error));
        });
      },
      addMarker(coordinate) {
        // Kreirajte novi feature za marker
        const marker = new Feature({
          geometry: new Point(coordinate),
        });
  
        // Dodajte feature u vector source
        this.vectorSource.clear(); // Očistite prethodne markere ako želite samo jedan marker u isto vreme
        this.vectorSource.addFeature(marker);
      },
      removeAllMarkers() {
        this.vectorSource.clear();
      },
      updateMap(longitude, latitude) {
        if(this.isMarkerUpdateEnabled){
          this.map.getView().setCenter(fromLonLat([longitude, latitude]));
          this.map.getView().setZoom(15);
          this.addMarker(fromLonLat([longitude, latitude]));
        }
      },
      setMarkerUpdateEnabled(enabled) {
        this.isMarkerUpdateEnabled = enabled;
      }
    },
  };
  </script>
  
  <style>
  .map-container {
    width: 100%;
    height: 200px;
    margin-top: 10px;
    margin-bottom: 10px
  }
  </style>