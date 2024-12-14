<template>
  <div class="container mt-5 d-flex">
    <!-- Side Menu -->
    <div class="side-menu">
      <h1 class="onlybuns-title">OnlyBuns</h1>
      <ul class="menu-list">
        <li @click="$router.push('/create')" class="menu-item">
          <i class="fas fa-plus-circle"></i> Create
        </li>
        <li @click="$router.push('/friends-bunnies')" class="menu-item active">
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
      <h2 class="text-center">Posts from Your Friends</h2>

      <!-- Display Posts -->
      <div v-if="posts.length > 0" class="row">
        <div v-for="post in sortedPosts" :key="post.id" class="col-md-6 mb-4">
          <div class="card post-card">
            <div @click="viewPost(post.id)">
              <img :src="post.photo" class="card-img-top post-image" alt="Post Image" v-if="post.photo" />
              <div class="card-body">
                <p class="post-description">{{ post.description }}</p>
                <p class="post-date">{{ formatDate(post.createdAt) }}</p>
              </div>
            </div>
            <div class="card-footer">
              <div class="icon-container">
                <i class="fas fa-thumbs-up icon"
                   :class="{'liked': post.likedByCurrentUser}"
                   @click="toggleLike(post.id, post)"></i>
              </div>
              <div class="user-link">
                <router-link :to="{ name: 'UserProfile', params: { userId: post.creatorId } }">
                  {{ post.creatorUsername }}
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else>
        <p>No posts from your friends yet. Start following some users to see their posts here!</p>
      </div>

      <!-- Error Message -->
      <div v-if="error" class="alert alert-danger mt-3 text-center">
        {{ errorMessage }}
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
      likedPosts: JSON.parse(localStorage.getItem("likedPosts")) || {},
    };
  },
  computed: {
    sortedPosts() {
      return [...this.posts].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    },
  },
  async created() {
    const token = localStorage.getItem("token");
    if (token) {
      await this.fetchFriendsPosts(token);
    } else {
      alert("No token found. Please log in first.");
      this.$router.push("/login");
    }
  },
  methods: {
    async fetchFriendsPosts(token) {
      try {
        const response = await axios.get("http://localhost:8080/api/posts/followed", {
          headers: { Authorization: `Bearer ${token}` },
        });
        const userEmail = jwtDecode(token).sub;

        this.posts = await Promise.all(
          response.data.map(async (post) => {
            const liked = await this.checkIfLiked(post.id, userEmail);
            post.likedByCurrentUser = liked;
            return post;
          })
        );

        this.updateLikedPostsInLocalStorage();
      } catch (error) {
        console.error("Error fetching friends' posts:", error.response?.status || error.message);
        this.error = true;
        this.errorMessage = "There was an error fetching posts. Please try again later.";
      }
    },
    async checkIfLiked(postId, userEmail) {
      if (this.likedPosts[postId]) {
        return this.likedPosts[postId];
      }
      try {
        const response = await axios.get(`http://localhost:8080/api/posts/${postId}/likes`, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        return response.data.some((like) => like.user.email === userEmail);
      } catch (error) {
        console.error("Error fetching likes:", error.response?.status || error.message);
        return false;
      }
    },
    toggleLike(postId, post) {
      const token = localStorage.getItem("token");
      if (token) {
        if (!post.likedByCurrentUser) {
          axios
            .post(`http://localhost:8080/api/posts/${postId}/like`, {}, { headers: { Authorization: `Bearer ${token}` } })
            .then(() => {
              post.likedByCurrentUser = true;
              this.likedPosts[postId] = true;
              this.updateLikedPostsInLocalStorage();
            })
            .catch((error) => {
              console.error("Error liking post:", error.response?.status || error.message);
              this.error = true;
              this.errorMessage = "There was an error liking the post.";
            });
        } else {
          alert("You have already liked this post.");
        }
      }
    },
    updateLikedPostsInLocalStorage() {
      localStorage.setItem("likedPosts", JSON.stringify(this.likedPosts));
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString();
    },
    viewPost(postId) {
      this.$router.push({ name: "PostDetails", params: { id: postId } });
    },
    goToProfile() {
      const token = localStorage.getItem("token");
      if (token) {
        const userEmail = jwtDecode(token).sub;
        this.$router.push(`/profile/${userEmail}`);
      } else {
        this.$router.push("/login");
      }
    },
  },
};
</script>

<style scoped>
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

.menu-item.active {
  background-color: #007bff;
  color: white;
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
  margin-left: 250px;
  padding: 20px;
}

.post-card {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
}

.post-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.card-img-top {
  width: 100%;
  height: 300px;
  object-fit: cover;
}

.icon {
  cursor: pointer;
  align-self: flex-start;
}

.icon.liked {
  color: #007bff;
}

.card-footer {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.user-link {
  font-size: 14px;
  font-weight: bold;
  color: #000;
}

.icon-container {
  align-self: flex-start;
}
</style>
