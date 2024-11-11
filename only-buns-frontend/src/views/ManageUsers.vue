<template>
  <div class="container mt-5">
    <div class="header text-center mb-3">
      <img src="@/assets/bunny-logo.jpg" alt="Logo" class="logo" />
      <h2>Search Registered Users</h2>
    </div>

    <div class="search-container">
      <div class="row">
        <div class="col-md-3">
          <label for="firstName">Search by First Name</label>
          <input type="text" v-model="searchQuery.firstName" class="form-control" />
        </div>
        <div class="col-md-3">
          <label for="lastName">Search by Last Name</label>
          <input type="text" v-model="searchQuery.lastName" class="form-control" />
        </div>
        <div class="col-md-3">
          <label for="email">Search by Email</label>
          <input type="email" v-model="searchQuery.email" class="form-control" />
        </div>
        <div class="col-md-3">
          <label for="minPosts">Min Posts</label>
          <input type="number" v-model="searchQuery.minPosts" class="form-control" min="0" />
        </div>
      </div>
      <div class="row mt-3">
        <div class="col-md-3">
          <label for="maxPosts">Max Posts</label>
          <input type="number" v-model="searchQuery.maxPosts" class="form-control" min="0" />
        </div>
        <div class="col-md-3">
          <label>Sort by</label>
          <select v-model="sortBy" class="form-control">
            <option value="followersNumber">Followers Number</option>
            <option value="email">Email</option>
          </select>
        </div>
        <div class="col-md-3">
          <label>Order</label>
          <select v-model="order" class="form-control">
            <option value="asc">Ascending</option>
            <option value="desc">Descending</option>
          </select>
        </div>
        <div class="col-md-3">
          <button @click="searchUsers" class="btn btn-primary w-100 mt-4">Search</button>
        </div>
      </div>
    </div>

    <div v-if="users.length > 0" class="user-list mt-4">
      <div v-for="user in filteredUsers" :key="user.id" class="card mb-4">
        <div class="card-body">
          <h5 class="card-title">{{ user.firstName }} {{ user.lastName }}</h5>
          <p class="card-text">Email: {{ user.email }}</p>
          <p class="card-text">Number of Posts: {{ user.postsCount }}</p>
          <p class="card-text">Number of Followers: {{ user.followersCount }}</p>
          <button v-if="!user.isAdmin" @click="makeUserAdmin(user.id)" class="btn btn-success mt-2">Make User Admin</button>
        </div>
      </div>
    </div>

    <div v-else>
      <p>No users available.</p>
    </div>

    <div class="pagination mt-3">
      <button 
        @click="goToPage(page - 1)" 
        :disabled="page <= 0" 
        class="pagination-btn">
        <i class="fas fa-chevron-left"></i> 
      </button>
      <span class="pagination-page">Page {{ page + 1 }}</span>
      <button 
        @click="goToPage(page + 1)" 
        :disabled="page >= totalPages - 1" 
        class="pagination-btn">
        <i class="fas fa-chevron-right"></i>
      </button>
    </div>

    <div v-if="error" class="alert alert-danger mt-3 text-center">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { jwtDecode } from "jwt-decode";

export default {
  data() {
    return {
      searchQuery: {
        firstName: "",
        lastName: "",
        email: "",
        minPosts: null,
        maxPosts: null,
      },
      users: [],
      sortBy: "followersNumber",
      order: "asc",
      error: false,
      errorMessage: "",
      page: 0,          
      pageSize: 10,     
      totalPages: 1,    
    };
  },
  computed: {
    filteredUsers() {
      return this.users;
    },
  },
  created() {
    const token = localStorage.getItem("token");

    if (token) {
      const hasAdminRole = this.getRolesFromToken(token);
      if (hasAdminRole) {
        this.searchUsers(); 
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

    async searchUsers() {
      const token = localStorage.getItem("token");

      if (!token) {
        console.error("No token found. Please log in to search users.");
        return;
      }

      try {
        const response = await axios.get("http://localhost:8080/api/users/search", {
          params: {
            ...this.searchQuery,  
            sortBy: this.sortBy,
            order: this.order,
            page: this.page,
            size: this.pageSize
          },
          headers: {
            Authorization: `Bearer ${token}`, 
          },
        });

        this.users = response.data.content; 
        this.totalPages = response.data.totalPages; 

        await Promise.all(
          this.users.map(async (user) => {
            const postsResponse = await axios.get(`http://localhost:8080/api/users/${user.id}/postsCount`, {
              headers: { Authorization: `Bearer ${token}` },
            });
            user.postsCount = postsResponse.data;

            const followersResponse = await axios.get(`http://localhost:8080/api/users/${user.id}/followersCount`, {
              headers: { Authorization: `Bearer ${token}` },
            });
            user.followersCount = followersResponse.data;
          })
        );
      } catch (error) {
        console.error("Error fetching users:", error);
        this.error = true;
        this.errorMessage = "There was an error fetching users. Please try again later.";
      }
    },

    async makeUserAdmin(userId) {
  const token = localStorage.getItem("token");

  if (!token) {
    console.error("No token found. Please log in to make this user an admin.");
    return;
  }

  try {
    const updatedUser = this.users.find((user) => user.id === userId);

    if (updatedUser && updatedUser.isAdmin) {
      this.error = true;
      this.errorMessage = "This user is already an admin.";
      return;
    }

    await axios.put(`http://localhost:8080/api/users/${userId}/makeAdmin`, null, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (updatedUser) {
      updatedUser.isAdmin = true;
    }
  } catch (error) {
    console.error("Error making user an admin:", error);
    this.error = true;
    this.errorMessage = "There was an error making the user an admin. Please try again later.";
  }
}
,

    goToPage(pageNumber) {
      if (pageNumber >= 0 && pageNumber < this.totalPages) {
        this.page = pageNumber;
        this.searchUsers(); 
      }
    },
  },
};
</script>

<style scoped>
.header {
  margin-bottom: 20px;
}

.logo {
  width: 150px;
  height: auto;
}

.search-container {
  width: 100%;
  margin-bottom: 20px;
}

.row {
  margin-bottom: 10px;
}

button {
  width: 100%;
  margin-top: 20px;
}

.card {
  margin-bottom: 15px;
  padding: 10px;
}

.card-title {
  font-size: 1.2em;
  font-weight: bold;
}

.card-text {
  font-size: 1em;
  color: #555;
}

.alert {
  font-size: 1.2em;
}

input[type="text"],
input[type="email"],
input[type="number"],
select {
  width: 100%;
  padding: 8px;
  margin-bottom: 10px;
}

.search-container label {
  display: block;
  margin-bottom: 5px;
}

.row input {
  width: 100%;
}

button {
  width: 100%;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.pagination-btn {
  background-color: #007bff; 
  color: white;              
  border: none;
  padding: 10px 15px;        
  font-size: 1.1em;          
  cursor: pointer;         
  border-radius: 5px;       
  transition: background-color 0.3s ease;
}

.pagination-btn:disabled {
  background-color: #d6d6d6; 
  cursor: not-allowed;      
}

.pagination-btn:hover:not(:disabled) {
  background-color: #0056b3; 
}

.pagination-page {
  font-size: 1.1em;
  color: #555;
  padding: 5px 10px;
  background-color: #f0f0f0;
  border-radius: 5px;
  font-weight: bold;
}
</style>
