<template>
  <div class="main-container">
    <!-- Side Menu on the left -->
    <div class="side-menu">
      <router-link to="/friends-bunnies" class="btn btn-primary mb-2">See your friends' bunnies</router-link>
      <router-link to="/trends" class="btn btn-primary mb-2">Trends</router-link>
      <router-link to="/map" class="btn btn-primary mb-2">Map</router-link>
      <router-link to="/chat" class="btn btn-primary mb-2">Chat</router-link>
      <router-link to="/your-profile" class="btn btn-primary mb-2">Your profile</router-link>
      <router-link to="/post-creation" class="btn btn-primary mb-2">Create post</router-link>
    </div>

    <!-- Content Area on the right -->
    <div class="content-container">
      <!-- Welcome message -->
      <h2 class="welcome-message text-center">Welcome, User!</h2>

      <!-- Display Posts -->
      <div v-if="posts.length > 0" class="posts-container">
        <div v-for="post in sortedPosts" :key="post.id" class="card">
          <router-link :to="{ name: 'PostDetails', params: { id: post.id } }">
          
  <div class="container mt-5">
    <h2 class="text-center">Welcome, User!</h2>

    <!-- Logout Button -->
    <div class="text-center">
      <button @click="showLogoutConfirmation = true" class="btn btn-danger mt-4">Logout</button>
    </div>

    <!-- Display Posts -->
    <div v-if="posts.length > 0" class="row">
      <div v-for="post in sortedPosts" :key="post.id" class="col-md-4 mb-4">
        <div class="card post-card">
          <div @click="viewPost(post.id)">

            <img :src="post.photo" class="card-img-top post-image" alt="Post Image" v-if="post.photo" />
            <div class="card-body">
              <h5 class="card-title">{{ post.creatorUsername }}</h5>
              <p>{{ post.description }}</p>
              <p class="card-text">Posted at: {{ formatDate(post.createdAt) }}</p>
            </div>
          </div>

          <!-- Like Button -->
          <div class="icon-container">
            <i class="fas fa-thumbs-up icon" 
               :class="{'liked': post.likedByCurrentUser}" 
               @click="toggleLike(post.id, post)"></i>
          </div>
        </div>
      </div>
      <div v-else class="no-posts">
        <p>No posts available.</p>
      </div>
    </div>

    <!-- Confirmation Modal for Logout -->
    <div v-if="showLogoutConfirmation" class="logout-confirmation-modal">
      <div class="modal-content">
        <h3>Are you sure you want to logout?</h3>
        <button @click="confirmLogout" class="btn btn-danger">Yes, Logout</button>
        <button @click="cancelLogout" class="btn btn-secondary">Cancel</button>
      </div>
    </div>
  </div>
</template>



<script>
import axios from "axios";
import { jwtDecode } from "jwt-decode"; // Import jwtDecode

