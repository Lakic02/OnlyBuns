<template>
<div class="pd-container">
    <div v-if="post" class="pd-card">
      <!-- Post Header -->
      <div class="pd-header">
        <div class="pd-user-info">
          <img 
            v-if="post.account.profileImage" 
            :src="post.account.profileImage" 
            alt="User Profile" 
            class="pd-profile-image"
          />
          <div class="pd-user-details">
            <h3 @click="loggedInUserId !== post.account.id ? navigateToUser(post.account.id) : null" 
                style="cursor: pointer;" class="pd-username">{{ post.account.userName }}</h3>
            <p class="pd-post-time">{{ formatDate(post.creationTime) }}</p>
          </div>
          <!-- Added Post Actions -->
          <div class="pd-post-actions" v-if="loggedInUserId === post.account.id">
            <button @click="startEditing(post)" class="pd-action-button">✏️</button>
            <button @click="deletePost(post.id)" class="pd-action-button">🗑️</button>
          </div>
          <div class="pd-post-actions" v-if="loggedInUserRole === 'admin'">
            <button @click="publishPost(post.id)" class="pd-action-button">Publish post</button>
          </div>
        </div>
      </div>

      <!-- Edit Form -->
      <div v-if="editingPost && editingPost.id === post.id" class="pd-edit-form">
        <textarea 
          v-model="editingPost.description" 
          class="pd-edit-input"
          placeholder="Input new text"
        ></textarea>
        
        <div class="pd-edit-image-container">
          <input 
            type="file" 
            @change="handleImageChange" 
            accept="image/*"
            class="pd-edit-file-input"
          >
          <img 
            v-if="imagePreview" 
            :src="imagePreview" 
            alt="Preview" 
            class="pd-image-preview"
          >
        </div>
        
        <div class="pd-edit-buttons">
          <button @click="saveEdit(post.id)" class="pd-save-button">Save changes</button>
          <button @click="cancelEdit" class="pd-cancel-button">Cancel</button>
        </div>
      </div>

      <!-- Post Image -->
      <div class="pd-image-container">
        <img 
          v-if="post.imageUrl" 
          :src="post.imageUrl" 
          alt="Post Image" 
          class="pd-post-image"
        />
      </div>

      <!-- Post Content -->
      <div class="pd-content">
        <p class="pd-description">{{ post.description }}</p>
        
        <!-- Location Info -->
        <div v-if="post.latitude && post.longitude" class="pd-location-info">
          <div class="pd-coordinates">
            <p>
              <span class="pd-label">Latitude:</span> 
              {{ formatCoordinate(post.latitude) }}
            </p>
            <p>
              <span class="pd-label">Longitude:</span> 
              {{ formatCoordinate(post.longitude) }}
            </p>
          </div>
          <a 
            :href="getGoogleMapsLink(post.latitude, post.longitude)" 
            target="_blank"
            class="pd-map-link"
          >
            View on Map
          </a>
        </div>

        <!-- Engagement Stats -->
        <div class="pd-engagement-stats">
          <div class="pd-stat-item">
            <button 
              @click="toggleLike" 
              class="pd-like-button"
              :class="{ 'pd-liked': isLiked }"
            >
              <span class="pd-like-icon">{{ isLiked ? '❤️' : '🤍' }}</span>
              <span>{{ likes }} Likes</span>
            </button>
          </div>
          <div class="pd-stat-item">
            <span class="pd-stat-icon">💬</span>
            <span>{{ comments.length }} Comments</span>
          </div>
        </div>



      </div>

      <!-- Comments Section -->
      <div class="pd-comments-section">
      <h4 class="pd-comments-title">Comments</h4>

      <div v-if="displayedComments.length" class="pd-comments-list">
        <div v-for="comment in displayedComments" :key="comment.id" class="pd-comment">
          <img 
            v-if="comment.account.profileImage" 
            :src="comment.account.profileImage" 
            alt="Commenter" 
            class="pd-commenter-image"
          />
          <div class="pd-comment-content">
            <p class="pd-commenter-name">{{ comment.account.userName }}</p>
            <p class="pd-comment-text">{{ comment.text }}</p>
            <p class="pd-comment-time">{{ formatDate(comment.creationTime) }}</p>
          </div>
        </div>
      </div>

      <p v-else class="pd-no-comments">No comments yet</p>

      <!-- Show more button -->
      <div v-if="displayedComments.length < comments.length" class="pd-show-more-comments">
        <button @click="loadMoreComments" class="pd-load-more-button">Show more comments</button>
      </div>

      <!-- New Comment Form -->
      <div class="pd-new-comment" v-if="canComment">
        <textarea 
          v-model="newCommentText" 
          placeholder="Add a comment..." 
          class="pd-new-comment-input"
        ></textarea>
        <button @click="addComment" class="pd-add-comment-button">Post Comment</button>
          </div>
            <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
          </div>
        </div>

    <!-- Loading State -->
    <div v-else class="pd-loading-state">
      <div class="pd-loading-spinner"></div>
      <p>Loading post details...</p>
    </div>

    <div v-if="isPopupVisible" class="popup-overlay">
      <div class="popup-content">
        <p>{{ popupMessage }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'PostDetails',
  
  data() {
    return {
      post: null,
      likes: 0,
      comments: [],
      loggedInUserId: 0,
      editingPost: null,
      newImage: null,
      imagePreview: null,
      isLiked: false,
      newCommentText: "",
      errorMessage: "",
      displayedComments: [],
      canComment: false,
      loggedInUserRole: "",
      isPopupVisible: false, 
      popupMessage: "",
    };
  },
  
  mounted() {
    this.fetchPostDetails()
      .then(() => this.fetchPostLikes())  // Nakon fetchPostDetails, poziva se fetchPostLikes
      .then(() => this.fetchComments())   // Nakon fetchPostLikes, poziva se fetchComments
      .then(() => this.loadUser())        // Nakon fetchComments, poziva se loadUser
      .then(() => this.checkIfLiked())    // Na kraju, poziva se checkIfLiked
      .then(() => this.canCommentOnPost())
    .catch(error => {
      console.error("An error occurred while fetching data: ", error);
    });
  },

  methods: {
    async publishPost(){
      if (!this.loggedInUserId) return;

      try {
        await axios.post(
            `http://localhost:8081/api/posts/publish`, this.post
          );

        this.popupMessage = "Post has been successfully published!";
        this.isPopupVisible = true;  // Prikazuje popup

        setTimeout(() => {
          this.isPopupVisible = false;
        }, 3000);


      } catch (error) {
        console.error('Error publish:', error);
        this.popupMessage = "There was an error publishing the post.";
        this.isPopupVisible = true;  // Prikazuje popup

        setTimeout(() => {
          this.isPopupVisible = false;
        }, 3000);
        }
    },

    loadMoreComments() {
      const nextComments = this.comments.slice(this.displayedComments.length, this.displayedComments.length + 5);
      this.displayedComments = [...this.displayedComments, ...nextComments];
    },
    async checkIfLiked() {
      if (!this.loggedInUserId) return;
      
      try {
        const response = await axios.get(
          `http://localhost:8081/api/posts/hasLiked/${this.$route.params.postId}/${this.loggedInUserId}`
        );
        this.isLiked = response.data;
      } catch (error) {
        console.error('Error checking like status:', error);
      }
    },
    async toggleLike() {
      if (!this.loggedInUserId) {
        // Redirect to login or show login prompt
        this.$router.push({ name: 'Login' });
        return;
      }
      try {
        if (this.isLiked) {
          await axios.delete(
            `http://localhost:8081/api/posts/dislike/${this.$route.params.postId}/${this.loggedInUserId}`
          );
          this.fetchPostLikes()
        } else {
          await axios.post(
            `http://localhost:8081/api/posts/like/${this.$route.params.postId}/${this.loggedInUserId}`
          );
          this.fetchPostLikes()
        }
        this.isLiked = !this.isLiked;
      } catch (error) {
        console.error('Error toggling like:', error);
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
    },
    async fetchPostDetails() {
      try {
        const response = await axios.get(
          `http://localhost:8081/api/posts/findById/${this.$route.params.postId}`
        );
        this.post = response.data;
        await this.fetchPostFile();
      } catch (error) {
        console.error('Error fetching post details:', error);
      }
    },
    navigateToUser(userId) {
      this.$router.push({ name: 'CheckUser', params: { userId } });
    },
    async fetchPostFile() {
      try {
        const response = await axios.get(
          `http://localhost:8081/api/posts/getFile/${this.$route.params.postId}`
        );
        
        let imagePath = response.data.imagePath;
        let compress = response.data.compress;

        imagePath = imagePath.replace(/\\/g, '/');
        this.post.imageUrl = compress === 1
          ? `http://localhost:8081/compressedImages/${imagePath}`
          : `http://localhost:8081/images/${imagePath}`;
      } catch (error) {
        console.error('Error fetching file:', error);
      }
    },

    async fetchPostLikes() {
      try {
        const response = await axios.get(
          `http://localhost:8081/api/posts/likes/count/${this.$route.params.postId}`
        );
        this.likes = response.data;
      } catch (error) {
        console.error('Error fetching likes:', error);
      }
    },
    async canCommentOnPost(){
        const response = await axios.get(
          `http://localhost:8081/api/posts/canComment/${this.$route.params.postId}/${this.loggedInUserId}`)
        if(response.data){
          this.canComment=true;
        }
        else{
          this.canComment=false;
        }
    },
    async addComment() {
      if (!this.newCommentText.trim()) {
        return; 
      }
      this.errorMessage = ''
      try {
        const formData = new FormData();
        formData.append("text", this.newCommentText);


        const response = await axios.post(
          `http://localhost:8081/api/posts/comment/${this.$route.params.postId}/${this.loggedInUserId}`,
          formData);
        if (response.status === 200) {
          this.newCommentText = "";
          this.fetchComments()
        }

      } catch (error) {
        // if (error.response && error.response.status === 400) {
        //     this.errorMessage = "You can post up to 60 comments per hour.";
        //     this.newCommentText = "";
        // } else {
        //     this.errorMessage = "An error occurred while posting the comment.";
        // }
        // console.error('Error posting comment:', error);
        if (error.response && error.response.status === 400) {
    const errorMessage = error.response.data;

        if (errorMessage === "comment_limit_reached") {
            this.errorMessage = "You can post up to 60 comments per hour.";
        } else if (errorMessage === "too_many_requests") {
            this.errorMessage = "Too many requests. Please wait before commenting again.";
        } else {
            this.errorMessage = "An error occurred while posting the comment.";
        }
        this.newCommentText = "";
    } else {
        this.errorMessage = "An error occurred while posting the comment.";
    }
      }
    },
    async fetchComments() {
      try {
        const response = await axios.get(
          `http://localhost:8081/api/posts/comments/${this.$route.params.postId}`
        );
        
        this.comments = response.data.sort((a, b) => new Date(b.creationTime) - new Date(a.creationTime));

        this.displayedComments = this.comments.slice(0, 5);

      } catch (error) {
        console.error('Error fetching comments:', error);
        this.comments = [];
      }
    },
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

    handleImageChange(event) {
      const selectedFile = event.target.files[0];
      if (selectedFile) {
        this.newImage = selectedFile;
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
          await this.fetchPostDetails();
          this.cancelEdit();
        }
      } catch (error) {
        console.error('Error updating post:', error);
      }
    },

    async deletePost(postId) {
      try {
        const response = await axios.delete(`http://localhost:8081/api/posts/delete/${postId}`);
        if (response.data) {
          // Navigate back to posts list after deletion
          this.$router.push({ name: 'Posts' });
        }
      } catch (error) {
        console.error('Error deleting post:', error);
      }
    },

    formatDate(date) {
      const options = { 
        year: 'numeric', 
        month: 'short', 
        day: 'numeric', 
        hour: '2-digit', 
        minute: '2-digit' 
      };
      return new Date(date).toLocaleDateString(undefined, options);
    },

    formatCoordinate(coord) {
      return coord.toFixed(6);
    },

    getGoogleMapsLink(lat, lng) {
      return `https://www.google.com/maps/search/?api=1&query=${lat},${lng}`;
    }
  }
};
</script>

