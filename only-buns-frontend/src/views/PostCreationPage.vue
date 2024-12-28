<template>
    <h2>Create a New Post</h2>
    <div class="content-container">
      <div class="form-container">
        <form @submit.prevent="createPost">
          <div class="form-group">
            <label for="description">Post Description</label>
            <input v-model="post.description" id="description" placeholder="Enter post description" required />
          </div>
          <div class="form-group">
            <label for="file">Post Photo</label>
            <input ref="fileInput" type="file" @change="handleFileChange" required />
          </div>
          <!-- Latitude i Longitude pored -->
          <div class="flex-group">
            <div>
              <label for="latitude">Latitude</label>
              <input v-model="post.location.latitude" id="latitude" placeholder="Latitude" required />
            </div>
            <div>
              <label for="longitude">Longitude</label>
              <input v-model="post.location.longitude" id="longitude" placeholder="Longitude" required />
            </div>
          </div>
          <!-- Street Name i Street Number -->
          <div class="form-group">
            <label for="streetName">Street Name</label>
            <input v-model="post.location.streetName" id="streetName" placeholder="Street Name" required />
          </div>
          <div class="form-group">
            <label for="streetNumber">Street Number</label>
            <input v-model="post.location.streetNumber" id="streetNumber" placeholder="Street Number" required />
          </div>
          <!-- City i Country pored -->
          <div class="flex-group">
            <div>
              <label for="city">City</label>
              <input v-model="post.location.city" id="city" placeholder="City" required />
            </div>
            <div>
              <label for="country">Country</label>
              <input v-model="post.location.country" id="country" placeholder="Country" required />
            </div>
          </div>
          <div class="form-submit-container">
            <button type="submit">Create Post</button>
            <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
          </div>
        </form>
      </div>
      <div id="map" class="map-container"></div>
    </div>
</template>

<script>
import axios from 'axios';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

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
          country: '',
          latitude: '',
          longitude: ''
        }
      },
      successMessage: '',
      map: null
    };
  },
  mounted() {
    // Inicijalizacija Leaflet mape
    this.map = L.map('map').setView([44.8, 20.5], 7); // Centriraj na Srbiju

    // Dodaj OpenStreetMap sloj
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '© OpenStreetMap'
    }).addTo(this.map);

    // Dodaj event za klik na mapu
    this.map.on('click', this.onMapClick);
  },
  methods: {
    async onMapClick(event) {
    const { lat, lng } = event.latlng; // Dobij latitude i longitude
    this.post.location.latitude = lat.toFixed(6);
    this.post.location.longitude = lng.toFixed(6);

    //L.marker([lat, lng]).addTo(this.map);

    try {
      const response = await axios.get(
        `https://nominatim.openstreetmap.org/reverse?lat=${lat}&lon=${lng}&format=json`
      );

      const address = response.data.address;
      this.post.location.streetName = address.road || '';
      this.post.location.streetNumber = address.house_number || '';
      this.post.location.city = address.city || address.town || address.village || '';
      this.post.location.country = address.country || '';
    } catch (error) {
      console.error("Error fetching address:", error);
    }
  },
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
      this.post.location.latitude = '';
      this.post.location.longitude = '';
    }
  }
};
</script>

<style scoped>
.content-container {
  display: flex;
  width: 100%;
}

.flex-group {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  padding: 10px;
}


.flex-group > div {
  flex: 1;
}

.flex-group > div input {
  width: 100%;
  box-sizing: border-box;
}

.form-container {
  flex: 1;
  padding: 20px;
  padding-top: 80px;
  border-right: 2px solid #ddd;
  overflow-y: auto;
}

.map-container {
  flex: 2;
  height: 856px;
  border: 2px solid #ddd;
  border-radius: 8px;
  margin-left: 20px;
  margin-right: 10px;
  margin-bottom: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  font-family: 'Pacifico', cursive;
  font-size: 52px;
  color: #007bff;
  margin-bottom: 20px;
  text-align: center;
  padding-top: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 10px;
}

label {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

input {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  width: 100%;
  box-sizing: border-box;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

input:focus {
  border-color: #007bff;
  box-shadow: 0 0 4px rgba(0, 123, 255, 0.5);
}

button {
  align-self: center;
  padding: 12px 24px;
  margin: 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

button:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
}

.form-submit-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

.success-message {
  margin: 0;
  padding: 10px 15px;
  background-color: #28a745;
  color: white;
  text-align: center;
  border-radius: 8px;
  font-size: 14px;
}

@media (max-width: 768px) {
  .post-creation-page {
    width: 90%;
    padding: 15px;
  }

  button {
    font-size: 32px;
    padding: 10px 20px;
  }
}
</style>