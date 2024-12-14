<template>
  <div v-if="user" class="profile-container">
    <div class="profile-card">
      <h2 class="profile-header">{{ user.firstName }}'s Bunnies</h2>
      <div class="profile-info">
        <p>{{ user.firstName }}</p>
        <p>{{ user.lastName }}</p>
        <p>
          <strong>Following:</strong>
          <span @click="fetchFollowingList(user.id)" class="clickable">
            {{ followingCount }}
          </span>
        </p>
        <p>
          <strong>Followers:</strong>
          <span @click="fetchFollowersList(user.id)" class="clickable">
            {{ followersCount }}
          </span>
        </p>
      </div>
      <template v-if="userRole === 'USER'">
        <button 
          class="follow-button"
          v-if="!isFollowing"
          @click="followUser"
        >
          Follow
        </button>
        <button
          class="unfollow-button"
          v-else
          @click="confirmUnfollow"
        >
          Unfollow
        </button>
      </template>
    </div>

    <div class="posts-section">
      <div v-if="posts.length > 0" class="posts-list">
        <div v-for="post in posts" :key="post.id" class="post-card">
          <img :src="post.photo" class="post-image" alt="Post Image" v-if="post.photo" />
          <p>{{ post.description }}</p>
          <p class="post-date">{{ formatDate(post.createdAt) }}</p>
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

  <!-- Following Modal -->
  <div v-if="showFollowingModal" class="modal-overlay" @click.self="closeFollowingModal">
  <div class="unfollow-modal" @click.stop>
    <h3>Following</h3>
    <ul v-if="followingList.length > 0">
      <li v-for="following in followingList" :key="following.id">
        {{ following.firstName }} {{ following.lastName }}
      </li>
    </ul>
    <p v-else>No users found in the following list.</p>
    <button @click="closeFollowingModal" class="close-button">Close</button>
  </div>
</div>


  <!-- Followers Modal -->
  <div v-if="showFollowersModal" class="modal-overlay" @click.self="closeFollowersModal">
    <div class="unfollow-modal" @click.stop>
      <h3>Followers</h3>
      <ul v-if="followersList.length > 0">
        <li v-for="follower in followersList" :key="follower.id">
          {{ follower.firstName }} {{ follower.lastName }}
        </li>
      </ul>
      <p v-else>No followers to show.</p>
      <button @click="closeFollowersModal" class="close-button">Close</button>
    </div>
  </div>

  <!-- Confirm Unfollow Dialog -->
  <!-- Unfollow Modal -->
<div v-if="showDialog" class="modal-overlay" @click.self="cancelUnfollow">
  <div class="unfollow-modal" @click.stop>
    <h3>Are you sure you want to unfollow?</h3>
    <div class="dialog-actions">
      <button @click="unfollowUser" class="confirm-button">Yes</button>
      <button @click="cancelUnfollow" class="cancel-button">No</button>
    </div>
  </div>
</div>

