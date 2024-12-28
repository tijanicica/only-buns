<template>
  <div class="profile-container">
    <h2>Map of Nearby Bunnies</h2>
    <div id="map" style="height: 500px; margin-top: 20px;"></div>
  </div>
</template>

<script>
import L from "leaflet";
import "leaflet-control-geocoder";
import "leaflet/dist/leaflet.css";
import axios from "axios";
import { jwtDecode } from "jwt-decode";

export default {
  data() {
    return {
      map: null,
      markers: [],
      userLocation: { lat: null, lng: null }, // User location
    };
  },
  created() {
    const token = localStorage.getItem('token');
    const decoded = jwtDecode(token);
    const email = decoded.sub;
    
    if (token && email) {
      this.fetchProfileData(email, token);  // Pass these values to the method
    } else {
      console.error('Token or email not found');
    }


  },
  methods: {
    async fetchProfileData(email, token) {
      try {
        const response = await axios.get(`http://localhost:8080/api/users/maps-profile/${email}`, {
          headers: { Authorization: `Bearer ${token}` }
        });
        const user = response.data;

        console.log(user);

          // Check if address exists and extract latitude and longitude
          if (user.address && user.address.latitude && user.address.longitude) {
          this.userLocation = { lat: user.address.latitude, lng: user.address.longitude };
          } else {
          console.error("User location is not set.");
          return;
          }

          // Initialize the map
          this.map = L.map("map").setView([this.userLocation.lat, this.userLocation.lng], 13);
          L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(this.map);

          // Add a marker for the user's location
          L.marker([this.userLocation.lat, this.userLocation.lng])
          .addTo(this.map)
          .bindPopup("Your Location")
          .openPopup();

          // Load nearby posts
          await this.loadNearbyPosts();
          } catch (error) {
          console.error("Error fetching user profile or initializing map:", error.message);
          }
    },
    async loadNearbyPosts() {
    try {
      const response = await axios.get("http://localhost:8080/api/posts/all-maps", {});
      const posts = response.data;

      posts.forEach((post) => {
        // Check if post has location
        if (post.location && post.location.latitude && post.location.longitude) {
          const marker = L.marker([post.location.latitude, post.location.longitude]).addTo(this.map);

          // Bind a tooltip to the marker for hovering over it
          marker.bindTooltip(`
            ${post.description}
          `, { permanent: false, sticky: true }); // Make the tooltip sticky

          // Adding click functionality to navigate to the PostDetailsPage
          marker.on('click', () => {
            this.$router.push({ name: 'PostDetails', params: { id: post.id } });
          });

          this.markers.push(marker);
        }
      });
    } catch (error) {
      console.error("Error fetching nearby posts:", error.message);
    }
  }

  },
};
</script>

<style scoped>
.profile-container {
  text-align: center;
  margin-top: 20px;
}
#map {
  height: 500px;
  width: 100%;
}
</style>
