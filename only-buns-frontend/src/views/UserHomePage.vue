<template>
  <div class="container mt-5 d-flex">
    
  

  
<!-- Side Menu -->
<div class="side-menu">
  <h1 class="onlybuns-title">OnlyBuns</h1>
  <ul class="menu-list">
    <li @click="$router.push('/create')" class="menu-item">
      <i class="fas fa-plus-circle"></i> Create
    </li>
    <li @click="$router.push('/friends-bunnies')" class="menu-item">
      <i class="fas fa-paw"></i> Friends' Bunnies
    </li>
    <li @click="$router.push('/trends')" class="menu-item">
      <i class="fas fa-chart-line"></i> Trends
    </li>
    <li @click="$router.push('/map')" class="menu-item">
      <i class="fas fa-map-marker-alt"></i> Map
    </li>
    <li @click="$router.push('/chat')" class="menu-item">
      <i class="fas fa-comments"></i> Chat
    </li>
    <li @click="goToProfile" class="menu-item">
      <i class="fas fa-user"></i> Your Profile
    </li>
  </ul>
  <button @click="showLogoutConfirmation = true" class="logout-btn">
    <i class="fas fa-sign-out-alt"></i> Logout
  </button>
</div>



    <!-- Main Content -->
    <div class="content">
     

    

      <!-- Display Posts -->
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
          <button @click="confirmLogout" class="btn btn-danger">Yes, Logout</button>
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
    goToProfile() {
    const token = localStorage.getItem("token");
    if (token) {
      const decoded = jwtDecode(token);
      const userEmail = decoded.sub;
      this.$router.push(`/profile/${userEmail}`);
    } else {
      this.$router.push("/login");  // Ako nema tokena, redirektuj na login
    }
  },
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
.container {
  display: flex;
  flex-wrap: nowrap; /* Sprečava prelamanje sadržaja */
}

.top-right {
  position: absolute;
  top: 10px;
  right: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 16px;
  font-weight: bold;
  margin: 0;
}

.profile-icon {
  font-size: 24px;
  cursor: pointer;
  color: #007bff;
}

.profile-icon:hover {
  color: #0056b3;
}


.side-menu .btn {
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
  width: 400px;
  max-width: 90%;
}

.logout-confirmation-modal button {
  margin-top: 10px;
}

.side-menu {
  position: fixed; /* Fiksira meni na levoj strani */
  top: 0;
  left: 0;
  width: 250px;
  height: 100vh; /* Puni visinu ekrana */
  background-color: #f9f9f9;
  padding: 20px;
  border-right: 1px solid #ddd;
  display: flex;
  flex-direction: column;
  justify-content: space-between; /* Odvaja stavke menija od Logout dugmeta */
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
}

.onlybuns-title {
  font-family: 'Pacifico', cursive;
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

.menu-item i {
  font-size: 18px;
  color: #007bff;
}

.menu-item:hover {
  background-color: #007bff;
  color: white;
}

.menu-item:hover i {
  color: white;
}
.logout-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background-color: #ff4d4d; /* Crvena boja za Logout */
  color: white;
  border: none;
  border-radius: 8px;
  padding: 10px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 30px; /* Razmak između menija i dugmeta */
}
.logout-btn i {
  font-size: 18px;
}

.logout-btn:hover {
  background-color: #cc0000; /* Tamnija crvena na hover */
}

.content {
  flex: 1;
  margin-left: 250px; /* Ostavlja prostor za meni */
  padding: 20px; /* Dodatno unutrašnje margine */
  box-sizing: border-box; /* Uključuje padding u dimenzije elementa */
}
</style>


