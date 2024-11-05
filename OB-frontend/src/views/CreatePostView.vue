<template>
    <div class="register-container-login">
      <form @submit.prevent="createPost">
        <input v-model="description" placeholder="Opis objave" required />
        <input type="file" @change="handleFileUpload" required />
        <!-- <map @locationSelected="updateLocation" />  Map komponenta -->
        <button type="submit">Postavi objavu</button>
      </form>
    </div>
</template>

<script>
import axios from 'axios';
export default {
  data() {
    return {
      description: "",
      image: null,
      latitude: null,
      longitude: null,
      posts: [],
      userId:0,
      username: '',
      role: ''
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
      const file = event.target.files[0];
      const reader = new FileReader();
      reader.onload = (e) => {
        this.image = e.target.result.split(",")[1]; // Base64 string slike
      };
      reader.readAsDataURL(file);
    },

    async createPost() {
      const post = {
        description: this.description,
        image: this.image,
        latitude: this.latitude,
        longitude: this.longitude,
        accId: this.userId
      };
      const response = await axios.post("http://localhost:8081/api/posts/create", post);
      if (response.data){
        this.postId = response.data.id;
      }
      this.fetchPosts();
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
    }
  },
  created() {
    this.fetchPosts(); 
  }
};
</script>
<style>
.register-container-login {
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



#LogInButton{
  background-color: var(--clr-primary);
  border: none;
  padding: 10px;
  font-size: 1.2em;
  cursor: pointer;
  transition: background-color 0.3s;
  width: 100%;
}


#LogInButton:hover {
  background-color: var(--clr-primary);
}

.error-login{
  text-align: center;
  color: red;
}
.logInTitle{
  text-align: center;
  padding-bottom: 30px;
}
</style>