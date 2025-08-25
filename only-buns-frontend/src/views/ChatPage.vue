<template>
  <div class="chat-layout">
    <!-- Sidebar for chat rooms and creating new ones -->
    <div class="sidebar">
      <h3>My Chats</h3>
      <div class="start-chat-form">
        <select v-model="privateChatUserId" class="user-select">
          <option :value="null" disabled>Start chat with...</option>
          <option v-for="user in availablePrivateChatUsers" :key="user.id" :value="user.id">
            {{ user.username }}
          </option>
        </select>
        <button @click="startPrivateChat" :disabled="!privateChatUserId">Chat</button>
      </div>

      <hr>

      <div class="create-room-form">
        <input v-model="newRoomName" @keyup.enter="createRoom" placeholder="New group name" />
        <button @click="createRoom" :disabled="!newRoomName.trim()">Create Group</button>
      </div>
      
      <ul class="room-list">
        <li v-for="room in chatRooms" 
            :key="room.id" 
            @click="selectRoom(room)"
            :class="{ 'active-room': room.id === selectedRoomId }">
          <span v-if="!room.groupChat">{{ getOtherParticipantName(room) }}</span>
          <span v-else>{{ room.name }}</span>
        </li>
      </ul>
    </div>

    <!-- Main chat area -->
    <div class="main-chat-area">
      <div v-if="selectedRoomId" class="chat-container">
        <div class="room-management" v-if="isRoomAdmin && roomDetails.groupChat">
          <div class="management-header">
            <h4>Manage Room: "{{ roomDetails.name }}"</h4>
            <div class="add-user-form">
              <select v-model="userToAddId" class="user-select">
                <option :value="null" disabled>Select a user to add</option>
                <option v-for="user in availableUsersToAdd" :key="user.id" :value="user.id">
                  {{ user.username }}
                </option>
              </select>
              <button @click="addUser" :disabled="!userToAddId">Add</button>
            </div>
          </div>
          <div v-if="roomDetails.participants && roomDetails.participants.length > 0" class="participants-container">
            <h5>Participants:</h5>
            <ul class="participants-list">
              <li v-for="p in roomDetails.participants" :key="p.id">
                <span>{{ p.username }} <em v-if="roomDetails.admin && p.id === roomDetails.admin.id">(Admin)</em></span>
                <button v-if="roomDetails.admin && p.id !== roomDetails.admin.id" @click="removeUser(p.id)" class="remove-btn">Remove</button>
              </li>
            </ul>
          </div>
        </div>

        <div class="chat-window">
          <div class="messages-area" ref="messagesArea">
            <div v-if="loadingMessages" class="loading">Loading messages...</div>
            <div v-for="msg in messages" :key="msg.id" class="message" :class="{ 'my-message': isMyMessage(msg) }">
              <div class="message-sender" v-if="!isMyMessage(msg)">{{ msg.sender.username }}</div>
              <div class="message-content">{{ msg.content }}</div>
              <div class="message-time">{{ formatTime(msg.sentAt) }}</div>
            </div>
          </div>
        </div>
        
        <div class="input-area">
          <input type="text" v-model="newMessage" @keyup.enter="sendMessage" placeholder="Type a message..." class="message-input" :disabled="!isConnected" />
          <button @click="sendMessage" class="send-button" :disabled="!isConnected || !newMessage.trim()">Send</button>
        </div>
        <div v-if="!isConnected" class="connection-status">Connecting to chat...</div>

      </div>
      <div v-else class="no-room-selected">
        <p>Select a chat from the list or start a new one.</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { jwtDecode } from "jwt-decode";

