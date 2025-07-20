<template>
  <div class="container mt-5">
    <h2 class="text-center">Register</h2>
    <div id="map" style="height: 200px; width: 20%; margin: 0 auto;"></div>  <!-- Dodaj mapu -->
    <form @submit.prevent="submitForm" class="mx-auto mt-4" style="max-width: 500px;">
      <div class="form-group mb-3">
        <label for="email">Email:</label>
        <input type="email" v-model="form.email" @blur="validateEmail" class="form-control" />
        <span v-if="errors.email" class="text-danger">{{ errors.email }}</span>
      </div>
      <div class="form-group mb-3">
        <label for="username">Username:</label>
        <input type="text" v-model="form.username" @blur="validateUsername" class="form-control" />
        <span v-if="errors.username" class="text-danger">{{ errors.username }}</span>
      </div>
      <div class="form-group mb-3">
        <label for="password">Password:</label>
        <input type="password" v-model="form.password" @blur="validatePassword" class="form-control" />
        <span v-if="errors.password" class="text-danger">{{ errors.password }}</span>
      </div>
      <div class="form-group mb-3">
        <label for="confirmPassword">Confirm Password:</label>
        <input type="password" v-model="form.confirmPassword" @blur="validateConfirmPassword" class="form-control" />
        <span v-if="errors.confirmPassword" class="text-danger">{{ errors.confirmPassword }}</span>
      </div>
      <div class="form-group mb-3">
        <label for="firstName">First Name:</label>
        <input type="text" v-model="form.firstName" @blur="validateFirstName" class="form-control" />
        <span v-if="errors.firstName" class="text-danger">{{ errors.firstName }}</span>
      </div>
      <div class="form-group mb-3">
        <label for="lastName">Last Name:</label>
        <input type="text" v-model="form.lastName" @blur="validateLastName" class="form-control" />
        <span v-if="errors.lastName" class="text-danger">{{ errors.lastName }}</span>
      </div>
      <div class="form-group mb-3">
        <label for="streetName">Street Name:</label>
        <input type="text" v-model="form.streetName" @blur="validateStreetName" class="form-control" />
        <span v-if="errors.streetName" class="text-danger">{{ errors.streetName }}</span>
      </div>
      <div class="form-group mb-3">
        <label for="streetNumber">Street Number:</label>
        <input type="text" v-model="form.streetNumber" @blur="validateStreetNumber" class="form-control" />
        <span v-if="errors.streetNumber" class="text-danger">{{ errors.streetNumber }}</span>
      </div>
      <div class="form-group mb-3">
        <label for="city">City:</label>
        <input type="text" v-model="form.city" @blur="validateCity" class="form-control" />
        <span v-if="errors.city" class="text-danger">{{ errors.city }}</span>
      </div>
      <div class="form-group mb-3">
        <label for="country">Country:</label>
        <input type="text" v-model="form.country" @blur="validateCountry" class="form-control" />
        <span v-if="errors.country" class="text-danger">{{ errors.country }}</span>
      </div>
      <button type="submit" class="btn btn-primary w-100">Register</button>
    </form>
  </div>
</template>

