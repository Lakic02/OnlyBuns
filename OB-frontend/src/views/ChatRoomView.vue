<template>
  <div class="chat-room-container">
    <h1 class="chat-room-title">Chat Room: {{ chatRoom.roomName }}</h1>

    <div class="buttons-container">
      <button v-if="isAdmin" @click="showAddUserModal" class="add-user-button">Add User</button>
      <button @click="showMembersModal" class="add-user-button">Show Members</button>
    </div>

    <!-- Chat Messages Section -->
    <div class="chat-messages-container">
      <div class="messages-list" ref="messagesList">
        <div v-for="message in messages" 
             :key="message.id" 
             :class="['message-item', message.sender.id === loggedInUserId ? 'message-mine' : 'message-others']">
          <div class="message-header">
            <span class="message-username">{{ message.sender.userName }}</span>
            <span class="message-time">{{ formatTime(message.timestamp) }}</span>
          </div>
          <div class="message-content">{{ message.content }}</div>
        </div>
      </div>
      
      <!-- Message Input Section -->
      <div class="message-input-container">
        <textarea v-model="newMessage" @keyup.enter="sendMessage" placeholder="Type your message..."
                  class="message-input"></textarea>
        <button @click="sendMessage" class="send-message-button">Send</button>
      </div>
    </div>

    <!-- Existing Modals -->
    <div class="chat-modal-overlay" v-if="isModalVisible">
      <div class="chat-modal-content">
        <h2>Add User to Chat</h2>
        <input v-model="userEmail" type="email" placeholder="Enter user email" class="chat-modal-input" />
        <div class="chat-modal-actions">
          <button @click="addUserToChat" class="chat-accept-button">Accept</button>
          <button @click="closeModal" class="chat-decline-button">Decline</button>
        </div>
      </div>
    </div>

    <div class="chat-modal-overlay" v-if="isMembersModalVisible">
      <div class="chat-modal-content">
        <h2>Chat Room Members</h2>
        <ul>
          <li v-for="user in chatRoomMemberships" :key="user.id">
            <span>{{ user.userName }}</span>
            <button @click="removeUserFromChat(user)" v-if="isAdmin" class="delete-member-button">Delete</button>
          </li>
        </ul>
        <div class="chat-modal-actions">
          <button @click="closeMembersModal" class="chat-decline-button">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { nextTick } from 'vue';

