import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import LoginPage from '@/views/LoginPage.vue';
import RegisterPage from '@/views/RegisterPage.vue';
import UserProfilePage from '@/views/UserProfilePage.vue'
import UserHomePage from '@/views/UserHomePage.vue'; 


const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage,
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginPage,
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterPage,
  },
  {
    path: '/users/:userId',
    name: 'UserProfile',
    component: UserProfilePage,
  },
  {
    path: '/user-home',
    name: 'UserHome',
    component: UserHomePage, // Make sure this is imported
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
