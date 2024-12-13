<template>
  <div class="trends-page">
    <h1>Trends</h1>

    <div v-if="loading">Loading trends...</div>
    <div v-else>
      <!-- Stats and Top Likers Section -->
      <div class="stats-top-likers-container">
        <div class="stats">
          <p>Total Posts: <span class="stats-value">{{ stats.totalPosts }}</span></p>
          <p>Posts Last Month: <span class="stats-value">{{ stats.postsLastMonth }}</span></p>
        </div>

        <div class="top-likers">
          <h2><i class="fas fa-thumbs-up"></i> Top Likers (Last Week)</h2>
          <ul>
            <li v-for="user in topLikers" :key="user.id">
              <router-link :to="{ name: 'UserProfile', params: { userId: user.id } }">
                <i class="fas fa-user-circle"></i> {{ user.firstName }}
              </router-link>
            </li>
          </ul>
        </div>
      </div>

      <!-- Weekly Popular Posts -->
      <div v-if="weeklyPopularPosts.length > 0" class="row">
        <h2 class="section-title">This Week's Best</h2>
        <div v-for="post in weeklyPopularPosts" :key="post.id" class="col-sm-6 col-md-4 mb-4">
          <div class="card post-card">
            <div @click="viewPost(post.id)">
              <img :src="post.photo" class="card-img-top post-image" alt="Post Image" v-if="post.photo" />
              <div class="card-body">
                <p>{{ post.description }}</p>
                <p class="card-text">{{ formatDate(post.createdAt) }}</p>
              </div>
            </div>
            <div class="card-footer">
              <router-link :to="{ name: 'UserProfile', params: { userId: post.creatorId } }">
                {{ post.creatorUsername }}
              </router-link>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        <p>Nothing trendy from last week.</p>
      </div>

      <!-- All Time Popular Posts -->
      <div v-if="allTimePopularPosts.length > 0" class="row">
        <h2 class="section-title">All Time Favourites</h2>
        <div v-for="post in allTimePopularPosts" :key="post.id" class="col-sm-6 col-md-4 mb-4">
          <div class="card post-card">
            <div @click="viewPost(post.id)">
              <img :src="post.photo" class="card-img-top post-image" alt="Post Image" v-if="post.photo" />
              <div class="card-body">
                <p>{{ post.description }}</p>
                <p class="card-text">{{ formatDate(post.createdAt) }}</p>
              </div>
            </div>
            <div class="card-footer">
              <router-link :to="{ name: 'UserProfile', params: { userId: post.creatorId } }">
                {{ post.creatorUsername }}
              </router-link>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        <p>No posts available.</p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      stats: {},
      weeklyPopularPosts: [],
      allTimePopularPosts: [],
      topLikers: [],
      loading: true,
    };
  },
  async created() {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("You must be logged in to view this page.");
      this.$router.push("/login");
      return;
    }

    try {
      axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;

      const [statsResponse, weeklyPostsResponse, allTimePostsResponse, topLikersResponse] =
        await Promise.all([
          axios.get("http://localhost:8080/api/trends/stats"),
          axios.get("http://localhost:8080/api/trends/popular-posts/weekly"),
          axios.get("http://localhost:8080/api/trends/popular-posts/all-time"),
          axios.get("http://localhost:8080/api/trends/top-likers/weekly"),
        ]);

      this.stats = statsResponse.data;
      this.weeklyPopularPosts = weeklyPostsResponse.data;
      this.allTimePopularPosts = allTimePostsResponse.data;
      this.topLikers = topLikersResponse.data;

      this.loading = false;
    } catch (error) {
      console.error("Error fetching trends data:", error);
      if (error.response && error.response.status === 401) {
        alert("Session expired. Please log in again.");
        this.$router.push("/login");
      }
    }
  },
  methods: {
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString();  // Formatting date as string
    },

    // Navigate to post details page
    viewPost(postId) {
      this.$router.push({ name: 'PostDetails', params: { id: postId } });
    },
  }
};
</script>

<style scoped>
.trends-page {
  padding: 40px;
  background-color: #f9f9f9;
  font-family: 'Arial', sans-serif;
  margin: 0 auto;
  max-width: 1200px; /* Increase the max-width for larger margins */
}

h1 {
  text-align: center;
  font-size: 2.2rem;
  margin-bottom: 20px;
  color: #333;
}

.stats-top-likers-container {
  display: flex;
  justify-content: space-between;
  margin-bottom: 40px;
}

.stats, .top-likers {
  background-color: #fff;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 48%;
}

.stats p,
.top-likers h2 {
  font-size: 1.2rem;
  margin: 10px 0;
  color: #333;
}

.stats-value {
  color: #007bff;
  font-weight: bold;
}

.top-likers ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.top-likers li {
  font-size: 1rem;
  margin: 8px 0;
}

.top-likers li a {
  color: #007bff;
  text-decoration: none;
}

.top-likers li a:hover {
  text-decoration: underline;
}

.section-title {
  font-size: 1.8rem;
  font-family: 'Verdana', sans-serif;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.row {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 30px;
}

.col-sm-6,
.col-md-4 {
  flex: 1 1 calc(33.333% - 20px);
  max-width: 33.333%;
  box-sizing: border-box;
}

.card {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  transition: all 0.3s ease;
  overflow: hidden;
}

.card:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.card-body {
  padding: 12px;
}

.card-img-top {
  width: 100%;
  height: 150px; /* Reduced image size */
  object-fit: cover;
  border-bottom: 1px solid #ddd;
}

.card-footer {
  padding: 8px 12px;
  background-color: #f8f8f8;
  border-top: 1px solid #ddd;
  text-align: right;
}

.card-footer a {
  color: #007bff;
  font-weight: bold;
  text-decoration: none;
}

.card-footer a:hover {
  text-decoration: underline;
}

.post-image {
  height: 150px; /* Reduced image size */
  object-fit: cover;
}

.post-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 100%;
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

@media screen and (max-width: 768px) {
  .col-md-4 {
    flex: 1 1 100%;
    max-width: 100%;
  }

  .stats-top-likers-container {
    flex-direction: column;
  }
}
</style>
