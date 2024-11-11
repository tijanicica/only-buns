<template>
    <div class="post-creation-form">
        <form @submit.prevent="createPost">
            <input v-model="post.description" placeholder="Post description" required />
            <input type="file" @change="handleFileChange" required />
            <input v-model="post.location.streetName" placeholder="Street Name" required />
            <input v-model="post.location.streetNumber" placeholder="Street Number" required />
            <input v-model="post.location.city" placeholder="City" required />
            <input v-model="post.location.country" placeholder="Country" required />
            <button type="submit">Create Post</button>
        </form>
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
            }
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
                .then(response => {
                    console.log("Post created successfully:", response.data);
                })
                .catch(error => {
                    console.error("Error creating post:", error);
                });
            } else {
                console.error("Post data is incomplete");
            }
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
  font-size: 24px;
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
  font-size: 14px;
  color: #333;
}

input,
textarea {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
  width: 100%;
  box-sizing: border-box;
}

textarea {
  resize: vertical;
  height: 150px;
}

button {
  align-self: center;
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #0056b3;
}
  </style>
  