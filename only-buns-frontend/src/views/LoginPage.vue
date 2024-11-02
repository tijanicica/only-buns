<template>
  <div class="container mt-5">
    <h2 class="text-center">Login</h2>
    <form @submit.prevent="submitForm" class="mx-auto mt-4" style="max-width: 500px;">
      <div class="form-group mb-3">
        <label for="email">Email:</label>
        <input type="email" v-model="form.email" @blur="validateEmail" class="form-control" />
        <span v-if="errors.email" class="text-danger">{{ errors.email }}</span>
      </div>
      <div class="form-group mb-3">
        <label for="password">Password:</label>
        <input type="text" v-model="form.password" @blur="validatePassword" class="form-control" />
        <span v-if="errors.password" class="text-danger">{{ errors.password }}</span>
      </div>
      <button type="submit" class="btn btn-primary w-100">Login</button>
    </form>
  </div>
</template>

<script>
//import axios from "axios";
export default {
  data() {
    return {
      form: {
        email: "",
        password: "",
      },
      errors: {}
    };
  },
  methods: {
    validateEmail() {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!this.form.email) {
        this.errors.email = "Email is required.";
      } else if (!emailRegex.test(this.form.email)) {
        this.errors.email = "Email is in wrong format.";
      } else {
        this.errors.email = "";
      }
    },
    validatePassword() {
      if (!this.form.password) {
        this.errors.password = "Password is required.";
      } else {
        this.errors.password = "";
      }
    },
    submitForm() {
      this.validateEmail();
      this.validatePassword();
      
      

      if (Object.values(this.errors).every((error) => error === "")) {
        alert("Successful login!");
      
      } else {
        alert("Login unsuccessful.Please fill out all required fields.");
      }
    }
  }
};
</script>

<style scoped>
.login {
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

  