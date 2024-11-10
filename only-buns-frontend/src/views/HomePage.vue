<template>
  <div class="container mt-5">
    <div class="text-center mb-2">
      <img src="@/assets/bunny-logo.jpg" alt="Bunny Logo" class="logo" />
    </div>
    <h1 class="text-center">OnlyBuns</h1>

    <div class="text-center mb-4">
      <button class="btn btn-primary" @click="goToLogin">Login</button>
      <button class="btn btn-secondary" @click="goToRegister">Register</button>
    </div>

    <div v-if="posts.length > 0" class="row">
      <div v-for="post in sortedPosts" :key="post.id" class="col-md-4 mb-4">
        <div class="card">
          <img :src="post.photo" class="card-img-top post-image" alt="Post Image" v-if="post.photo" />
          <div class="card-body">
            <h5 class="card-title">
              <router-link :to="{ name: 'UserProfile', params: { userId: post.creatorId } }">
                {{ post.creatorUsername }}
              </router-link>
            </h5>
            <p>{{ post.description }}</p>
            <p class="card-text">Posted at: {{ formatDate(post.createdAt) }}</p>
          </div>
          <div class="icon-container">
            <i class="fas fa-thumbs-up icon" @click="showAlert('You must be logged in to like posts.')"></i>
            <i class="fas fa-comment icon" @click="showAlert('You must be logged in to comment.')"></i>
          </div>
        </div>
      </div>
    </div>
    <div v-else>
      <p>No posts available.</p>
    </div>

    <div v-if="alertMessage" class="error-alert">
      {{ alertMessage }}
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      posts: [],
      alertMessage: null,  
    };
  },
  computed: {
    sortedPosts() {
      return [...this.posts].sort(
        (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
      );
    },
  },
  methods: {
    async fetchPosts() {
      try {
        const response = await axios.get('http://localhost:8080/api/posts/all');
        this.posts = response.data;
      } catch (error) {
        console.error('Error fetching posts:', error);
      }
    },
    goToLogin() {
      this.$router.push('/login');
    },
    goToRegister() {
      this.$router.push('/register');
    },
    formatDate(date) {
      return new Date(date).toLocaleString();
    },
    showAlert(message) {
      this.alertMessage = message;
      setTimeout(() => {
        this.alertMessage = null;
      }, 5000);  
    },
  },
  mounted() {
    this.fetchPosts();
  },
};
</script>

<style scoped>
.logo {
  width: 90px; 
  height: auto;
}
.post-image {
  width: 100%;
  height: auto;
  max-height: 200px;
  object-fit: cover;
}
.icon-container {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.icon {
  cursor: pointer;
  width: 24px; 
  height: 24px; 
  color: #555; 
}

.error-alert {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  color: #721c24;
  text-align: center;
  padding: 15px;
  font-weight: bold;
}
</style>
