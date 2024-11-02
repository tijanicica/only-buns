<template>
    <div v-if="user" class="profile-container">
      <div class="profile-card">
        <h2 class="profile-header">{{ user.username }}'s Profile</h2>
        <div class="profile-info">
          <p><strong>Username:</strong> {{ user.username }}</p>
          <p><strong>First Name:</strong> {{ user.firstName }}</p>
          <p><strong>Last Name:</strong> {{ user.lastName }}</p>
          <p><strong>Followers:</strong> {{ user.followersNumber }}</p>
        </div>
      </div>
  
      <!-- User's Posts Section -->
      <div class="posts-section">
        <div v-if="posts.length > 0" class="posts-list">
          <div v-for="post in posts" :key="post.id" class="post-card">
            <img :src="post.photo" class="post-image" alt="Post Image" v-if="post.photo" />
            <p>{{ post.description }}</p>
            <p class="post-date">Posted on: {{ formatDate(post.createdAt) }}</p>
          </div>
        </div>
        <div v-else class="no-posts-message">
          <p>This user has no posts yet.</p>
        </div>
      </div>
    </div>
  
    <div v-else class="loading-message">
      <p>Loading profile...</p>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        user: null,
        posts: [], // Stores user's posts
      };
    },
    async created() {
      const userId = this.$route.params.userId;
      await this.fetchUserProfile(userId);
      await this.fetchUserPosts(userId); // Fetch user's posts
    },
    methods: {
      async fetchUserProfile(userId) {
        try {
          const response = await axios.get(`http://localhost:8080/api/users/${userId}`);
          this.user = response.data;
        } catch (error) {
          console.error('Error fetching user profile:', error);
        }
      },
      async fetchUserPosts(userId) {
        try {
          const response = await axios.get(`http://localhost:8080/api/posts/${userId}/posts`);
          this.posts = response.data;
        } catch (error) {
          console.error('Error fetching user posts:', error);
        }
      },
      formatDate(date) {
        return new Date(date).toLocaleString();
      },
    },
  };
  </script>
  
  <style scoped>
  .profile-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-top: 50px;
  }
  
  .profile-card {
    width: 400px;
    padding: 20px;
    background-color: #f8f9fa;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
  }
  
  .profile-header {
    text-align: center;
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 20px;
    color: #333;
  }
  
  .profile-info {
    font-size: 16px;
    color: #555;
  }
  
  .profile-info p {
    margin: 10px 0;
  }
  
  .profile-info strong {
    color: #333;
  }
  
  .posts-section {
    width: 400px;
  }
  
  .posts-section h3 {
    font-size: 20px;
    color: #333;
    margin-bottom: 15px;
  }
  
  .posts-list .post-card {
    background-color: #fff;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .post-card h4 {
    font-size: 18px;
    color: #333;
    margin-bottom: 10px;
  }
  
  .post-card p {
    color: #555;
  }
  
  .post-date {
    font-size: 12px;
    color: #777;
    margin-top: 10px;
  }
  
  .no-posts-message {
    text-align: center;
    color: #777;
  }
  .post-image {
  width: 100%; /* Podesite širinu slike da bude 100% širine roditelja */
  height: auto; /* Visina automatski podešava da zadrži proporcije */
  max-height: 200px; /* Maksimalna visina za slike, možete prilagoditi */
  object-fit: cover; /* Osigurava da slika pokriva čitavu površinu kartice */
}
  </style>
  