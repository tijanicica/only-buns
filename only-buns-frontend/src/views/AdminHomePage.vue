<template>
    <div class="container mt-5">
      <h2 class="text-center">Admin Dashboard</h2>
      
      <!-- Admin Side Menu -->
      <div class="side-menu">
        <router-link to="/admin/users" class="btn btn-primary mb-2">Manage Users</router-link>
        <router-link to="/admin/posts" class="btn btn-primary mb-2">Manage Posts</router-link>
        <router-link to="/admin/settings" class="btn btn-primary mb-2">Settings</router-link>
      </div>
  
      <!-- Display Posts -->
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
            <!-- Like and Comment Buttons -->
            <div class="icon-container">
              <i class="fas fa-thumbs-up icon" @click="likePost(post.id)"></i>
              <i class="fas fa-comment icon" @click="commentPost(post.id)"></i>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        <p>No posts available.</p>
      </div>
  
      <!-- Error Message -->
      <div v-if="error" class="alert alert-danger mt-3 text-center">
        {{ errorMessage }}
      </div>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  import { jwtDecode } from "jwt-decode";
  
  export default {
    data() {
      return {
        posts: [],
        error: false,
        errorMessage: ""
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
    created() {
      const token = localStorage.getItem("token");
  
      if (token) {
        const hasAdminRole = this.getRolesFromToken(token);
        console.log("User has admin role:", hasAdminRole);
  
        if (hasAdminRole) {
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
  
          // Check if the role is 'ADMIN'
          if (tokenData.role && tokenData.role === "ADMIN") {
            console.log("User has ADMIN authority");
            return true;
          }
  
          console.log("User does not have the required admin role");
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
            headers: { Authorization: `Bearer ${token}` }
          })
          .then((response) => {
            console.log("Posts fetched successfully:", response.data);
            this.posts = response.data;
          })
          .catch((error) => {
            console.error(
              "Error fetching posts:",
              error.response ? error.response.status : error.message
            );
            this.error = true;
            this.errorMessage = "There was an error fetching posts. Please try again later.";
          });
      },
      likePost(postId) {
        console.log(`Liked post with ID: ${postId}`);
      },
      commentPost(postId) {
        console.log(`Commented on post with ID: ${postId}`);
      },
      formatDate(date) {
        return new Date(date).toLocaleString();
      },
    }
  };
  </script>
  
  <style scoped>
  .container {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .side-menu {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    margin-bottom: 20px;
  }
  
  .btn {
    width: 100%;
    text-align: left;
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
  </style>
  