export default {
  data() {
    return {
      chatRoom: '',
      chatRoomMemberships: [],
      isModalVisible: false,
      isMembersModalVisible: false,
      userEmail: '',
      messages: [],
      newMessage: '',
      loggedInUserId: null,
      isAdmin: false
    };
  },
  mounted() {
    this.loadLoggedInUser()
      .then(() => this.fetchChatRoomData())
      .then(() => this.fetchMessages())
    .catch(error => {
      console.error("An error occurred while fetching data: ", error);
    });
    // Započni polling za nove poruke

    const socket = new SockJS('http://localhost:8081/ws');  // Putanja do vašeg WebSocket endpoint-a
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      onConnect: this.onConnect,
      onStompError: this.onStompError,
    });
    // Povezivanje sa WebSocket-om
    this.stompClient.activate();
  },
  beforeDestroy() {
    // Zatvaranje WebSocket konekcije kada komponenta bude uništena
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
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
    onConnect() {
      console.log('Connected to WebSocket server');
      console.log(this.stompClient);
      // Pretplaćivanje na chat sobu, koristi `chatRoomId` koji je prosleđen u URL-u
      console.log("this.$route.params.roomId")
      console.log(this.$route.params.roomId)
      this.stompClient.subscribe(`/topic/chatRoom/${this.$route.params.roomId}`, this.onMessageReceived);
    },
    onStompError(frame) {
      console.error('STOMP Error:', frame);
    },
    onMessageReceived(message) {
      // Kada poruka stigne u chat, dodaćemo je u listu poruka
      const receivedMessage = JSON.parse(message.body);
      this.messages.push(receivedMessage);
    },
    async sendMessage() {
      if (!this.newMessage.trim()) {
        console.log("Add new message first!");
        return;
      }
      // Ensure that the STOMP client is connected before sending the message
      if (!this.stompClient || !this.stompClient.connected || !this.stompClient.publish) {
        console.error("STOMP client is not connected.");
        return;
      }
      console.log('USLO JE U CHAT SLLANJE PORUKE')

      this.stompClient.publish({
        destination: `/app/chat.sendMessage/${this.$route.params.roomId}`, // Endpoint na serveru
        body: JSON.stringify({ 
          chatRoomId: this.$route.params.roomId, 
          content: this.newMessage.trim(),
          sender: { id: this.loggedInUserId }
        })
      });

      this.newMessage = ''; // Očisti polje za unos poruke
    },
    async fetchChatRoomData() {
      try {
        const response = await axios.get(`http://localhost:8081/api/chat/getChatRoom/${this.$route.params.roomId}`);
        this.chatRoom = response.data;
        console.log(this.chatRoom)
        if(this.loggedInUserId == this.chatRoom.admin.id)
          this.isAdmin = true
        else
          this.isAdmin = false

        const membershipsResponse = await axios.get(`http://localhost:8081/api/chat/getUsersInChatRoom/${this.$route.params.roomId}`);
        console.log(membershipsResponse.data);
        this.chatRoomMemberships = membershipsResponse.data;
      } catch (error) {
        console.error('Failed to fetch chat room data:', error);
      }
    },

    async fetchMessages() {
      try {
        //const response = await axios.get(`http://localhost:8081/api/chat/getMessages/${this.$route.params.roomId}`);
        //this.messages = response.data;
        //this.$nextTick(() => {
          //this.scrollToBottom();
        //});
        const response = await axios.get(
          `http://localhost:8081/api/chat/getMessagesForChatRoom/${this.$route.params.roomId}/${this.loggedInUserId}`
        );
        
        // Spajanje svih poruka u jednu promenljivu
        if (response.data.allMessages) {
          // Ako je korisnik admin, sve poruke su u 'allMessages'
          console.log('ADMINNNNNNN')
          this.messages = response.data.allMessages;
        } else {
          // Ako nije admin, spajamo poruke pre i posle ulaska
          console.log('MEMBERRRRR')
          this.messages = [...response.data.lastMessages, ...response.data.newMessages];
        }
      } catch (error) {
        console.error('Failed to fetch messages:', error);
      }
    },
    formatTime(timestamp) {
      return new Date(timestamp).toLocaleTimeString([], { 
        hour: '2-digit', 
        minute: '2-digit' 
      });
    },

    // scrollToBottom() {
    //   const messagesList = this.$refs.messagesList;
    //   messagesList.scrollTop = messagesList.scrollHeight;
    // },

    // Existing methods remain the same
    showAddUserModal() {
      this.isModalVisible = true;
    },

    closeModal() {
      this.isModalVisible = false;
      this.userEmail = '';
    },

    async addUserToChat() {
      if (!this.userEmail) {
        alert('Please enter a valid email!');
        return;
      }
      if (!this.stompClient || !this.stompClient.connected || !this.stompClient.publish) {
        console.error("STOMP client is not connected.");
        return;
      }
      console.log('DODAVANJE NOVOG USERA')

      await this.stompClient.publish({
        destination: `/app/chat.addUser/${this.$route.params.roomId}`, // Endpoint na serveru
        body: JSON.stringify({ 
          email: this.userEmail
        })
      });
      this.closeModal()
      this.fetchChatRoomData()


      // try {
      //   const response = await axios.post('http://localhost:8081/api/chat/addUserToChat', {
      //     chatRoomId: this.$route.params.chatRoomId,
      //     email: this.userEmail,
      //   });

      //   this.chatRoom.users.push(response.data);
      //   this.closeModal();
      // } catch (error) {
      //   console.error('Error adding user to chat:', error);
      // }
    },

    showMembersModal() {
      this.isMembersModalVisible = true;
    },

    closeMembersModal() {
      this.isMembersModalVisible = false;
    },

    async removeUserFromChat(user) {
      try {
        if (!this.stompClient || !this.stompClient.connected || !this.stompClient.publish) {
          console.error("STOMP client is not connected.");
          return;
        }
        console.log(user.email)

        //await axios.delete(`http://localhost:8081/api/chat/removeMembership/${this.$route.params.chatRoomId}/${this.loggedInUserId}`);
        await this.stompClient.publish({
          destination: `/app/chat.removeUser/${this.$route.params.roomId}`, // Endpoint na serveru
          body: JSON.stringify({ 
            email: user.email
          })
        });
        this.fetchChatRoomData()
      } catch (error) {
        console.error('Error removing user from chat:', error);
      }
    }
  }
};
</script>

<style>
.chat-room-container {
  padding: 20px;
  max-width: 800px;
  margin: auto;
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.buttons-container {
  margin-bottom: 20px;
}

.chat-messages-container {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
  height: calc(100vh - 200px);
}

.messages-list {
  flex-grow: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message-item {
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 8px;
  margin-bottom: 10px;
}

.message-mine {
  align-self: flex-end;
  background-color: #007bff;
  color: white;
}

.message-others {
  align-self: flex-start;
  background-color: #e9ecef;
  color: #212529;
}

.message-header {
  font-size: 0.8em;
  margin-bottom: 5px;
  display: flex;
  justify-content: space-between;
}

.message-username {
  font-weight: bold;
  margin-right: 3rem;
}

.message-time {
  opacity: 0.8;
}

.message-content {
  word-break: break-word;
}

.message-input-container {
  display: flex;
  gap: 10px;
  padding: 20px;
  background: white;
  border-top: 1px solid #dee2e6;
}

.message-input {
  flex-grow: 1;
  padding: 10px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  resize: none;
  height: 40px;
  font-family: inherit;
}

.send-message-button {
  padding: 0 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.send-message-button:hover {
  background-color: #0056b3;
}
.chat-room-container {
  padding: 20px;
  max-width: 800px;
  margin: auto;
}

.chat-room-title {
  text-align: center;
  color: #333;
}

.add-user-button, .show-members-button {
  display: inline-block;
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.add-user-button:hover, .show-members-button:hover {
  background-color: #218838;
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
.delete-member-button {
  background-color: #e53e3e;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.delete-member-button:hover {
  background-color: #c53030;
}
.user-list {
  margin-top: 30px;
}

.user-list ul {
  list-style-type: none;
  padding: 0;
}

.user-list li {
  background-color: #f7f7f7;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 4px;
}
</style>