export default {
  data() {
    return {
      messages: [], newMessage: '', stompClient: null, isConnected: false, loadingMessages: true, currentUser: null, chatRooms: [], selectedRoomId: null, newRoomName: '', roomDetails: {},
      userToAddId: null,
      privateChatUserId: null,
      availableUsers: [],
      currentSubscription: null,
    };
  },
  computed: {
    isRoomAdmin() {
      if (!this.currentUser || !this.roomDetails || !this.roomDetails.admin) return false;
      return this.currentUser.id === this.roomDetails.admin.id;
    },
    availableUsersToAdd() {
      if (!this.roomDetails.participants) return [];
      const participantIds = this.roomDetails.participants.map(p => p.id);
      return this.availableUsers.filter(user => !participantIds.includes(user.id));
    },
    availablePrivateChatUsers() {
      if (!this.currentUser) return [];
      return this.availableUsers.filter(user => user.id !== this.currentUser.id);
    }
  },
  async created() {
    const isUserLoaded = await this.getCurrentUserAndDetails();
    if (isUserLoaded) {
      await this.fetchAvailableUsers();
      await this.fetchUserChatRooms();
      this.connect();
    } else {
      this.$router.push('/login');
    }
  },
  beforeUnmount() {
    if (this.stompClient && this.stompClient.connected) this.stompClient.deactivate();
  },
  methods: {
    async getCurrentUserAndDetails() {
      const token = localStorage.getItem("token");
      if (!token) return false;
      try {
        const decoded = jwtDecode(token);
        const userEmail = decoded.sub;
        if (!userEmail) throw new Error("Email not in token.");
        const response = await axios.get(`http://localhost:8080/api/users/my-profile/${userEmail}`, { headers: { Authorization: `Bearer ${token}` } });
        this.currentUser = response.data;
        return !!this.currentUser.id;
      } catch (e) {
        localStorage.removeItem("token");
        return false;
      }
    },
    async fetchUserChatRooms() {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get('http://localhost:8080/api/chat-rooms', { headers: { Authorization: `Bearer ${token}` } });
        this.chatRooms = response.data;
        if (this.chatRooms.length > 0 && !this.selectedRoomId) {
          await this.selectRoom(this.chatRooms[0]);
        } else if (this.chatRooms.length === 0) {
          this.selectedRoomId = null;
        }
      } catch (error) { console.error("Error fetching rooms:", error); }
    },
    async fetchAvailableUsers() {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get('http://localhost:8080/api/users/chat-buddies', { headers: { Authorization: `Bearer ${token}` } });
        this.availableUsers = response.data;
      } catch (error) { console.error("Error fetching available users:", error); }
    },
    async selectRoom(room) {
      if (!room || this.selectedRoomId === room.id) return;
      this.selectedRoomId = room.id;
      this.messages = [];
      this.loadingMessages = true;
      this.roomDetails = {};
      if (this.currentSubscription) this.currentSubscription.unsubscribe();
      if (this.isConnected) this.subscribeToRoom();
      await this.fetchRoomDetails();
      await this.fetchInitialMessages();
    },
    subscribeToRoom() {
      if (!this.selectedRoomId || !this.stompClient?.connected) return;
      this.currentSubscription = this.stompClient.subscribe(`/topic/room/${this.selectedRoomId}`, (message) => {
        this.messages.push(JSON.parse(message.body));
        this.scrollToBottom();
      });
    },
    async createRoom() {
      if (!this.newRoomName.trim()) return;
      try {
        const token = localStorage.getItem("token");
        const response = await axios.post('http://localhost:8080/api/chat-rooms', { name: this.newRoomName }, { headers: { Authorization: `Bearer ${token}` } });
        this.newRoomName = '';
        
        // ISPRAVKA: Dodajemo novu sobu u listu pre selektovanja
        this.chatRooms.push(response.data);
        await this.selectRoom(response.data);
        
      } catch (error) { console.error("Error creating room:", error); }
    },
    // u ChatPage.vue -> methods

