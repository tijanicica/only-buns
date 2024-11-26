<template>
  <div class="container mt-5">
    <div class="text-center mb-2">
      <h1 class="onlybuns-title">OnlyBuns</h1>
      <img src="@/assets/bunny-logo.jpg" alt="Bunny Logo" class="logo" />
    </div>
    

    <div class="button-container text-center mb-4">
      <button class="btn btn-primary" @click="goToLogin">Login</button>
      <button class="btn btn-secondary" @click="goToRegister">Register</button>
    </div>

    <div v-if="posts.length > 0" class="row">
      <div v-for="post in sortedPosts" :key="post.id" class="col-md-4 mb-4">
        <div class="card post-card">
          <div @click="viewPost(post.id)">
            <img :src="post.photo" class="card-img-top post-image" alt="Post Image" v-if="post.photo" />
            <div class="card-body">
             
              <p>{{ post.description }}</p>
              <p class="card-text"> {{ formatDate(post.createdAt) }}</p>
            </div>
          </div>

          <div class="card-footer">
    <router-link :to="{ name: 'UserProfile', params: { userId: post.creatorId } }">
      {{ post.creatorUsername }}
    </router-link>
          </div>

          <!-- Only Display Like and Comment Counts, No Commenting or Liking for Non-Authenticated Users -->
          <div class="icon-container">
            <i class="fas fa-thumbs-up icon" @click="showAlert('You must be logged in to like.')"></i>
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
    isAuthenticated() {
      return !!localStorage.getItem("token");
    }
  },
  methods: {
    async fetchPosts() {
      try {
        const response = await axios.get('http://localhost:8080/api/posts/all');
        this.posts = response.data;
        // Assuming the API provides `likesCount` and `commentsCount` in the response
        this.posts.forEach(post => {
          post.likesCount = post.likes.length;  // Assuming `likes` is an array of likes
          post.commentsCount = post.comments.length;  // Assuming `comments` is an array of comments
        });
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
    },showAlert(message) {
      this.alertMessage = message;
      setTimeout(() => {
        this.alertMessage = null;
      }, 5000);  
    },
    viewPost(postId) {
      this.$router.push({ name: 'PostDetails', params: { id: postId } });
    },
  },
  mounted() {
    this.fetchPosts();
  }
};
</script>

<style scoped>
.logo {
  width: 90px;
  height: auto;
}
.button-container .btn {
  margin: 0 10px; /* Adds spacing between buttons */
}
.onlybuns-title {
  font-family: 'Pacifico', cursive;
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 10px;
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
.card {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
  height: 100%;
  cursor: pointer; 
  transition: transform 0.3s ease, box-shadow 0.3s ease; }

.card:hover {
  transform: translateY(-5px); /* Slight lift effect */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Add shadow for hover */
}
.card:hover {
  transform: translateY(-5px); /* Slight lift effect */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Add shadow for hover */
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
