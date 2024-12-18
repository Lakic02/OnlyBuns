<template>
  <div>
    <h1 class="chat-title">My Chats</h1>
    <button @click="modalVisible" class="create-chat-button">Create New Chat</button>
    <div v-if="chats.length === 0" class="no-chats-message">
      No chats available.
    </div>
    <div v-else class="chat-container">
      <div v-for="chat in chats" :key="chat.id" class="chat-card" @click="navigateToChatDetails(chat.id)">
        <div class="chat-header">
          <h3>{{ chat.roomName }}</h3>
          <p class="chat-participants">{{ chat.participantsCount }} ... participants</p>
        </div>
        <div class="chat-footer">
          <span>{{ chat.lastMessageTime ? formatDate(chat.lastMessageTime) : 'No messages yet' }}</span>
        </div>
      </div>
    </div>

    <div class="chat-modal-overlay" v-if="isChatModalVisible">
    <div class="chat-modal-content">
      <h2>Create New Chat</h2>
      <input v-model="chatName" type="text" placeholder="Enter chat name" class="chat-input" />
      <div class="chat-modal-actions">
        <button @click="createNewChat" class="chat-accept-button">Accept</button>
        <button @click="close" class="chat-decline-button">Decline</button>
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
      loggedInUserId:0,
      chats: [],
      chatName: '', 
      isChatModalVisible: false,
    };
  },
  mounted() {
    this.loadLoggedInUser()
      .then(() => this.fetchChats())
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
    // Metoda za navigaciju do detalja chata
    navigateToChatDetails(roomId) {
      this.$router.push({ name: 'ChatRoom', params: { roomId } });
    },

    // Metoda za formatiranje vremena
    formatDate(date) {
      const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
      return new Date(date).toLocaleDateString(undefined, options);
    },

    // Metoda za preuzimanje chatova iz baze
    async fetchChats() {
      try {
        const response = await axios.get(`http://localhost:8081/api/chat/getChatRooms/${this.loggedInUserId}`);
        this.chats = response.data;
      } catch (error) {
        console.error("There was an error fetching chats:", error);
      }
    },
    // Metoda za kreiranje novog chata
    async createNewChat() {
      // Ovde bi trebalo otvoriti formu za kreiranje novog chata, ili samo pozvati API da bi se chat kreirao
      try {
        const response = await axios.post(`http://localhost:8081/api/chat/createRoom/${this.chatName}/${this.loggedInUserId}`)
        this.navigateToChatDetails(response.data.id)
      } catch (error) {
        console.error('Error creating chat:', error);
      }
    },
    modalVisible(){
      this.isChatModalVisible = true
    },
    close(){
      this.isChatModalVisible = false
    }
  },
};
</script>

<style scoped>
.chat-title {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

.create-chat-button {
  display: block;
  margin: 20px auto;
  padding: 10px 20px;
  background-color: #1da1f2;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.create-chat-button:hover {
  background-color: #1991da;
}

.chat-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  justify-content: center;
}

.chat-card {
  background-color: #fff;
  border: 1px solid #e1e8ed;
  border-radius: 8px;
  padding: 1rem;
  width: 280px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.chat-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: scale(1.05);
}

.chat-header h3 {
  font-size: 1.2rem;
  margin: 0;
}

.chat-participants {
  font-size: 0.9rem;
  color: #657786;
}

.chat-footer {
  margin-top: 10px;
  font-size: 0.8rem;
  color: #657786;
}

.no-chats-message {
  text-align: center;
  font-size: 1.2rem;
  color: #657786;
}

.chat-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.chat-modal-content {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  width: 300px;
  text-align: center;
}

.chat-input {
  width: 100%;
  padding: 10px;
  margin-top: 15px;
  margin-bottom: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.chat-modal-actions button {
  margin: 0 10px;
}

.chat-accept-button {
  background-color: #1da1f2;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.chat-accept-button:hover {
  background-color: #1991da;
}

.chat-decline-button {
  background-color: #e0e0e0;
  color: #333;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.chat-decline-button:hover {
  background-color: #c6c6c6;
}
</style>
