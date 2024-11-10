<template>
  <div class="container mt-5">
    <h2 class="text-center">Welcome, User!</h2>

    <!-- Side Menu Buttons -->
    <div class="side-menu">
      <router-link to="/friends-bunnies" class="btn btn-primary mb-2">See your friends' bunnies</router-link>
      <router-link to="/trends" class="btn btn-primary mb-2">Trends</router-link>
      <router-link to="/map" class="btn btn-primary mb-2">Map</router-link>
      <router-link to="/chat" class="btn btn-primary mb-2">Chat</router-link>
      <router-link to="/your-profile" class="btn btn-primary mb-2">Your profile</router-link>
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
            <i class="fas fa-thumbs-up icon" 
               :class="{'liked': post.likedByCurrentUser}" 
               @click="toggleLike(post.id, post)">
            </i>
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
import { jwtDecode } from "jwt-decode"; // Import jwtDecode

export default {
  data() {
    return {
      posts: [],
      error: false,
      errorMessage: "",
      likedPosts: JSON.parse(localStorage.getItem("likedPosts")) || {}  // Fetch liked posts from localStorage
    };
  },
  computed: {
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

    toggleLike(postId, post) {
      const token = localStorage.getItem("token");
      if (token) {
        axios
          .post(`http://localhost:8080/api/posts/${postId}/like`, {}, {
            headers: { Authorization: `Bearer ${token}` },
          })
          .then((response) => {
            console.log("Post liked successfully:", response.data);
            // Update the local 'likedByCurrentUser' state immediately after the like is successful
            post.likedByCurrentUser = !post.likedByCurrentUser;

            // Update liked posts in localStorage
            this.likedPosts[postId] = post.likedByCurrentUser;
            this.updateLikedPostsInLocalStorage();
          })
          .catch((error) => {
            console.error("Error liking post:", error.response ? error.response.status : error.message);
            this.error = true;
            this.errorMessage = "There was an error liking the post.";
          });
      }
    },

    // Store the liked posts in local storage
    updateLikedPostsInLocalStorage() {
      localStorage.setItem("likedPosts", JSON.stringify(this.likedPosts));
    },

    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString();  // Formatting date as string
    },
  },
};
</script>

<style scoped>
.card {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
  height: 100%;
}

.card-body {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.card-img-top {
  width: 100%;
  height: auto;
  max-height: 200px;
  object-fit: cover;
}

.text-muted {
  font-size: 0.9em;
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
  color: #007bff;  /* Change the color to blue when liked */
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
</style>
