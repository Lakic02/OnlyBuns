<template>
  <div v-if="isAdminOrRegisteredUser">
    <h1 class="home-posts-title">Posts from Users You Follow</h1>
    <div class="post-container">
      <div v-for="post in posts" :key="post.id" class="post-card" @click="navigateToPostDetails(post.id)">
        <div class="post-header">
          <img v-if="post.account.profileImage" :src="post.account.profileImage" alt="User Profile" class="profile-image" />
          <div>
            <h3>{{ post.account.userName }}</h3>
            <p class="post-date">{{ formatDate(post.creationTime) }}</p>
          </div>
        </div>
        
        <div class="post-body">
          <!-- Prikaz forme za editovanje ako je trenutni post u edit modu -->
          <div v-if="editingPost && editingPost.id === post.id">
            <textarea 
              v-model="editingPost.description" 
              class="posts-edit-input"
              placeholder="Input new text"
            ></textarea>
            
            <div class="edit-image-container">
              <input 
                type="file" 
                @change="handleImageChange" 
                accept="image/*"
                class="edit-post-file-input"
              >
              <img 
                v-if="imagePreview" 
                :src="imagePreview" 
                alt="Preview" 
                class="image-preview"
              >
            </div>
          </div>
          
          <!-- Normalan prikaz posta ako nije u edit modu -->
          <div v-else>
            <p>{{ post.description }}</p>
            <img v-if="post.imageUrl" :src="post.imageUrl" alt="Post Image" class="post-image" />
          </div>
        </div>
        
        <div class="post-footer">
          <span>{{ postLikes[post.id] || 0 }} Likes</span>
        </div>
      </div>
    </div>
  </div>

  <div v-if="!isAdminOrRegisteredUser">
    <h1 class="home-posts-title">Posts</h1>
    <div class="post-container">
      <div v-for="post in posts" :key="post.id" class="post-card" @click="navigateToPostDetails(post.id)">
        <div class="post-header">
          <img v-if="post.account.profileImage" :src="post.account.profileImage" alt="User Profile" class="profile-image" />
          <div>
            <h3>{{ post.account.userName }}</h3>
            <p class="post-date">{{ formatDate(post.creationTime) }}</p>
          </div>
        </div>
        
        <div class="post-body">
          <!-- Prikaz forme za editovanje ako je trenutni post u edit modu -->
          <div v-if="editingPost && editingPost.id === post.id">
            <textarea 
              v-model="editingPost.description" 
              class="posts-edit-input"
              placeholder="Input new text"
            ></textarea>
            
            <div class="edit-image-container">
              <input 
                type="file" 
                @change="handleImageChange" 
                accept="image/*"
                class="edit-post-file-input"
              >
              <img 
                v-if="imagePreview" 
                :src="imagePreview" 
                alt="Preview" 
                class="image-preview"
              >
            </div>
          </div>
          
          <!-- Normalan prikaz posta ako nije u edit modu -->
          <div v-else>
            <p>{{ post.description }}</p>
            <img v-if="post.imageUrl" :src="post.imageUrl" alt="Post Image" class="post-image" />
          </div>
        </div>
        
        <div class="post-footer">
          <span>{{ postLikes[post.id] || 0 }} Likes</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      posts: [],
      postLikes: {},
      loggedInUserId: 0,
      editingPost: null,
      newImage: null,
      fileName: null,
      imagePreview: null,
      loggedInUserRole: ''
    };
  },
  mounted() {
    this.loadUser()
      .then(() => this.fetchPosts())
    .catch(error => {
      console.error("An error occurred while fetching data: ", error);
    });
  },
  computed: {
    // Ova computed property proverava da li je korisnik admin ili registrovani korisnik
    isAdminOrRegisteredUser() {
      return this.loggedInUserRole === 'admin' || this.loggedInUserRole === 'registered';
    }
  },
  methods: {
    startEditing(post) {
      this.editingPost = {
        id: post.id,
        description: post.description
      };
      this.imagePreview = post.imageUrl;
    },
    cancelEdit() {
      this.editingPost = null;
      this.newImage = null;
      this.imagePreview = null;
    },
    navigateToPostDetails(postId) {
      this.$router.push({ name: 'PostDetails', params: { postId } });
    },
    handleImageChange(event) {
      const selectedFile = event.target.files[0];
      if (selectedFile) {
        this.newImage = selectedFile;
        this.fileName = selectedFile ? selectedFile.name : null;
        this.imagePreview = URL.createObjectURL(selectedFile);
      }
    },
    async saveEdit(postId) {
      try {
        const formData = new FormData();
        formData.append('description', this.editingPost.description);
        if (this.newImage) {
          formData.append('file', this.newImage);
        }

        const response = await axios.put(
          `http://localhost:8081/api/posts/update/${postId}`,
          formData,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }
        );

        if (response.data) {
          // Osvežavanje liste postova
          await this.fetchPosts();
          this.cancelEdit();
        }
      } catch (error) {
        console.error('Error updating post:', error);
      }
    },
    fetchPosts() {
      if(this.loggedInUserRole === 'admin' || this.loggedInUserRole === 'registered'){
        axios.get(`http://localhost:8081/api/posts/followed/${this.loggedInUserId}`)
        .then(response => {
          this.posts = response.data;
          for (const post of this.posts) {
            this.fetchPostFile(post.id);
            this.fetchPostLikes(post.id);
          }
        })
        .catch(error => {
          console.error("There was an error fetching posts:", error);
        });
      }else{
        axios.get('http://localhost:8081/api/posts/getAll')
        .then(response => {
          this.posts = response.data;
          for (const post of this.posts) {
            this.fetchPostFile(post.id);
            this.fetchPostLikes(post.id);
          }
        })
        .catch(error => {
          console.error("There was an error fetching posts:", error);
        });
      }
      
    },
    formatDate(date) {
      const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
      return new Date(date).toLocaleDateString(undefined, options);
    },
    async fetchPostFile(postId) {
      try {
        const response = await axios.get(`http://localhost:8081/api/posts/getFile/${postId}`);
        let imagePath = response.data.imagePath;
        let compress = response.data.compress;

        if (compress === 1) {
          imagePath = imagePath.replace(/\\/g, '/');
          this.posts.find(post => post.id === postId).imageUrl = `http://localhost:8081/compressedImages/${imagePath}`;
        } else {
          imagePath = imagePath.replace(/\\/g, '/');
          this.posts.find(post => post.id === postId).imageUrl = `http://localhost:8081/images/${imagePath}`;
        }

      } catch (error) {
        console.error('Error fetching file:', error);
      }
    },
    async fetchPostLikes(postId) {
      try {
        const response = await axios.get(`http://localhost:8081/api/posts/likes/count/${postId}`);
        this.postLikes[postId] = response.data;
      } catch (error) {
        console.error('Error fetching likes:', error);
        this.postLikes[postId] = 0;
      }
    },
    async loadUser() {
      const token = localStorage.getItem('token');
      if (token) {
        try {
          const response = await axios.post("http://localhost:8081/api/authentication/jwt/decode", { token });
          if (response.status === 200) {
            const { id, username, role } = response.data;
            this.loggedInUserId = id;
            this.loggedInUserRole = role;
          }
        } catch (error) {
          console.error('Failed to decode token:', error);
        }
      } else {
        this.loggedInUserId = 0;
      }
    }
  }
};
</script>
<style>

</style>