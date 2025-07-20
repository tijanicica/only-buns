<template>
  <div class="user-profile" v-if="isLoaded">

    <!-- Profile Header -->
    

    <!-- Profile Information -->
    <div class="profile-info">
      <div class="user-details">
        <h1 class="user-name">
          <span>{{ profileData.firstName }} {{ profileData.lastName }}</span>
          <button @click="openEditModal" class="edit-button">
            <i class="fas fa-edit"></i> 
          </button>
        </h1>
        <p><strong>Email:</strong> {{ profileData.email }}</p>
        <p><strong>Address:</strong> {{ profileData.streetName + ' ' + profileData.streetNumber + ', ' + profileData.city + ', ' + profileData.country }}</p>
        
        <button @click="toggleFollowersList">{{ followers.length }} Followers</button>
        <div v-if="isFollowersListVisible" class="dropdown-list">
          <ul>
            <li v-for="follower in followers" :key="follower.id">
              <router-link :to="{ name: 'UserProfile', params: { userId: follower.id } }">
                {{ follower.firstName }} 
              </router-link>
            </li>
          </ul>
        </div>

        <button @click="toggleFollowingList">{{ following.length }} Following</button>
        <div v-if="isFollowingListVisible" class="dropdown-list">
          <ul>
            <li v-for="followee in following" :key="followee.id">
              <router-link :to="{ name: 'UserProfile', params: { userId: followee.id } }">
                {{ followee.firstName }} 
              </router-link>
            </li>
          </ul>
        </div>

        <!-- Change Password section -->
        <div class="password-change">
          <button @click="showPasswordForm = !showPasswordForm">Change Password</button>
          <div v-if="showPasswordForm">
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

      </div>
    </div>

    <!-- Edit Profile Modal -->
    <div v-if="isEditModalOpen" class="modal">
      <div class="modal-content">
        <h2>Edit Profile</h2>
        <form @submit.prevent="updateProfile">
          <label>First Name:</label>
          <input type="text" v-model="editData.firstName" required />

          <label>Last Name:</label>
          <input type="text" v-model="editData.lastName" required />

          <label>Street Name:</label>
          <input type="text" v-model="editData.streetName" required />

          <label>Street Number:</label>
          <input type="text" v-model="editData.streetNumber" required />

          <label>City:</label>
          <input type="text" v-model="editData.city" required />

          <label>Country:</label>
          <input type="text" v-model="editData.country" required />

          <button type="submit">Save Changes</button>
          <button @click="closeEditModal">Cancel</button>
        </form>
      </div>
    </div>

    <!-- Posts Section -->
    <div class="posts-section">
      <div v-if="posts.length > 0" class="posts-list">
        <div 
          v-for="(post, index) in posts" 
          :key="post.id" 
          class="post-card clickable-post" 
          @click="viewPost(post.id)"
          :class="{'post-column': index % 3 === 0, 'post-column-middle': (index - 1) % 3 === 0, 'post-column-right': (index - 2) % 3 === 0}">
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
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      profileData: null,
      editData: {
        firstName: '',
        lastName: '',
        streetName: '',
        streetNumber: '',
        city: '',
        country: '',
        latitude: null,
        longitude: null,
      },
      followers: [],
      following: [],
      posts: [],
      isLoaded: false,
      isEditModalOpen: false,
      passwordData: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      isFollowersListVisible: false,
      isFollowingListVisible: false,
      showPasswordForm: false,
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
        this.isLoaded = true;
      } catch (error) {
        console.error('Error fetching profile:', error);
        alert('Unable to load profile.');
      }
    },
    openEditModal() {
      // Važno: Kopiramo sve podatke, uključujući i postojeće koordinate ako postoje
      this.editData = { ...this.profileData };
      this.isEditModalOpen = true;
    },
    closeEditModal() {
      this.isEditModalOpen = false;
    },
    async geocodeAddressForUpdate() {
      const address = `${this.editData.streetName} ${this.editData.streetNumber}, ${this.editData.city}, ${this.editData.country}`;
      console.log("Geocoding updated address:", address);


      const apiKey = "d5ecc7a49e6b489b911416cad59b056a";
      const url = `https://api.opencagedata.com/geocode/v1/json?q=${encodeURIComponent(
          address
      )}&key=${apiKey}`;

      try {
        const response = await axios.get(url);
        console.log("OpenCage response for update:", response);

        if (response.data && response.data.results && response.data.results.length > 0) {
          const location = response.data.results[0].geometry;
          this.editData.latitude = location.lat;
          this.editData.longitude = location.lng;
          console.log("Geocoded new location:", location);
        } else {
          throw new Error("No results found for the provided address.");
        }
      } catch (error) {
        console.error("Error during geocoding:", error.message);
        throw error;
      }
    },

    async updateProfile() {
      const token = localStorage.getItem('token');
      try {
        await this.geocodeAddressForUpdate();

        const response = await axios.put('http://localhost:8080/api/users/edit-profile', this.editData, {
          headers: { Authorization: `Bearer ${token}` }
        });

        if (response.status === 200) {
          alert('Profile updated successfully!');
          this.profileData = { ...this.editData };
          this.closeEditModal();
        }
      } catch (error) {
        console.error('Error updating profile:', error);
        if (error.message.includes("geocoding")) {
          alert('Could not get coordinates for the new address. Please check the address and try again.');
        } else {
          alert('Failed to update profile.');
        }
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
    toggleFollowersList() {
      this.isFollowersListVisible = !this.isFollowersListVisible;
    },
    toggleFollowingList() {
      this.isFollowingListVisible = !this.isFollowingListVisible;
    },
    viewPost(postId) {
      this.$router.push({ name: 'PostDetails', params: { id: postId } });
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString();
    },
  }
};
</script>


