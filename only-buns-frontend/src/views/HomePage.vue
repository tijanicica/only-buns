<template>
    <div class="home-page">
      <h1>OnlyBuns</h1>
  
      <!-- Login and Register Buttons -->
      <div class="auth-buttons">
        <button @click="goToLogin">Login</button>
        <button @click="goToRegister">Register</button>
      </div>
  
      <!-- Display Posts -->
      <div v-if="posts.length > 0" class="posts">
        <div v-for="post in sortedPosts" :key="post.id" class="post">
          <h2>{{ post.description }}</h2>
          <img :src="post.photo" class="post-image" alt="Post Image" v-if="post.photo" />
          <p>Posted at: {{ formatDate(post.createdAt) }}</p>
        </div>
      </div>
      <div v-else>
        <p>No posts available.</p>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        posts: [],
      };
    },
    computed: {
      // Sort posts in reverse order based on createdAt field
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
    },
    mounted() {
      this.fetchPosts();
    },
  };
  </script>
  
  <style scoped>
  .home-page {
    text-align: center;
  }
  .auth-buttons {
    margin-bottom: 20px;
  }
  .auth-buttons button {
    margin: 0 10px;
    padding: 10px;
  }
  .posts {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  .post {
    border: 1px solid #ddd;
    padding: 20px;
    margin-bottom: 20px;
    width: 80%;
  }


  .post {
    flex: 1 1 300px; 
    margin: 10px; 
  }

  .post-image {
    width: 100%; 
    height: auto; 
    max-width: 300px; 
    max-height: 200px; 
    object-fit: cover; 
  }
  </style>
  