export default {
  data() {
    return {
      posts: [],
      error: false,
      errorMessage: "",
      likedPosts: JSON.parse(localStorage.getItem("likedPosts")) || {},  // Fetch liked posts from localStorage
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
      console.log("Token found, fetching posts...");
      this.fetchPosts(token);
    } else {
      alert("No token found. Please login first.");
      this.$router.push("/login");
    }
  },
  methods: {
    // Fetch posts from backend
    async fetchPosts(token) {
      console.log("Fetching posts with token:", token);
      try {
        const response = await axios.get("http://localhost:8080/api/posts/all", {
          headers: { Authorization: `Bearer ${token}` },
        });
        console.log("Posts fetched successfully:", response.data);

        // Extract current user email from token
        const userEmail = jwtDecode(token).sub;

        // Fetch posts and check if the user liked them
        this.posts = await Promise.all(response.data.map(async (post) => {
          const liked = await this.checkIfLiked(post.id, userEmail);  // Ensure this completes before returning the post
          post.likedByCurrentUser = liked;  // Set the liked status
          return post;
        }));

        // Update local storage after fetching posts
        this.updateLikedPostsInLocalStorage();
      } catch (error) {
        console.error("Error fetching posts:", error.response ? error.response.status : error.message);
        this.error = true;
        this.errorMessage = "There was an error fetching posts. Please try again later.";
      }
    },

    // Check if the post is liked by the current user
    async checkIfLiked(postId, userEmail) {
      // Check if the post is liked by the user in the local storage first
      if (this.likedPosts[postId]) {
        return this.likedPosts[postId];
      }

      try {
        const response = await axios.get(`http://localhost:8080/api/posts/${postId}/likes`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
        });
        // Check if the current user liked this post
        return response.data.some(like => like.user.email === userEmail);
      } catch (error) {
        console.error("Error fetching likes:", error.response ? error.response.status : error.message);
        return false;  // Default to false if there's an error
      }
    },

    // Toggle the like status of a post
    toggleLike(postId, post) {
      const token = localStorage.getItem("token");
      if (token) {
        // Check if the post is already liked by the current user
        if (!post.likedByCurrentUser) {
          // User hasn't liked the post, so send a like request
          axios
            .post(`http://localhost:8080/api/posts/${postId}/like`, {}, {
              headers: { Authorization: `Bearer ${token}` },
            })
            .then((response) => {
              console.log("Post liked successfully:", response.data);
              // Update the local 'likedByCurrentUser' state immediately after the like is successful
              post.likedByCurrentUser = true;

              // Update liked posts in localStorage
              this.likedPosts[postId] = true;
              this.updateLikedPostsInLocalStorage();
            })
            .catch((error) => {
              console.error("Error liking post:", error.response ? error.response.status : error.message);
              this.error = true;
              this.errorMessage = "There was an error liking the post.";
            });
        } else {
          // If the post is already liked, show an alert
          alert("You have already liked this post.");
        }
      }
    },

    // Store the liked posts in local storage
    updateLikedPostsInLocalStorage() {
      localStorage.setItem("likedPosts", JSON.stringify(this.likedPosts));
    },

    // Format date to a more readable format
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString();  // Formatting date as string
    },

    // Navigate to post details page
    viewPost(postId) {
      this.$router.push({ name: 'PostDetails', params: { id: postId } });
    },

    // Confirm logout method
    confirmLogout() {
      console.log("Confirming logout...");

      // Log token before logout
      const tokenBeforeLogout = localStorage.getItem("token");
      console.log("Token before logout: ", tokenBeforeLogout);  // Log the current token

      // Remove the token from localStorage
      localStorage.removeItem("token");

      // Optionally clear user data (like liked posts) from localStorage
      localStorage.removeItem("likedPosts");

      // Log token after logout
      const tokenAfterLogout = localStorage.getItem("token");
      console.log("Token after logout: ", tokenAfterLogout);  // Should be null or undefined

      this.$router.push("/");  // Redirect to home page after logout
    },

    // Cancel logout method
    cancelLogout() {
      this.showLogoutConfirmation = false; // Hide the logout confirmation modal
    },
  },
};
</script>

<style scoped>
/* Main container with full width */
.main-container {
  display: flex;
}

/* Fixed side menu on the left */
.side-menu {
  width: 200px;
  position: fixed;
  left: 0;
  top: 0;
  height: 120vh;
  padding: 20px;
  background-color: #f8f9fa;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  overflow-y: auto;
}

/* Content area to the right of the side menu */
.content-container {
  margin-left: 220px; /* Space for the side menu */
  padding: 20px;
  width: 100%;
}

/* Welcome message styling */
.welcome-message {
  text-align: center;
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 32px;
  color: #333;
}

/* Posts grid */
.posts-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.btn {
  width: 100%;
  text-align: left;
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
  cursor: pointer; /* Show a pointer cursor to indicate it's clickable */
  transition: transform 0.3s ease, box-shadow 0.3s ease; /* Smooth transition for hover effect */
}

.card:hover {
  transform: translateY(-5px); /* Slight lift effect */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* Add shadow for hover */
}

.card-body {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
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

.icon.liked {
  color: #007bff;
}


.no-posts {
  padding: 20px;
  text-align: center;
}

.btn {
  text-align: left;
  width: 100%;

.logout-confirmation-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
}

.logout-confirmation-modal .modal-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  width: 400px; /* Adjust the width to make it smaller */
  max-width: 90%; /* Ensure it doesn't exceed screen size */
}

.logout-confirmation-modal button {
  margin-top: 10px;
}

</style>