<style scoped>
.user-stats {
  display: flex;
  justify-content: center;
  gap: 20px; /* Space between buttons */
  margin-bottom: 15px; /* Add spacing below the buttons */
}

.user-stats button {
  background: none;
  border: none;
  font-size: 1.2rem;
  font-weight: bold;
  cursor: pointer;
  color: #007bff; /* Use your preferred color */
}

.user-stats button:hover {
  text-decoration: underline;
}

.dropdown-list ul {
  list-style-type: none; /* Remove dots */
  padding: 0; /* Remove default padding */
  margin: 0; /* Remove default margin */
}

.dropdown-list li {
  margin: 5px 0; /* Add spacing between list items */
}

.dropdown-list li a {
  text-decoration: none;
  color: #007bff; /* Use your preferred color */
}

.dropdown-list li a:hover {
  text-decoration: underline;
}
/* Profile Section */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5); /* Semi-transparent overlay */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px; /* Adds space around the edges of the viewport */
  box-sizing: border-box;
}

.modal-content {
  background: white;
  padding: 30px; /* Space inside the modal content */
  border-radius: 10px;
  width: 600px; /* Width of the modal content */
  max-width: 100%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

h2 {
  margin-bottom: 20px;
  text-align: center;
  font-size: 1.5em;
}

form {
  display: flex;
  flex-direction: column;
  gap: 15px; /* Razmak između elemenata */
}

label {
  font-weight: bold;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-size: 1rem;
}

button {
  padding: 10px 20px;
  font-size: 1rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button[type="submit"] {
  background-color: #007bff;
  color: white;
  margin-top: 15px;
}

button[type="button"] {
  background-color: #ccc;
  color: black;
}

button:hover {
  opacity: 0.9;
}

.edit-profile-form label {
  font-weight: bold;
  display: block;
  margin-bottom: 5px;
}

.modal-buttons {
  display: flex;
  justify-content: space-between;
}

.save-btn {
  background-color: #007bff;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background-color: #ccc;
  color: black;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.save-btn:hover {
  background-color: #0056b3;
}

.cancel-btn:hover {
  background-color: #bbb;
}
.user-profile {
  max-width: 800px;
  margin: 30px auto;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}
.scrollable-list {
  max-height: 300px; /* Podesite maksimalnu visinu prema vašim potrebama */
  overflow-y: auto;  /* Omogućava vertikalno pomeranje */
  padding: 0;
  margin: 0;
  list-style: none;
}
.profile-info {
  flex: 1 1 60%;
  margin-right: 20px;
}

.user-details {
  text-align: center;
  margin: 0 auto;
  max-width: 600px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
.user-name span {
  font-size: 1.5rem;
  font-weight: 600;
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
  margin: 20px auto;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.posts-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding: 20px;
}

.post-card {
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 10px rgba(0, 0, 0, 0.15);
}

.post-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.post-card p {
  margin: 10px 15px;
  font-size: 14px;
  color: #333;
}

.post-date {
  margin: 10px 15px 15px;
  font-size: 12px;
  color: #888;
  text-align: right;
}

.edit-button {
  background: none;
  border: none;
  cursor: pointer;
  color: #007bff;
  margin-left: 10px;
  size:0.5cm;
}

.edit-button i {
  margin-right: 5px;
}

.edit-profile-form {
  margin-top: 20px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.edit-profile-form input {
  width: 100%;
  padding: 10px;
  margin: 10px 0;
  border-radius: 4px;
  border: 1px solid #ccc;
}

</style>