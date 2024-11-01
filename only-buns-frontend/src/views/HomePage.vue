<template>
  <div class="container mt-5">
    <h1 class="text-center">OnlyBuns</h1>

    <!-- Login and Register Buttons -->
    <div class="text-center mb-4">
      <button class="btn btn-primary" @click="goToLogin">Login</button>
      <button class="btn btn-secondary" @click="goToRegister">Register</button>
    </div>

    <!-- Display Posts -->
    <div v-if="posts.length > 0" class="row">
      <div v-for="post in sortedPosts" :key="post.id" class="col-md-4 mb-4">
        <div class="card">
          <img :src="post.photo" class="card-img-top post-image" alt="Post Image" v-if="post.photo" />
          <div class="card-body">
            <h5 class="card-title">{{ post.description }}</h5>
            <p class="card-text">Posted at: {{ formatDate(post.createdAt) }}</p>
          </div>
        </div>
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
.post-image {
  width: 100%;
  height: auto;
  max-height: 200px;
  object-fit: cover;
}
</style>