</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      user: null,
      posts: [],
      isFollowing: false,
      showDialog: false,
      userRole: null,
      showFollowingModal: false,
      showFollowersModal: false,
      followingList: [],
      followersList: [],
      followersCount: 0,
      followingCount: 0,
    };
  },
  async created() {
    const userId = this.$route.params.userId;
    try {
      await this.fetchUserProfile(userId);
      await this.fetchUserPosts(userId);
      await this.checkIfFollowing(userId);
      await this.fetchFollowersCount(userId);
      await this.fetchFollowingCount(userId);
      this.getUserRole();
      console.log('Initialization complete:', {
        user: this.user,
        posts: this.posts,
        isFollowing: this.isFollowing,
        followersCount: this.followersCount,
        followingCount: this.followingCount,
      });
    } catch (error) {
      console.error("Error during component initialization:", error);
    }
  },
  methods: {
    async fetchUserProfile(userId) {
      try {
        const response = await axios.get(`http://localhost:8080/api/users/profile/${userId}`);
        this.user = response.data;
      } catch (error) {
        console.error("Error fetching user profile:", error);
      }
    },
    async fetchUserPosts(userId) {
      try {
        const response = await axios.get(`http://localhost:8080/api/posts/user/${userId}`);
        this.posts = response.data;
      } catch (error) {
        console.error("Error fetching user posts:", error);
      }
    },
    async fetchFollowersCount(userId) {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(
          `http://localhost:8080/api/users/${userId}/followers/count`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        this.followersCount = response.data;
      } catch (error) {
        console.error("Error fetching followers count:", error);
      }
    },
    async fetchFollowingCount(userId) {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(
          `http://localhost:8080/api/users/${userId}/following/count`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        this.followingCount = response.data;
      } catch (error) {
        console.error("Error fetching following count:", error);
      }
    },
    async checkIfFollowing(userId) {
      const token = localStorage.getItem("token");
      if (!token) {
        console.error("No token found. User must be logged in.");
        return;
      }

      try {
        const response = await axios.get(
          `http://localhost:8080/api/users/${userId}/isFollowing`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        this.isFollowing = response.data;
        console.log('Check if following:', this.isFollowing);
      } catch (error) {
        console.error("Error checking if following:", error.response?.status, error.message);
      }
    },
    async fetchFollowingList(userId) {
  try {
    const token = localStorage.getItem("token");
    const response = await axios.get(
      `http://localhost:8080/api/users/${userId}/following`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
    this.followingList = response.data;
    if (this.followingList.length > 0) {
  this.showFollowingModal = true;
} else {
  console.log("Following list is empty!");
  this.followingList = [{ id: 1, firstName: "Test", lastName: "User" }]; // Test data
}

  } catch (error) {
    console.error("Error fetching following list:", error);
  }
}
,
    async fetchFollowersList(userId) {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.get(
          `http://localhost:8080/api/users/${userId}/followers`,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        this.followersList = response.data;
        this.showFollowersModal = true;
        console.log('Followers list:', this.followersList);
      } catch (error) {
        console.error("Error fetching followers list:", error);
      }
    },
    async followUser() {
      try {
        const userId = this.user.id;
        const token = localStorage.getItem("token");
        if (!token) {
          console.error("No token found. User must be logged in.");
          return;
        }

        await axios.post(
          `http://localhost:8080/api/users/${userId}/follow`,
          {},
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        this.isFollowing = true;
        this.followersCount += 1;
        console.log('Followed user:', userId);
      } catch (error) {
        console.error("Error following user:", error.response?.status, error.message);
      }
    },
    confirmUnfollow() {
      this.showDialog = true;
      console.log('Confirm unfollow dialog opened');
    },
    async unfollowUser() {
      try {
        const userId = this.user.id;
        const token = localStorage.getItem("token");
        if (!token) {
          console.error("No token found. User must be logged in.");
          return;
        }

        await axios.delete(
          `http://localhost:8080/api/users/${userId}/unfollow`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        this.isFollowing = false;
        this.followersCount -= 1;
        console.log('Unfollowed user:', userId);
      } catch (error) {
        console.error("Error unfollowing user:", error.response?.status, error.message);
      } finally {
        this.showDialog = false;
        console.log('Unfollow dialog closed');
      }
    },
    cancelUnfollow() {
      this.showDialog = false;
      console.log('Unfollow dialog canceled');
    },
    async getUserRole() {
      const token = localStorage.getItem("token");
      if (!token) {
        console.error("No token found. User must be logged in.");
        return;
      }

      try {
        const decodedToken = JSON.parse(atob(token.split(".")[1]));
        this.userRole = decodedToken.role;
        console.log('User role:', this.userRole);
      } catch (error) {
        console.error("Error decoding token:", error.message);
      }
    },
    formatDate(date) {
      return new Date(date).toLocaleString();
    },
    closeFollowingModal() {
      this.showFollowingModal = false;
      console.log('Following modal closed');
    },
    closeFollowersModal() {
      this.showFollowersModal = false;
      console.log('Followers modal closed');
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
  background-color: #2c3e50;
  color: #ecf0f1;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  margin-bottom: 20px;
}

.profile-header {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.profile-info {
  font-size: 16px;
}

.profile-info p {
  margin: 10px 0;
}

.follow-button {
  width: 100%;
  margin-top: 15px;
  padding: 10px;
  font-size: 16px;
  font-weight: bold;
  color: #2c3e50;
  background-color: #1abc9c;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.follow-button:hover {
  background-color: #16a085;
}

.unfollow-button {
  width: 100%;
  margin-top: 15px;
  padding: 10px;
  font-size: 16px;
  font-weight: bold;
  color: #ecf0f1;
  background-color: #e74c3c;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.unfollow-button:hover {
  background-color: #c0392b;
}

.posts-section {
  width: 400px;
}

.posts-list .post-card {
  background-color: #34495e;
  color: #ecf0f1;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 10px;
}

.post-image {
  width: 100%;
  height: auto;
  border-radius: 5px;
  margin-bottom: 10px;
}

.post-date {
  font-size: 12px;
  color: #bdc3c7;
}

.no-posts-message {
  text-align: center;
  color: #bdc3c7;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6); /* Poluprovidna pozadina */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  display: flex; /* Ensure it shows the content */
  flex-direction: column; /* Align content properly */
  align-items: center; /* Center content horizontally */
  justify-content: center; /* Center content vertically */
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  max-width: 400px;
  width: 90%;
  text-align: center;
  z-index: 1100;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}




.modal h3 {
  margin-bottom: 20px;
}

.modal ul {
  list-style: none;
  padding: 0;
}

.modal li {
  margin-bottom: 10px;
}

.close-button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #1abc9c;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.close-button:hover {
  background-color: #16a085;
}

.clickable {
  color: #1abc9c;
  cursor: pointer;
  text-decoration: underline;
}

.dialog-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.confirm-button {
  padding: 10px 20px;
  background-color: #e74c3c;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.unfollow-modal {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  max-width: 500px;
  max-height: 500px;
  width: 90%;
  text-align: center;
  z-index: 1100;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  position: fixed; /* Fiksiran na ekranu */
  top: 50%; /* Pomera se na sredinu po vertikali */
  left: 50%; /* Pomera se na sredinu po horizontali */
  transform: translate(-50%, -50%); /* Centriranje */
}

.cancel-button {
  padding: 10px 20px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>