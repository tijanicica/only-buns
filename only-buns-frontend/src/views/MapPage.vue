<template>
  <div class="map-container">
    <h2 class="map-title">See your friends nearby!</h2>
    <div id="map" class="leaflet-map"></div>
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
    mounted() {
  this.initializeMap();
},
methods: {
  initializeMap() {
    if (this.userLocation.lat && this.userLocation.lng) {
      this.map = L.map("map", {zoomAnimation: false}).setView([this.userLocation.lat, this.userLocation.lng], 13);
      L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(this.map);
    } else {
      console.error("User location is not set.");
    }
  },
},

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
  

        // Add a marker for the user's location with custom icon
        const userIcon = L.icon({
          iconUrl: "/assets/user-icon.jpg", // Put your icon image path here
          iconSize: [32, 32], // Size of the icon
          iconAnchor: [16, 32], // Anchor point of the icon
          popupAnchor: [0, -32], // Where the popup will appear
        });

        L.marker([this.userLocation.lat, this.userLocation.lng], { icon: userIcon })
          .addTo(this.map)
          .bindPopup("Your Location")
          .openPopup();

        // Load nearby posts
        await this.loadNearbyPosts();
        await this.loadNearbyLocations();
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
            const postIcon = L.icon({
              iconUrl: "/assets/post-icon.jpg", // Put your icon image path here
              iconSize: [32, 32],
              iconAnchor: [16, 32],
              popupAnchor: [0, -32],
            });

            const marker = L.marker([post.location.latitude, post.location.longitude], { icon: postIcon })
              .addTo(this.map);

            // Bind a tooltip to the marker for hovering over it
            marker.bindTooltip(`
              ${post.description}
            `, { permanent: false, sticky: true });

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
    },
    async loadNearbyLocations() {
      try {
        const response = await axios.get("http://localhost:8080/api/rabbit-care/locations");
        const locations = response.data;

        locations.forEach((location) => {
          const locationIcon = L.icon({
            iconUrl: "/assets/vet-icon.jpg", // Put your icon image path here
            iconSize: [32, 32],
            iconAnchor: [16, 32],
            popupAnchor: [0, -32],
          });

          const marker = L.marker([location.latitude, location.longitude], { icon: locationIcon })
            .addTo(this.map);

          marker.bindTooltip(location.name, { permanent: false, sticky: true });
          this.markers.push(marker);
        });
      } catch (error) {
        console.error("Error fetching locations:", error.message);
      }
    }
  },
};
</script>

<style scoped>
.map-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.map-title {
  font-size: 2rem;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.leaflet-map {
  height: 500px;
  width: 100%;
  max-width: 900px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.leaflet-container {
  border-radius: 8px;
}
</style>
