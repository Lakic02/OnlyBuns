<template>
  <div class="post-container">
    <div v-for="post in posts" :key="post.id" class="post-card">
      <div class="post-header">
        <img v-if="post.account.profileImage" :src="post.account.profileImage" alt="User Profile" class="profile-image" />
        <div>
          <h3>{{ post.account.userName }}</h3>
          <p class="post-date">{{ formatDate(post.creationTime) }}</p>
        </div>
        <div class="post-actions" v-if="loggedInUserId === post.account.id">
          <button @click="editPost(post.id)" class="action-button">✏️</button>
          <button @click="deletePost(post.id)" class="action-button">🗑️</button>
        </div>
      </div>
      
      <div class="post-body">
        <p>{{ post.description }}</p>
        <img v-if="post.imageUrl" :src="post.imageUrl" alt="Post Image" class="post-image" />
      </div>
      
      <div class="post-footer">
        <span>{{ postLikes[post.id] || 0 }} Likes</span>
        <span>Comments...</span>
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
      postImageUrls: {},
      postLikes: {},
      loggedInUserId: 0
    };
  },
  mounted() {
    this.fetchPosts();
    this.loadUser();
  },
  methods: {
    fetchPosts() {
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
    },
    formatDate(date) {
      const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
      return new Date(date).toLocaleDateString(undefined, options);
    },
    postImage(imageData) {
      // Convert binary data to a URL object for the image
      return `data:image/png;base64,${btoa(new Uint8Array(imageData).reduce((data, byte) => data + String.fromCharCode(byte), ''))}`;
    },
    async fetchPostFile(postId) {
  try {
    const response = await axios.get(`http://localhost:8081/api/posts/getFile/${postId}`);
    let imagePath = response.data;
    imagePath = imagePath.replace(/\\/g, '/'); // Zameni sve backslash-e sa slash-ovima
    
    // Postavljanje pune URL putanje za sliku
    this.posts.find(post => post.id === postId).imageUrl = `http://localhost:8081/images/${imagePath}`;
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
    async deletePost(postId){
      try {
        const response = await axios.delete(`http://localhost:8081/api/posts/delete/${postId}`);
        if(response.data){
          this.posts = []
          this.fetchPosts();
        }
      } catch (error) {
        console.error('Error deleting post:', error);
      }
    },
    async loadUser(){
      const token = localStorage.getItem('token');
      if (token) {
        try {
          const response = await axios.post("http://localhost:8081/api/authentication/jwt/decode", { token });
          if (response.status === 200) {
            const { id, username, role } = response.data;
            this.loggedInUserId = id;
          }
        } catch (error) {
          console.error('Failed to decode token:', error);
        }
      }else{
        this.loggedInUserId = 0
      }
    }
  }
};
</script>

<style>
.post-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  padding: 1rem;
  justify-content: center;
}

.post-card {
  width: 300px;
  background-color: #fff;
  border: 1px solid #e1e8ed;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-top: 25vh;
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
  justify-content: space-between;
}
.post-actions {
  display: flex;
  gap: 0.3rem;
}
.action-button {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.1rem;
  color: #657786;
}
.action-button:hover {
  background-color: #e0245e;
}

.profile-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 0.5rem;
}

.post-header h3 {
  font-size: 1rem;
  margin: 0;
}

.post-date {
  font-size: 0.8rem;
  color: #657786;
}

.post-body p {
  font-size: 0.9rem;
  margin: 0.5rem 0;
  color: #14171a;
}

.post-image {
  width: 100%;
  height: auto;
  margin-top: 0.5rem;
  border-radius: 8px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
  font-size: 0.9rem;
  color: #657786;
}
</style>