<script>
import axios from "axios";
import L from "leaflet";
import "leaflet-control-geocoder";
import 'leaflet-control-geocoder/dist/Control.Geocoder.css';
export default {
  data() {
    return {
      form: {
        email: "",
        username: "",
        password: "",
        confirmPassword: "",
        firstName: "",
        lastName: "",
        streetName: "",
        streetNumber: "",
        city: "",
        country: "", 
        latitude: null,
        longitude: null
      },
      errors: {}
    };
  },
  mounted() {
    this.$nextTick(() => {
      const mapContainer = document.getElementById("map");
      if (mapContainer) {
        this.map = L.map("map").setView([45.2671, 19.8310], 13);
        L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png").addTo(this.map);
      } else {
        console.error("Map container not found.");
      }
    });
  },

  methods: {
    async validateEmail() {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!this.form.email) {
        this.errors.email = "Email is required.";
      } else if (!emailRegex.test(this.form.email)) {
        this.errors.email = "Email is in wrong format.";
      } else {
        try {
          const response = await axios.post("http://localhost:8080/api/users/check-email", { email: this.form.email });
          if (response.data.exists) {
            this.errors.email = "Email already in use.";
          } else {
            this.errors.email = "";
          }
        } catch (error) {
          console.error("Error checking email:", error);
        }
      }
    },
    async validateUsername() {
      if (!this.form.username) {
        this.errors.username = "Username is required.";
      } else {
        try {
          const response = await axios.post("http://localhost:8080/api/users/check-username", { username: this.form.username });
          if (response.data.exists) {
            this.errors.username = "Username already in use.";
          } else {
            this.errors.username = "";
          }
        } catch (error) {
          console.error("Error checking username:", error);
        }
      }
    },
    validatePassword() {
      if (!this.form.password) {
        this.errors.password = "Password is required.";
      } else {
        this.errors.password = "";
      }
    },
    validateConfirmPassword() {
      if (!this.form.confirmPassword) {
        this.errors.confirmPassword = "Confirm your password.";
      } else if (this.form.confirmPassword !== this.form.password) {
        this.errors.confirmPassword = "Passwords don't match.";
      } else {
        this.errors.confirmPassword = "";
      }
    },
    validateFirstName() {
      if (!this.form.firstName) {
        this.errors.firstName = "Name is required.";
      } else {
        this.errors.firstName = "";
      }
    },
    validateLastName() {
      if (!this.form.lastName) {
        this.errors.lastName = "Last name is required.";
      } else {
        this.errors.lastName = "";
      }
    },
    validateStreetName() {
      if (!this.form.streetName) {
        this.errors.streetName = "Street name is required.";
      } else {
        this.errors.streetName = "";
      }
    },
    validateStreetNumber() {
      if (!this.form.streetNumber) {
        this.errors.streetNubmer = "Street number is required.";
      } else {
        this.errors.streetNubmer = "";
      }
    },
    validateCity() {
      if (!this.form.city) {
        this.errors.city = "City is required.";
      } else {
        this.errors.city = "";
      }
    },
    validateCountry() {
      if (!this.form.country) {
        this.errors.country = "Country is required.";
      } else {
        this.errors.country = "";
      }
    },
    async geocodeAddress() {
      const address = `${this.form.streetName} ${this.form.streetNumber}, ${this.form.city}, ${this.form.country}`;
      console.log("Geocoding address:", address);

      const apiKey = "d5ecc7a49e6b489b911416cad59b056a"; // Replace with your actual API key
      const url = `https://api.opencagedata.com/geocode/v1/json?q=${encodeURIComponent(
        address
      )}&key=${apiKey}`;

      try {
        const response = await axios.get(url);
        console.log("OpenCage response:", response);

        if (
          response.data &&
          response.data.results &&
          response.data.results.length > 0
        ) {
          const location = response.data.results[0].geometry;
          this.form.latitude = location.lat;
          this.form.longitude = location.lng;
          this.map.setView([location.lat, location.lng], 14);

          L.marker([location.lat, location.lng]).addTo(this.map);
          console.log("Geocoded location:", location);
        } else {
          throw new Error("No results found for the provided address.");
        }
      } catch (error) {
        console.error("Error during geocoding:", error.message);
        throw error;
      }
    },

async submitForm() {
  this.validateEmail();
  this.validateUsername();
  this.validatePassword();
  this.validateConfirmPassword();
  this.validateFirstName();
  this.validateLastName();
  this.validateStreetName();
  this.validateStreetNumber();
  this.validateCity();
  this.validateCountry();

  if (Object.values(this.errors).every((error) => error === "")) {
    try {
      await this.geocodeAddress();

      await axios.post("http://localhost:8080/api/users/register", this.form);
      alert("Registration successful!");
      this.$router.push({ name: 'Login' });
    } catch (error) {
      console.error("Error during form submission:", error.message);
      alert("Could not get coordinates for the address. Please try again.");
    }
  }
},

  },
};
</script>

<style scoped>
.register {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
}
.form-group {
  margin-bottom: 15px;
}
input[type="text"],
input[type="email"],
input[type="password"] {
  width: 100%;
  padding: 8px;
}
span {
  color: red;
  font-size: 0.8em;
}
button {
  padding: 10px 15px;
  background-color: #4caf50;
  color: white;
  border: none;
  cursor: pointer;
}
button:hover {
  background-color: #45a049;
}
.text-danger {
  font-size: 0.9em;
}
</style>

  