<template>
    <div class="register-container-create">
      <form @submit.prevent="createPost" class="aa">
        <input v-model="description" placeholder="Opis objave" required />
        <input type="file" @change="handleFileUpload" required />
        <MapComponent class="Map" ref="mapComponent" @locationSelected="updateLocation"></MapComponent>
        <!-- <map @locationSelected="updateLocation" />  Map komponenta -->
        <button type="submit">Postavi objavu</button>
      </form>
    </div>
   <!-- <MapComponent class="Map" ref="mapComponent" @location-selected="updateLocation"></MapComponent>
    <img class="imagee"  :src="imageUrl" alt="Uploaded image" />-->

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
    //await this.fetchPostFile(this.postId);
  },
  methods: {
    handleFileUpload(event) {
      this.file = event.target.files[0];
    },

    async createPost() {
    const formData = new FormData();
    formData.append("description", this.description);
    formData.append("latitude", this.latitude);
    formData.append("longitude", this.longitude);
    formData.append("file", this.file);

    // Proverite FormData sadržaj
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
      console.log(this.longitude)
      console.log(this.latitude)
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
.register-container-create {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-bottom: 60px;
  height: 60vh;
}

.form-login {
  display: flex;
  flex-direction: column;
  width: 550px;
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.form-group-login {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.imagee{
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.aa{
  margin-top: 100px;
}
</style>