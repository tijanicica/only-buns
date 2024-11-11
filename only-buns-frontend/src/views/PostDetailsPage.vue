<template>
  <div class="container mt-5">
    <!-- Header with Logo -->
    <div class="header">
      <img src="@/assets/bunny-logo.jpg" alt="Logo" class="logo" />
    </div>

    <div v-if="post" class="post-detail">
      <!-- Post Image with Edit and Delete Icons on Top Right -->
      <div class="post-image-container">
        <img :src="post.photo" class="card-img-top post-image" alt="Post Image" v-if="post.photo" />
        
        <!-- Edit and Delete Icons (Only visible if the user is the post creator) -->
        <div v-if="isPostCreator" class="edit-delete-container">
          <i class="fas fa-edit icon" @click="toggleEditMode"></i>
          <i class="fas fa-trash-alt icon" @click="confirmDeletePost"></i>
        </div>
      </div>

      <!-- Post Title (Description) -->
      <h2 class="post-title">{{ post.description }}</h2>

      <!-- Editable Post Section -->
      <div v-if="isEditing">
        <textarea v-model="updatedDescription" class="form-control" placeholder="Edit the post content..."></textarea>
        <button @click="updatePost" class="btn btn-primary mt-2">Save</button>
        <button @click="cancelEdit" class="btn btn-secondary mt-2">Cancel</button>
      </div>

      <div v-else>
        <!-- Post Details -->
        <p class="post-details">Posted by: {{ post.creatorUsername }}</p>
        <p class="post-details">Posted at: {{ formatDate(post.createdAt) }}</p>

        <!-- Like and Comment Count with Icons -->
        <div class="like-comment-container">
          <div class="like-section">
            <i class="fas fa-thumbs-up icon"></i>
            <span>{{ likesCount }} Likes</span>
          </div>
          <div class="comment-section">
            <i class="fas fa-comment icon"></i>
            <span>{{ commentsCount }} Comments</span>
          </div>
        </div>
      </div>

      <!-- Comment Box and Submit Button (Only shown for authenticated users) -->
      <div v-if="isAuthenticated" class="comment-box">
        <textarea v-model="commentText" placeholder="Add a comment..." class="form-control"></textarea>
        <button @click="submitComment" class="btn btn-primary mt-2">Submit</button>
      </div>

      <!-- Display Comments -->
      <div v-if="comments && comments.length > 0" class="comments-list">
        <h3>Comments:</h3>
        <div v-for="comment in comments" :key="comment.id" class="comment">
          <p>{{ comment.content }}</p>
          <small>Posted by: {{ comment.name }} at {{ formatDate(comment.date) }}</small>
        </div>
      </div>
    </div>

    <!-- Confirmation Modal for Deletion -->
    <div v-if="showDeleteConfirmation" class="delete-confirmation-modal">
      <div class="modal-content">
        <h3>Are you sure you want to delete this post?</h3>
        <button @click="deletePost" class="btn btn-danger">Yes, Delete</button>
        <button @click="cancelDelete" class="btn btn-secondary">Cancel</button>
      </div>
    </div>

  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      post: null,
      comments: [],
      likesCount: 0,
      commentsCount: 0,
      commentText: '',
      isPostCreator: false,  
      isEditing: false, 
      updatedDescription: '', 
      showDeleteConfirmation: false, 
      isAuthenticated: false, // Track if user is authenticated
    };
  },
  created() {
    const postId = this.$route.params.id;  
    this.fetchPostDetails(postId);  

    // Check if user is authenticated (token presence)
    this.isAuthenticated = !!localStorage.getItem('token');
  },
  methods: {
    async fetchPostDetails(postId) {
      const token = localStorage.getItem('token');  // Get token from localStorage

      try {
        const response = await axios.get(`http://localhost:8080/api/posts/${postId}`, {
          headers: token ? { Authorization: `Bearer ${token}` } : {}  // Conditionally send token in headers
        });

        this.post = response.data.post;
        this.comments = response.data.comments;
        this.likesCount = response.data.likesCount || 0;  // Default to 0 if undefined
        this.commentsCount = response.data.commentsCount || 0;  // Default to 0 if undefined
        this.creatorEmail = response.data.creatorEmail;

        const currentUserEmail = this.getCurrentUserEmail();
        this.isPostCreator = this.creatorEmail === currentUserEmail;
      } catch (error) {
        console.error('Error fetching post details:', error);
      }
    },

    getCurrentUserEmail() {
      const token = localStorage.getItem('token');
      if (!token) return null;
      const decodedToken = JSON.parse(atob(token.split('.')[1])); // Decode JWT token
      return decodedToken.sub;  // Assuming email is stored in the token
    },

    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleString();
    },

    toggleEditMode() {
      this.isEditing = !this.isEditing;
      if (this.isEditing) {
        this.updatedDescription = this.post.description;
      }
    },

    async updatePost() {
      if (!this.updatedDescription) {
        console.error('Post description cannot be empty.');
        return;
      }

      const token = localStorage.getItem('token');
      if (!token) {
        console.error('No token found. Please log in to update the post.');
        return;
      }

      try {
        const response = await axios.put(`http://localhost:8080/api/posts/${this.post.id}`, {
          description: this.updatedDescription
        }, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.post.description = response.data.description;  
        this.isEditing = false;  
      } catch (error) {
        console.error('Error updating post:', error);
      }
    },

    cancelEdit() {
      this.isEditing = false;
      this.updatedDescription = '';
    },

    confirmDeletePost() {
      this.showDeleteConfirmation = true;
    },

    async deletePost() {
      const token = localStorage.getItem('token');
      if (!token) {
        console.error('No token found. Please log in to delete the post.');
        return;
      }

      try {
        await axios.delete(`http://localhost:8080/api/posts/${this.post.id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.$router.push('/user-home');  // Redirect to home or other page
      } catch (error) {
        console.error('Error deleting post:', error);
      }
    },

    cancelDelete() {
      this.showDeleteConfirmation = false;
    },

    async submitComment() {
      if (!this.commentText) {
        console.log('No comment text to submit');
        return;
      }

      const token = localStorage.getItem('token');  // Get token from localStorage

      if (!token) {
        console.error('No token found. Please log in to comment.');
        return;
      }

      try {
        const response = await axios.post(`http://localhost:8080/api/posts/${this.post.id}/comments`, {
          content: this.commentText
        }, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        this.comments.push(response.data);
        this.commentText = '';
        this.commentsCount++;
      } catch (error) {
        console.error('Error submitting comment:', error);
      }
    }
  },
};
</script>

