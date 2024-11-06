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
    formData.append("file", this.file); // Dodajemo fajl u formData

    try {
      console.log("jswsjiwjdijwijdijwij")
        const response = await axios.post(
            `http://localhost:8081/api/posts/create/${this.userId}`,
            formData, // Saljemo formData
            {
                headers: {
                    "Content-Type": "multipart/form-data" // Postavljanje odgovarajućeg Content-Type
                }
            }
            
        );
        if (response.data) {
            this.postId = response.data.id;
            console.log("223333333333333")
        }
        this.fetchPosts();
    } catch (error) {
        console.error("Error creating post:", error);
    }
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