<template>
  <div>
    <form @submit.prevent="sendMessage">
      <div>
        <label>Name:</label>
        <input v-model="name" required />
      </div>
      <div>
        <label>Latitude:</label>
        <input type="number" v-model.number="latitude" step="0.00000000000000001" required />
      </div>
      <div>
        <label>Longitude:</label>
        <input type="number" v-model.number="longitude" step="0.00000000000000001" required />
      </div>
      <div>
        <label>Description:</label>
        <textarea v-model="description"></textarea>
      </div>
      <button type="submit">Send Message</button>
    </form>

    <!-- Mapa -->
    <MapComponent @locationSelected="updateLocation" />

    <p v-if="status" :style="{ color: statusColor }">{{ status }}</p>
  </div>
</template>

<script>
import MapComponent from './MapComponent.vue';
import axios from 'axios';

export default {
  components: { MapComponent },
  data() {
    return {
      name: '',
      latitude: null,
      longitude: null,
      description: '',
      status: '',
      statusColor: 'green'
    };
  },
  methods: {
    updateLocation(location) {
      this.latitude = location.latitude;
      this.longitude = location.longitude;
      console.log('Selected lat/lon:', this.latitude, this.longitude);
    },
    async sendMessage() {
      try {
        const payload = {
          name: this.name,
          latitude: this.latitude,
          longitude: this.longitude,
          description: this.description
        };
        await axios.post('http://localhost:8082/api/locations', payload);
        this.status = 'Message sent successfully!';
        this.statusColor = 'green';
        this.name = '';
        this.latitude = null;
        this.longitude = null;
        this.description = '';
      } catch (err) {
        console.error(err);
        this.status = 'Error sending message.';
        this.statusColor = 'red';
      }
    }
  }
};
</script>

<style scoped>
form div {
  margin-bottom: 10px;
}
label {
  display: inline-block;
  width: 100px;
}
input, textarea {
  width: 300px;
}
button {
  padding: 5px 10px;
}
</style>
