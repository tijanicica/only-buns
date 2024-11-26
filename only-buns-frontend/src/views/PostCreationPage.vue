<template>
  <div class="post-creation-page">
    <h2>Create a New Post</h2>
    <form @submit.prevent="createPost" class="post-form">
      <div class="form-group">
        <label for="description">Post Description</label>
        <input v-model="post.description" id="description" placeholder="Enter post description" required />
      </div>
      <div class="form-group">
        <label for="file">Post Photo</label>
        <input ref="fileInput" type="file" @change="handleFileChange" required />
      </div>
      <div class="form-group">
        <label for="streetName">Street Name</label>
        <input v-model="post.location.streetName" id="streetName" placeholder="Street Name" required />
      </div>
      <div class="form-group">
        <label for="streetNumber">Street Number</label>
        <input v-model="post.location.streetNumber" id="streetNumber" placeholder="Street Number" required />
      </div>
      <div class="form-group">
        <label for="city">City</label>
        <input v-model="post.location.city" id="city" placeholder="City" required />
      </div>
      <div class="form-group">
        <label for="country">Country</label>
        <input v-model="post.location.country" id="country" placeholder="Country" required />
      </div>
      <button type="submit">Create Post</button>
    </form>

    <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      post: {
        description: '',
        photo: '',
        location: {
          streetName: '',
          streetNumber: '',
          city: '',
          country: ''
        }
      },
      successMessage: ''
    };
  },
  methods: {
    handleFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onloadend = () => {
          this.post.photo = reader.result;
        };
        reader.readAsDataURL(file);
      }
    },
    createPost() {
  if (this.post && this.post.description && this.post.photo && this.post.location) {
    const postData = {
      description: this.post.description,
      photo: this.post.photo,
      location: this.post.location
    };

    // Send data to backend
    axios.post('http://localhost:8080/api/posts/create', postData, {
      headers: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    })
    .then(() => {
      this.successMessage = "Post created successfully!";
      // Clear form fields after successful post creation
      this.resetForm();
    })
    .catch(error => {
      console.error("Error creating post:", error);
    });
  } else {
    console.error("Post data is incomplete");
  }
}
,
    resetForm() {
      this.post.description = '';
      this.post.location.streetName = '';
      this.post.location.streetNumber = '';
      this.post.location.city = '';
      this.post.location.country = '';
      this.$refs.fileInput.value = null;
    }
  }
};
</script>

<style scoped>
.post-creation-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 30px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
}

.post-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

input {
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 14px;
  width: 100%;
  box-sizing: border-box;
}

button {
  padding: 12px 24px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  align-self: center;
}

button:hover {
  background-color: #0056b3;
}

.success-message {
  margin-top: 20px;
  padding: 15px;
  background-color: #28a745;
  color: white;
  text-align: center;
  border-radius: 4px;
  font-size: 16px;
}
</style>
