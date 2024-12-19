<template>
  <div class="container mt-5 d-flex">
    <!-- Admin Side Menu -->
    <div class="side-menu">
      <h1 class="onlybuns-title">OnlyBuns</h1>
      <ul class="menu-list">
        <li @click="$router.push('/admin/trends')" class="menu-item">
          <i class="fas fa-chart-line"></i> Trends
        </li>
        <li @click="$router.push('/admin/analytics')" class="menu-item">
          <i class="fas fa-chart-pie"></i> Analytics
        </li>
        <li @click="$router.push('/admin/users')" class="menu-item">
          <i class="fas fa-users"></i> User Profiles
        </li>
      </ul>
      <button @click="showLogoutConfirmation = true" class="logout-btn">
        <i class="fas fa-sign-out-alt"></i> Logout
      </button>
    </div>

    <!-- Main Content -->
    <div class="content">
      <h2 class="text-center">Admin Dashboard - All Posts</h2>

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
            <!-- Like Button -->
            <div class="icon-container">
              <i class="fas fa-thumbs-up icon"
                 :class="{'liked': post.likedByCurrentUser}"
                 @click="toggleLike(post.id, post)"></i>
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

      <!-- Confirmation Modal for Logout -->
      <div v-if="showLogoutConfirmation" class="logout-confirmation-modal">
        <div class="modal-content">
          <h3>Are you sure you want to logout?</h3>
          <button @click="logout" class="btn btn-danger">Yes, Logout</button>
          <button @click="cancelLogout" class="btn btn-secondary">Cancel</button>
        </div>
      </div>
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
      errorMessage: "",
      showLogoutConfirmation: false, // To control the logout confirmation modal
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
        return tokenData.role === "ADMIN";
      } catch (e) {
        console.error("Error decoding token:", e);
        return false;
      }
    },
    fetchPosts(token) {
      axios
        .get("http://localhost:8080/api/posts/all", {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((response) => {
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
    formatDate(date) {
      return new Date(date).toLocaleString();
    },
    viewPost(postId) {
      this.$router.push({ name: "PostDetails", params: { id: postId } });
    },
    logout() {
      localStorage.removeItem("token");
      localStorage.removeItem("likedPosts");
      this.$router.push("/");
    },
    cancelLogout() {
      this.showLogoutConfirmation = false;
    },
  },
};
</script>

<style scoped>
/* Add styles from the User Home Page for consistency */
.container {
  display: flex;
  flex-wrap: nowrap;
}

.side-menu {
  position: fixed;
  top: 0;
  left: 0;
  width: 250px;
  height: 100vh;
  background-color: #f9f9f9;
  padding: 20px;
  border-right: 1px solid #ddd;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
}

.onlybuns-title {
  font-family: "Pacifico", cursive;
  font-size: 2rem;
  font-weight: bold;
  color: #007bff;
  text-align: center;
  margin-bottom: 20px;
}

.menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.menu-item:hover {
  background-color: #007bff;
  color: white;
}

.post-image {
  width: 100%;
  height: auto;
  max-height: 200px;
  object-fit: cover;
}

.card {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
  height: 100%;
  cursor: pointer; 
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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

.icon.liked {
  color: #007bff;
}



.logout-btn {
  background-color: #ff4d4d;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.logout-btn:hover {
  background-color: #cc0000;
}

.content {
  flex: 1;
  margin-left: 250px;
  padding: 20px;
}
</style>