async startPrivateChat() {
  if (!this.privateChatUserId) {
    alert("Please select a user from the list.");
    return;
  }
  
  try {
    const targetUser = this.availableUsers.find(u => u.id === this.privateChatUserId);
    
    if (!targetUser || !targetUser.username) {
      alert("Could not find the selected user. Please try again.");
      return;
    }
    
    // Kreiramo payload objekat koji šaljemo
    const payload = { username: targetUser.username };
    
    // Logujemo tačno ono što šaljemo
    console.log("[DIJAGNOSTIKA] Šaljem POST na /private-chat sa payloadom:", JSON.stringify(payload));
    
    const token = localStorage.getItem("token");
    const response = await axios.post(
      'http://localhost:8080/api/chat-rooms/private-chat', 
      payload, // Šaljemo payload
      { headers: { Authorization: `Bearer ${token}` } }
    );
    
    this.privateChatUserId = null;
    await this.fetchUserChatRooms();
    await this.selectRoom(response.data);
    
  } catch (error) {
    console.error("Greška pri započinjanju privatnog četa:", error.response || error);
    alert("User not found or an error occurred. Check console for details.");
  }
},
    connect() {
      const token = localStorage.getItem("token");
      if (!token) return;
      const socket = new SockJS('http://localhost:8080/ws');
      this.stompClient = new Client({
        webSocketFactory: () => socket,
        connectHeaders: { Authorization: `Bearer ${token}` },
        onConnect: () => {
          this.isConnected = true;
          if (this.selectedRoomId) this.subscribeToRoom();
        },
        onStompError: () => { this.isConnected = false; },
        onWebSocketClose: () => { this.isConnected = false; }
      });
      this.stompClient.activate();
    },
    sendMessage() {
      if (this.newMessage.trim() && this.stompClient?.connected && this.selectedRoomId) {
        const chatMessageDto = { content: this.newMessage, chatRoomId: this.selectedRoomId };
        this.stompClient.publish({ destination: '/app/chat.sendMessage', body: JSON.stringify(chatMessageDto) });
        this.newMessage = '';
      }
    },
    async fetchInitialMessages() {
      if (!this.selectedRoomId) return;
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(`http://localhost:8080/api/chat-rooms/${this.selectedRoomId}/messages`, { headers: { Authorization: `Bearer ${token}` } });
        this.messages = response.data;
      } catch (error) { this.messages = []; }
      finally {
        this.loadingMessages = false;
        this.scrollToBottom();
      }
    },
    async fetchRoomDetails() {
      if (!this.selectedRoomId) return;
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(`http://localhost:8080/api/chat-rooms/${this.selectedRoomId}`, { headers: { Authorization: `Bearer ${token}` } });
        this.roomDetails = response.data;
      } catch (error) { console.error("Error fetching room details:", error); }
    },
    async addUser() {
      if (!this.userToAddId) return;
      try {
        const token = localStorage.getItem("token");
        await axios.post(`http://localhost:8080/api/chat-rooms/${this.selectedRoomId}/participants`, { userIdToAdd: this.userToAddId }, { headers: { Authorization: `Bearer ${token}` } });
        alert('User successfully added!');
        this.userToAddId = null;
        this.fetchRoomDetails();
      } catch (error) {
        alert('Error: ' + (error.response?.data || 'This user might already be in the room.'));
      }
    },
    async removeUser(userIdToRemove) {
      if (!confirm('Are you sure?')) return;
      try {
        const token = localStorage.getItem("token");
        await axios.delete(`http://localhost:8080/api/chat-rooms/${this.selectedRoomId}/participants/${userIdToRemove}`, { headers: { Authorization: `Bearer ${token}` } });
        alert('User successfully removed!');
        this.fetchRoomDetails();
      } catch (error) { alert('An error occurred.'); }
    },
    // ISPRAVLJENA METODA
    getOtherParticipantName(room) {
      if (!this.currentUser || !room.participants || room.participants.length < 2) {
          // Ako nema drugog učesnika, to je verovatno novokreirana grupa
          // pa vraćamo njeno pravo ime.
          return room.name; 
      }
      const otherParticipant = room.participants.find(p => p.id !== this.currentUser.id);
      return otherParticipant ? otherParticipant.username : 'Private Chat';
    },
    isMyMessage(message) {
      return this.currentUser && message.sender && message.sender.id === this.currentUser.id;
    },
    formatTime(dateTimeString) {
      if (!dateTimeString) return '';
      return new Date(dateTimeString).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const area = this.$refs.messagesArea;
        if (area) area.scrollTop = area.scrollHeight;
      });
    }
  }
};
</script>

