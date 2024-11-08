<template>
    <div class="container mt-5">
      <h2 class="text-center">Welcome, User!</h2>
  
      <div v-if="posts.length === 0 && !error" class="text-center">No posts available.</div>
  
      <div v-for="post in posts" :key="post.id" class="card mb-3">
        <div class="card-body">
          <h5 class="card-title">{{ post.description }}</h5>
          <img :src="post.photo" class="card-img-top" alt="Post image" />
          <p class="card-text"><small class="text-muted">{{ post.createdAt }}</small></p>
          <router-link :to="{ name: 'UserProfile', params: { userId: post.creatorId } }">
                {{ post.creatorUsername }}
              </router-link>
        </div>
      </div>
  
      <div v-if="error" class="alert alert-danger mt-3 text-center">
        {{ errorMessage }}
      </div>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  import { jwtDecode } from 'jwt-decode';
  
  export default {
    data() {
      return {
        posts: [], 
        error: false, 
        errorMessage: "" 
      };
    },
    created() {
      const token = localStorage.getItem("token");
  
      if (token) {
        const hasUserRole = this.getRolesFromToken(token);
        console.log("User has required role:", hasUserRole);
  
        if (hasUserRole) {
          this.fetchPosts(token);
        } else {
          this.error = true;
          this.errorMessage = "You do not have the correct role to access this page.";
        }
      } else {
        alert("No token found. Please login first.");
        this.$router.push("/login");
      }
    },
    methods: {
      getRolesFromToken(token) {
        try {
          const tokenData = jwtDecode(token);
          console.log("Decoded token data:", tokenData);
  
          // Check the `role` property for the `USER` role
          if (tokenData.role && tokenData.role === 'USER') {
            console.log('User has USER authority');
            return true;
          }
  
          console.log('User does not have the required role');
          return false;
        } catch (e) {
          console.error("Error decoding token:", e);
          return false;
        }
      },
      fetchPosts(token) {
        console.log("Sending request with token:", token);
        axios
          .get("http://localhost:8080/api/posts/all", {
            headers: { Authorization: `Bearer ${token}` },
          })
          .then((response) => {
            console.log("Posts fetched successfully:", response.data);
            this.posts = response.data;
          })
          .catch((error) => {
            console.error("Error fetching posts:", error.response ? error.response.status : error.message);
            this.error = true;
            this.errorMessage = "There was an error fetching posts. Please try again later.";
          });
      }
    }
  };
  </script>
  
  <style scoped>
  .container {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
  }
  .card {
    margin-bottom: 15px;
  }
  .card-img-top {
    max-height: 300px;
    object-fit: cover;
  }
  .text-muted {
    font-size: 0.9em;
  }
  .alert {
    font-size: 1.2em;
  }
  </style>
  