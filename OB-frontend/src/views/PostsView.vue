<template>
  <h1 class="admin-posts-title">Check All Posts</h1>
  <div class="post-container">
    <div v-for="post in posts" :key="post.id" class="post-card" @click="navigateToPostDetails(post.id)">
      <div class="post-header">
        <img v-if="post.account.profileImage" :src="post.account.profileImage" alt="User Profile" class="profile-image" />
        <div>
          <h3>{{ post.account.userName }}</h3>
          <p class="post-date">{{ formatDate(post.creationTime) }}</p>
        </div>
        <div class="post-actions" v-if="loggedInUserId === post.account.id">
          <button @click="startEditing(post)" class="action-button">✏️</button>
          <button @click="deletePost(post.id)" class="action-button">🗑️</button>
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
          
          <div class="edit-buttons">
            <button @click="saveEdit(post.id)" class="posts-save-button">Save changes</button>
            <button @click="cancelEdit" class="posts-cancel-button">Cancel</button>
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
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      posts: [],
      postImageUrls: {},
      postLikes: {},
      loggedInUserId: 0,
      editingPost: null,
      newImage: null,
      fileName: null,
      imagePreview: null
    };
  },
  mounted() {
    this.fetchPosts();
    this.loadUser();
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
.admin-posts-title {
  margin-top: 20px;
  color: var(--clr-black);
  text-align: center;
  margin-bottom: 30px;
}
.post-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  padding: 20px;
  justify-content: center;
}

.post-card {
  width: 300px;
  background-color: #fff;
  border: 1px solid #e1e8ed;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
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

.posts-edit-input {
  width: 100%;
  min-height: 100px;
  padding: 8px;
  margin-bottom: 10px;
  border: 1px solid #e1e8ed;
  border-radius: 4px;
  resize: vertical;
}

.edit-image-container {
  margin: 10px 0;
}

.edit-post-file-input {
  margin-bottom: 10px;
}

.image-preview {
  max-width: 100%;
  height: auto;
  margin-top: 10px;
  border-radius: 8px;
}

.edit-buttons {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.posts-save-button, .posts-cancel-button {
  padding: 6px 12px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
}

.posts-save-button {
  background-color: #1da1f2;
  color: white;
}

.posts-cancel-button {
  background-color: #657786;
  color: white;
}

.posts-save-button:hover {
  background-color: #1991da;
}

.posts-cancel-button:hover {
  background-color: #566b76;
}
.post-card:hover {
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3); /* Povećana senka */
  transform: scale(1.02); /* Blago povećanje kartice */
}
</style>