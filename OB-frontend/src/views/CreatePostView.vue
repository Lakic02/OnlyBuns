<template>
  <div class="form-container">
    <form @submit.prevent="createPost" class="form">
      <input v-model="description" placeholder="Description" required class="input-field" />
      
      <label for="file" class="file-label">
        Choose File
        <input type="file" id="file" @change="handleFileUpload" required class="file-input" />
      </label>
      
      <div v-if="fileName" class="file-info">
        File: {{ fileName }}
      </div>
      
      <MapComponent class="map-component" ref="mapComponent" @locationSelected="updateLocation"></MapComponent>
      
      <div v-if="latitude && longitude" class="location-info">
        <p>Address: {{ address.city }}, {{ address.road }} {{ address.house_number }}</p>
      </div>
      
      <button type="submit" class="submit-btn">Post</button>
    </form>
  </div>
</template>
<script>

import axios from 'axios';
import MapComponent from '@/components/MapComponent.vue';

export default {
  components:{
    MapComponent
  },
  data() {
    return {
      description: "",
      image: null,
      latitude: null,
      longitude: null,
      posts: [],
      userId:0,
      username: '',
      role: '',
      file: null,
      fileName: null,
      address: null
    };
  },
  async mounted(){
    const token = localStorage.getItem('token');
      if (token) {
        try {
          const response = await axios.post("http://localhost:8081/api/authentication/jwt/decode", { token });
          if (response.status === 200) {
            const {id, username, role } = response.data;
            this.username = username;
            this.role = role;
            this.userId = id;
          }
          else{
            this.SetDefaultVisibilities()
          }
        } catch (error) {
          console.error('Failed to decode token:', error);
        }
      }
  },
  methods: {
    handleFileUpload(event) {
      const selectedFile = event.target.files[0];
      this.file = selectedFile;
      this.fileName = selectedFile ? selectedFile.name : null;
    },

    async createPost() {
    const formData = new FormData();
    formData.append("description", this.description);
    formData.append("latitude", this.latitude);
    formData.append("longitude", this.longitude);
    formData.append("file", this.file);

    for (let [key, value] of formData.entries()) {
        console.log(key, value);
    }

    try {
        const response = await axios.post(
            `http://localhost:8081/api/posts/create/${this.userId}`,
            formData,
            {
                headers: {
                    "Content-Type": "multipart/form-data" 
                }
            }
        );
        if (response.data) {
            this.postId = response.data.id;
        }
        this.fetchPosts();
    } catch (error) {
        console.error("Error creating post:", error);
    }
}, 
    updateLocation(location){
      this.longitude = location.longitude
      this.latitude = location.latitude
      this.address = location.address;
      console.log(this.longitude)
      console.log(this.latitude)
      console.log(this.address)
    },

    async likePost() {
      await axios.post(`http://localhost:8081/api/posts/like/${this.postId}/${this.userId}`);
      this.fetchPosts(); 
    },

    async commentOnPost() {
      const comment = {
        text: this.commentText
      };
      await axios.post(`http://localhost:8081/api/posts/comment/${this.postId}/${this.userId}`, comment);
      this.commentText = "";
      this.fetchPosts(); 
    },

    async fetchPosts() {
      try {
        const response = await axios.get("http://localhost:8081/api/posts/getAll");
        this.posts = response.data; 
      } catch (error) {
        console.error("Error fetching posts:", error);
      }
    },
   /* async fetchPostFile(postId) {
    try {
        const response = await axios.get(`http://localhost:8081/api/posts/getFile/${postId}`, { responseType: 'arraybuffer' });
        const blob = new Blob([response.data], { type: 'image/png' }); // Ili odgovarajući MIME tip za vaš fajl
        const url = URL.createObjectURL(blob);
        this.imageUrl = url; // Stvaranje URL-a za sliku
        console.log("AAAAAAAAAAAAAA"+this.imageUrl)
    } catch (error) {
        console.error('Error fetching file:', error);
    }
  },*/
  created() {
    this.fetchPosts(); 
    
  }
  
}
};
</script>
<style>
.form-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: white;
}

.form {
  display: flex;
  flex-direction: column;
  background-color: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  width: 400px;
}

.input-field {
  padding: 10px;
  margin: 10px 0;
  border: 1px solid #e56b6b;
  border-radius: 5px;
  font-size: 16px;
  outline: none;
}

.input-field:focus {
  border-color: #e56b6b;
}

.map-component {
  margin: 20px 0;
}

.location-info {
  margin: 10px 0;
  font-size: 14px;
  color: #e56b6b;
}

.submit-btn {
  padding: 12px;
  background-color: #e56b6b;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.submit-btn:hover {
  background-color: #d75555;
}

.file-label {
  background-color: #e56b6b;
  color: white;
  padding: 12px;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  text-align: center;
  margin-bottom: 20px;
  display: block;
  transition: background-color 0.3s;
}

.file-label:hover {
  background-color: #d75555;
}

.file-input {
  display: none;
}

.file-info {
  margin-top: 10px;
  font-size: 14px;
  color: #e56b6b;
  font-weight: 600;
}
</style>