<style scoped>
.pd-container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.pd-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* Header Styles */
.pd-header {
  padding: 1rem;
  border-bottom: 1px solid #eee;
}

.pd-user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.pd-profile-image {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.pd-user-details {
  flex: 1;
}

.pd-username {
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.pd-post-time {
  color: #666;
  font-size: 0.875rem;
  margin: 0;
}

/* Image Styles */
.pd-image-container {
  position: relative;
  background: #f8f8f8;
}

.pd-post-image {
  width: 100%;
  max-height: 600px;
  object-fit: contain;
}

/* Content Styles */
.pd-content {
  padding: 1.5rem;
}

.pd-description {
  font-size: 1rem;
  line-height: 1.5;
  color: #333;
  margin-bottom: 1.5rem;
}

/* Location Styles */
.pd-location-info {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pd-coordinates {
  font-size: 0.875rem;
  color: #555;
}

.pd-label {
  font-weight: 600;
  margin-right: 0.5rem;
}

.pd-map-link {
  color: #2563eb;
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  background: #e0e7ff;
  transition: all 0.2s ease;
}

.pd-map-link:hover {
  background: #c7d2fe;
}

/* Engagement Stats */
.pd-engagement-stats {
  display: flex;
  gap: 2rem;
  padding: 1rem 0;
  border-top: 1px solid #eee;
}

.pd-stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
}

.pd-stat-icon {
  font-size: 1.25rem;
}

/* Comments Section */
.pd-comments-section {
  border-top: 1px solid #eee;
  padding: 1.5rem;
}

.pd-comments-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #1a1a1a;
}

.pd-comments-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.pd-comment {
  display: flex;
  gap: 1rem;
}

.pd-commenter-image {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.pd-comment-content {
  flex: 1;
}

.pd-commenter-name {
  font-weight: 600;
  margin: 0 0 0.25rem 0;
  color: #1a1a1a;
}

.pd-comment-text {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.pd-comment-time {
  font-size: 0.75rem;
  color: #666;
  margin: 0;
}

.pd-no-comments {
  text-align: center;
  color: #666;
  padding: 2rem 0;
}

/* Loading State */
.pd-loading-state {
  text-align: center;
  padding: 4rem 0;
  color: #666;
}

.pd-loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #3498db;
  border-radius: 50%;
  margin: 0 auto 1rem;
  animation: pdSpin 1s linear infinite;
}

@keyframes pdSpin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Responsive Design */
@media (max-width: 640px) {
  .pd-container {
    margin: 0;
    padding: 0;
  }
  
  .pd-card {
    border-radius: 0;
  }

  .pd-location-info {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }

  .pd-engagement-stats {
    justify-content: space-around;
  }
}

.pd-post-actions {
  margin-left: auto;
  display: flex;
  gap: 0.5rem;
}

.pd-action-button {
  background: none;
  border: none;
  font-size: 1.25rem;
  padding: 0.5rem;
  cursor: pointer;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.pd-action-button:hover {
  background-color: #f3f4f6;
}

.pd-edit-form {
  padding: 1.5rem;
  background-color: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.pd-edit-input {
  width: 100%;
  min-height: 100px;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  margin-bottom: 1rem;
  font-size: 1rem;
  resize: vertical;
}

.pd-edit-image-container {
  margin-bottom: 1rem;
}

.pd-edit-file-input {
  margin-bottom: 1rem;
}

.pd-image-preview {
  max-width: 100%;
  max-height: 300px;
  object-fit: contain;
  margin-top: 1rem;
  border-radius: 8px;
}

.pd-edit-buttons {
  display: flex;
  gap: 1rem;
}

.pd-save-button,
.pd-cancel-button {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.pd-save-button {
  background-color: #2563eb;
  color: white;
  border: none;
}

.pd-save-button:hover {
  background-color: #1d4ed8;
}

.pd-cancel-button {
  background-color: #fff;
  color: #4b5563;
  border: 1px solid #d1d5db;
}

.pd-cancel-button:hover {
  background-color: #f3f4f6;
}
.pd-new-comment {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.pd-new-comment-input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.pd-add-comment-button {
  padding: 0.5rem 1rem;
  background-color: #e56b6b;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.pd-add-comment-button:hover {
  background-color: #d75555;
}

.error-message{
  color: red;
}
.pd-show-more-comments {
  text-align: center;
  margin-top: 1rem;
}

.pd-load-more-button {
  background-color: #e56b6b;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
}

.pd-load-more-button:hover {
  background-color: #d75555;
}

.popup-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); 
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.5s ease-out;
}

.popup-content {
  background-color: #fff;
  border-radius: 5px;
  padding: 20px;
  text-align: center;
  width: 300px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  animation: slideIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  0% { opacity: 0; }
  100% { opacity: 1; }
}

@keyframes slideIn {
  0% { transform: translateY(-50px); opacity: 0; }
  100% { transform: translateY(0); opacity: 1; }
}
</style>