<style scoped>
/* Styling for the header with logo */
.header {
  display: flex;
  justify-content: center;
  padding: 10px;
}

.logo {
  width: 150px;  
  height: auto;
}

/* Styling for the post details */
.post-detail {
  text-align: center;
}

.post-image-container {
  position: relative;
}

.card-img-top {
  width: 80%;
  height: auto;
  max-height: 300px;
  object-fit: cover;
  margin: 10px 0;
}

.edit-delete-container {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 10px;
}

.edit-delete-container .icon {
  font-size: 1.5em;
  cursor: pointer;
}

h2.post-title {
  font-size: 1.8em;
  font-weight: bold;
  margin-top: 10px;
}

p.post-details {
  font-size: 1.2em;
  color: #555;
  margin-top: 10px;
}

/* Styling for Like and Comment Count */
.like-comment-container {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
}

.like-section, .comment-section {
  display: flex;
  align-items: center;
}

.icon {
  font-size: 1.2em;
  margin-right: 5px;
}

.comment-box {
  width: 80%;
  margin: 10px auto;
}

textarea.form-control {
  width: 100%;
  margin-bottom: 10px;
}

.comments-list {
  margin-top: 30px;
  text-align: left;
}

.comment {
  background-color: #f8f9fa;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 5px;
}

/* Confirmation Dialog */
.delete-confirmation-modal {
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

.delete-confirmation-modal .modal-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  width: 400px; 
  max-width: 90%;
}

.delete-confirmation-modal button {
  margin-top: 10px;
}
</style>
