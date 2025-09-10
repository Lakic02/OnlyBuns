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
import { Icon, Style, Circle as CircleStyle, Fill, Stroke } from 'ol/style';  // ubaci plavu ikonicu

export default {
  props: {
    initialLongitude: { type: Number, default: 20.465976243346777 },
    initialLatitude: { type: Number, default: 44.80920324246111 },
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
      this.vectorSource = new VectorSource();
      const vectorLayer = new VectorLayer({ source: this.vectorSource });

      const centerCoords = fromLonLat([this.initialLongitude, this.initialLatitude]);

      this.map = new Map({
        target: this.$refs.mapContainer,
        layers: [
          new TileLayer({ source: new OSM() }),
          vectorLayer
        ],
        view: new View({ center: centerCoords, zoom: 7 }),
        controls: defaultControls().extend([new ScaleLine()]),
      });

      // Klik na mapu za izbor lokacije
      this.map.on('click', async (e) => {
        const coords = e.coordinate;
        const lonLat = transform(coords, 'EPSG:3857', 'EPSG:4326');
        const lon = lonLat[0];
        const lat = lonLat[1];

        

        if (this.isMarkerUpdateEnabled) {
          try {
            const url = `https://nominatim.openstreetmap.org/reverse?format=json&lon=${lon}&lat=${lat}`;
            const response = await fetch(url);
            const data = await response.json();
            if (data) {
              this.$emit('locationSelected', {
                longitude: lon,
                latitude: lat,
              });
             console.log('Map clicked at:', lon, lat);
            }
          } catch (err) {
            console.error('Error fetching address:', err);
          }
        }
      });
    },
  }
};
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 300px;
  margin-top: 10px;
}
</style>
