<template>
  <div class="user-profile-container">
    <div class="user-profile-card" v-if="user">
      <div class="user-info-container">
        <div class="user-avatar"></div>
        <div class="user-name-email">
          <h2 class="user-full-name">{{ user.userName }}</h2>
          <h2 class="user-full-name">{{ user.firstName }} {{ user.lastName }}</h2>
          <p class="user-email">{{ user.email }}</p>
        </div>
      </div>
      <div class="user-stats-actions">
        <div class="user-stats">
          <div class="user-followers-container">
            <span class="user-followers-count" @click="showFollowersModal"
                  style="cursor: pointer;">Followers: {{ user.followerCount }}</span>
          </div>
        </div>
        <div class="user-actions">
          <button v-if="!isFollowing" class="follow-button" @click="followUser" :disabled="isProcessing">
            Follow
          </button>
          <button v-if="isFollowing" class="unfollow-button" @click="unfollowUser" :disabled="isProcessing">
            Unfollow
          </button>
        </div>
      </div>
      <p v-if="isProcessing" class="processing-message">Processing...</p>
    </div>
  </div>

  <!-- Modal for displaying followers -->
  <div v-if="isFollowersModalVisible" class="followers-modal-overlay" @click="closeFollowersModal">
    <div class="followers-modal" @click.stop>
      <h3>Followers</h3>
      <ul>
        <li v-for="follower in followers" :key="follower.id" 
            @click="navigateToUser(follower.id)" style="cursor: pointer;">
          <span class="username">{{ follower.userName }}</span>
          <span class="name">{{ follower.firstName }} {{ follower.lastName }}</span>
        </li>
      </ul>
      <button @click="closeFollowersModal">Close</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  watch: {
    '$route.params.userId': 'fetchUser',
  },
  data() {
    return {
      user: null,
      isFollowing: false,
      isProcessing: false,
      loggedInUserId: 0,
      followers: [],
      isFollowersModalVisible: false
    };
  },
  created() {
    this.loadLoggedInUser()
      .then(() => this.fetchUser())
    .catch(error => {
      console.error("An error occurred while fetching data: ", error);
    });
  },
  methods: {
    async loadLoggedInUser() {
      const token = localStorage.getItem('token');
      if (token) {
        try {
          const response = await axios.post("http://localhost:8081/api/authentication/jwt/decode", { token });
          if (response.status === 200) {
            const { id, username, role } = response.data;
            this.loggedInUserId = id;
            //console.log(this.loggedInUserId)
          }
        } catch (error) {
          console.error('Failed to decode token:', error);
        }
      } else {
        this.loggedInUserId = 0;
      }
    },
    async fetchUser() {
      try {
        const response = await axios.get(`http://localhost:8081/api/accounts/getById/${this.$route.params.userId}`);
        this.user = response.data;
        console.log(this.user)
        this.checkIfFollowing(this.$route.params.userId);
      } catch (error) {
        console.error("Error fetching user:", error);
      }
    },
    async checkIfFollowing(userId) {
      try {
        const response = await axios.get(`http://localhost:8081/api/follow/status/${this.loggedInUserId}/${userId}`);
        this.isFollowing = response.data;
      } catch (error) {
        console.error("Error checking follow status:", error);
      }
    },
    async followUser() {
      this.isProcessing = true;
      try {
        const followedId = this.user.id;
        await axios.post(`http://localhost:8081/api/follow/${this.loggedInUserId}/${followedId}`);
        this.isFollowing = true;
        this.user.followerCount++;
        this.isProcessing = false;
      } catch (error) {
        this.isProcessing = false;
        console.error("Error following user:", error);
      }
    },
    async unfollowUser() {
      this.isProcessing = true;
      try {
        const followedId = this.user.id;
        await axios.delete(`http://localhost:8081/api/follow/${this.loggedInUserId}/${followedId}`);
        this.isFollowing = false;
        this.user.followerCount--;
        this.isProcessing = false;
      } catch (error) {
        this.isProcessing = false;
        console.error("Error unfollowing user:", error);
      }
    },
    showFollowersModal() {
      this.isFollowersModalVisible = true;
      this.fetchFollowers();
    },
    async fetchFollowers() {
      try {
        const response = await axios.get(`http://localhost:8081/api/follow/followers/${this.user.id}`);
        this.followers = response.data;
      } catch (error) {
        console.error("Error fetching followers:", error);
      }
    },
    closeFollowersModal() {
      this.isFollowersModalVisible = false;
    },
    navigateToUser(userId) {
      this.$router.push({ name: 'CheckUser', params: { userId } });
      this.closeFollowersModal()
    },
  },
};
</script>

<style>
.user-profile-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 2rem 0;
}

.user-profile-card {
  background-color: #fff;
  border-radius: 0.5rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  padding: 1.5rem;
  width: 100%;
  max-width: 40rem;
}

.user-info-container {
  display: flex;
  align-items: center;
  margin-bottom: 1.5rem;
}

.user-avatar {
  width: 4rem;
  height: 4rem;
  border-radius: 50%;
  background-color: #e5e7eb;
  margin-right: 1rem;
}

.user-name-email {
  flex-grow: 1;
}

.user-full-name {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.user-email {
  color: #6b7280;
}

.user-stats-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-stats {
  display: flex;
  gap: 1rem;
}

.user-followers-container,
.user-posts-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #6b7280;
}

.user-followers-icon,
.user-posts-icon {
  font-size: 1.125rem;
}

.user-followers-count,
.user-posts-count {
  font-size: 1rem;
}

.follow-button {
  background-color: #3b82f6;
  color: #fff;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  transition: background-color 0.2s ease-in-out;
}

.follow-button:hover {
  background-color: #2563eb;
}

.follow-button:disabled {
  background-color: #9ca3af;
  cursor: not-allowed;
}

.unfollow-button {
  background-color: #ef4444;
  color: #fff;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  font-size: 0.875rem;
  transition: background-color 0.2s ease-in-out;
}

.unfollow-button:hover {
  background-color: #dc2626;
}

.unfollow-button:disabled {
  background-color: #9ca3af;
  cursor: not-allowed;
}

.processing-message {
  margin-top: 1rem;
  color: #6b7280;
}

/* Style for the modal */
.followers-modal-overlay {
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
}

.followers-modal {
  background-color: white;
  padding: 2rem;
  border-radius: 8px;
  width: 80%;
  max-width: 600px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.followers-modal h3 {
  margin-bottom: 1.5rem;
}

.followers-modal ul {
  list-style-type: none;
  padding: 0;
}

.followers-modal ul li {
  margin-bottom: 1rem;
  font-size: 1.1rem;
  border: 1px solid #e5e7eb;
  border-radius: 0.375rem;
  padding: 0.75rem 1rem;
}

.followers-modal ul li .username {
  font-weight: 600;
  margin-right: 10px;
}

.followers-modal ul li .name {
  font-size: 0.9rem;
  color: #6b7280;
}

.followers-modal button {
  background-color: #3b82f6;
  color: white;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
}

.followers-modal button:hover {
  background-color: #2563eb;
}
</style>