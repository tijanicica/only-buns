<template>
  <div class="user-profile">
    <div class="profile-info">
      <div class="user-details">
        <h1>{{ profileData.firstName }} {{ profileData.lastName }}</h1>
        <p><strong>Email:</strong> {{ profileData.email }}</p>
        <p><strong>Address:</strong> {{ profileData.streetName + ' ' + profileData.streetNumber + ', ' + profileData.city + ', ' + profileData.country }}</p>
        <p><strong>Followers:</strong> {{ followers.length }}</p>
      </div>
      <div class="password-change">
        <h3>Change Password</h3>
        <form @submit.prevent="changePassword">
          <label>Current Password:</label>
          <input type="password" v-model="passwordData.currentPassword" required />
          
          <label>New Password:</label>
          <input type="password" v-model="passwordData.newPassword" required />
          
          <label>Confirm New Password:</label>
          <input type="password" v-model="passwordData.confirmPassword" required />
          
          <button type="submit">Change</button>
        </form>
      </div>
    </div>

    <div class="connections">
      <h3>Followers</h3>
      <ul class="scrollable-list">
        <li v-for="follower in followers" :key="follower.id">
          {{ follower.firstName }} {{ follower.lastName }}
        </li>
      </ul>

      <h3>Following</h3>
      <ul class="scrollable-list">
        <li v-for="followee in following" :key="followee.id">
          {{ followee.firstName }} {{ followee.lastName }}
        </li>
      </ul>
    </div>
  </div>

  <div class="posts-section">
    <div v-if="posts.length > 0" class="posts-list">
      <div v-for="post in posts" :key="post.id" class="post-card">
        <img :src="post.photo" class="post-image" alt="Post Image" v-if="post.photo" />
        <p>{{ post.description }}</p>
      </div>
    </div>
    <div v-else class="no-posts-message">
      <p>This user has no posts yet.</p>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      profileData: null,
      followers: [],
      following: [],
      posts: [],
      passwordData: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    };
  },
  created() {
    const token = localStorage.getItem('token');
    const email = this.$route.params.userEmail;
    this.fetchProfileData(email, token);
    this.fetchConnections(email, token);
    this.fetchUserPosts(email, token);
  },
  methods: {
    async fetchProfileData(email, token) {
      try {
        const response = await axios.get(`http://localhost:8080/api/users/my-profile/${email}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        this.profileData = response.data;
      } catch (error) {
        console.error('Error fetching profile:', error);
        alert('Unable to load profile.');
      }
    },
    async fetchConnections(email, token) {
      try {
        const followersResponse = await axios.get(`http://localhost:8080/api/users/followers/${email}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        this.followers = followersResponse.data;

        const followingResponse = await axios.get(`http://localhost:8080/api/users/following/${email}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        this.following = followingResponse.data;
      } catch (error) {
        console.error('Error fetching connections:', error);
        alert('Unable to load connections.');
      }
    },
    async fetchUserPosts(email, token) {
      try {
        const postsResponse = await axios.get(`http://localhost:8080/api/users/posts/${email}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        this.posts = postsResponse.data;
      } catch (error) {
        console.error('Error fetching posts:', error);
        alert('Unable to load posts.');
      }
    },
    async changePassword() {
      if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
        alert('New passwords do not match');
        return;
      }
    
      const token = localStorage.getItem('token');
      const email = this.$route.params.userEmail;

      const passwordPayload = {
        email: email,
        oldPassword: this.passwordData.currentPassword,
        newPassword: this.passwordData.newPassword,
      };

      try {
        const response = await axios.put('http://localhost:8080/api/users/change-password', passwordPayload, {
          headers: {
            'Authorization': `Bearer ${token}`,
          }
        });

        if (response.status === 200) {
          alert('Password changed successfully');
        }
      } catch (error) {
        console.error('Error:', error);
        alert('Error changing password: ' + error.response.data);
      }
    },
  }
};
</script>

<style scoped>
/* Profile Section */
.user-profile {
  max-width: 800px;
  margin: 30px auto;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.profile-info {
  flex: 1 1 60%;
  margin-right: 20px;
}

.user-details {
  font-family: 'Arial', sans-serif;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.password-change {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

.password-change h3 {
  font-size: 1.5rem;
  margin-bottom: 15px;
}

.password-change label {
  font-weight: bold;
  display: block;
  margin-bottom: 5px;
}

.password-change input {
  width: 100%;
  padding: 10px;
  margin-top: 5px;
  border-radius: 4px;
  border: 1px solid #ccc;
  box-sizing: border-box;
}

.password-change button {
  background-color: #007bff;
  color: #fff;
  border: none;
  padding: 10px 15px;
  margin-top: 15px;
  cursor: pointer;
  border-radius: 5px;
}

.password-change button:hover {
  background-color: #0056b3;
}

/* Connections Section */
.connections {
  flex: 1 1 35%;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.connections h3 {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 10px;
}

.connections ul {
  list-style: none;
  padding-left: 0;
}

.connections li {
  font-size: 1.1rem;
  color: #555;
  margin-bottom: 8px;
}

.connections li:hover {
  background-color: #eef2f5;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
}

/* Posts Section */
.posts-section {
  margin-top: 30px;
}

.posts-list {
  display: flex;
  flex-wrap: wrap;
}

.post-card {
  flex: 1 1 45%;
  margin: 10px;
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.post-image {
  max-width: 100%;
  border-radius: 8px;
}

.no-posts-message {
  text-align: center;
  font-size: 1.2rem;
  color: #777;
}
</style>