<style scoped>
/* Main Layout */
.chat-layout { display: flex; height: calc(100vh - 56px); font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; }
.sidebar { width: 280px; background-color: #f0f2f5; border-right: 1px solid #ddd; padding: 15px; display: flex; flex-direction: column; box-sizing: border-box; }
.sidebar h3 { margin-top: 0; color: #333; padding-bottom: 10px; border-bottom: 1px solid #ddd; }
.main-chat-area { flex-grow: 1; display: flex; flex-direction: column; background-color: #fff; }
.no-room-selected { display: flex; justify-content: center; align-items: center; width: 100%; height: 100%; color: #888; font-size: 1.2em; }
.chat-container { display: flex; flex-direction: column; width: 100%; height: 100%; }
.sidebar hr { margin: 15px 0; border: none; border-top: 1px solid #ddd; }

/* Sidebar Forms */
.start-chat-form, .create-room-form { display: flex; margin-bottom: 10px; }
.start-chat-form .user-select, .create-room-form input { flex-grow: 1; min-width: 0; margin-right: 10px; border-radius: 8px; border: 1px solid #ccc; padding: 8px 12px; }
.start-chat-form button, .create-room-form button { border-radius: 8px; border: none; color: white; padding: 8px 15px; cursor: pointer; font-weight: bold; white-space: nowrap; flex-shrink: 0; }
.start-chat-form button { background-color: #28a745; }
.create-room-form button { background-color: #6c757d; }
.start-chat-form button:disabled, .create-room-form button:disabled { background-color: #c0c0c0; }

/* Room List */
.room-list { list-style: none; padding: 0; margin: 0; overflow-y: auto; }
.room-list li { padding: 12px; cursor: pointer; border-radius: 8px; margin-bottom: 5px; font-weight: 500; color: #333; transition: background-color 0.2s; }
.room-list li:hover { background-color: #e4e6eb; }
.room-list li.active-room { background-color: #007bff; color: white; }

/* Room Management Panel */
.room-management { padding: 15px; border-bottom: 1px solid #ddd; background-color: #fafafa; }
.management-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px; }
.room-management h4, .room-management h5 { margin: 0 0 10px 0; }
.add-user-form { display: flex; margin-bottom: 15px; }
.add-user-form .user-select { flex-grow: 1; margin-right: 10px; padding: 8px; border-radius: 5px; border: 1px solid #ccc; }
.add-user-form button { padding: 8px 12px; border-radius: 5px; border: none; background-color: #28a745; color: white; cursor: pointer; }
.add-user-form button:disabled { background-color: #a0a0a0; }
.participants-container { margin-top: 15px; }
.participants-list { list-style: none; padding: 0; max-height: 120px; overflow-y: auto; }
.participants-list li { display: flex; justify-content: space-between; align-items: center; padding: 5px 0; }
.remove-btn { background-color: #e74c3c; color: white; border: none; border-radius: 4px; cursor: pointer; padding: 3px 8px; font-size: 0.8em; }

/* Chat Window & Messages */
.chat-window { flex-grow: 1; display: flex; flex-direction: column; overflow: hidden; }
.messages-area { flex-grow: 1; padding: 20px; overflow-y: auto; background-color: #ffffff; }
.message { display: flex; flex-direction: column; margin-bottom: 15px; max-width: 70%; clear: both; }
.message-sender { font-size: 0.8em; color: #888; margin-bottom: 4px; padding: 0 10px; }
.message-content { padding: 10px 15px; border-radius: 18px; background-color: #e4e6eb; color: #050505; word-wrap: break-word; }
.message-time { font-size: 0.75em; color: #999; margin-top: 4px; align-self: flex-start; }
.my-message { align-items: flex-end; float: right; }
.my-message .message-content { background-color: #0d6efd; color: white; }
.my-message .message-sender { display: none; }
.my-message .message-time { display: block; align-self: flex-end; }

/* Fixed Input Area */
.input-area { display: flex; padding: 15px; border-top: 1px solid #ddd; background-color: #f0f2f5; }
.message-input { flex-grow: 1; border: 1px solid #ccc; border-radius: 20px; padding: 10px 15px; font-size: 1em; }
.message-input:focus { outline: none; border-color: #007bff; }
.send-button { margin-left: 10px; padding: 10px 20px; border: none; background-color: #007bff; color: white; border-radius: 20px; cursor: pointer; font-weight: bold; }
.send-button:disabled { background-color: #a0a0a0; }

/* Statuses */
.connection-status, .loading { text-align: center; padding: 5px; color: #888; }

/* Scrollbar Style */
.room-list::-webkit-scrollbar, .participants-list::-webkit-scrollbar, .messages-area::-webkit-scrollbar { width: 5px; }
.room-list::-webkit-scrollbar-track, .participants-list::-webkit-scrollbar-track, .messages-area::-webkit-scrollbar-track { background: transparent; }
.room-list::-webkit-scrollbar-thumb, .participants-list::-webkit-scrollbar-thumb, .messages-area::-webkit-scrollbar-thumb { background-color: rgba(0,0,0,0.2); border-radius: 10px; }
.room-list, .participants-list, .messages-area { scrollbar-width: thin; scrollbar-color: rgba(0,0,0,0.2) transparent; }
</style>