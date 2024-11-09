import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import LoginPage from '@/views/LoginPage.vue';
import RegisterPage from '@/views/RegisterPage.vue';
import UserProfilePage from '@/views/UserProfilePage.vue'
import UserHomePage from '@/views/UserHomePage.vue'; 
import YourProfilePage from '@/views/YourProfilePage.vue';
import ChatPage from '@/views/ChatPage.vue';
import FriendsPostsPage from '@/views/FriendsPostsPage.vue';
import MapPage from '@/views/MapPage.vue';
import TrendsPage from '@/views/TrendsPage.vue';

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
    component: UserHomePage,
  },
  {
    path: '/your-profile',
    name: 'YourProfile',
    component: YourProfilePage,
  },
  {
    path: '/chat',
    name: 'Chat',
    component: ChatPage,
  },
  {
    path: '/friends-bunnies',
    name: 'FriendsPosts',
    component: FriendsPostsPage,
  },
  {
    path: '/map',
    name: 'Map',
    component: MapPage,
  },
  {
    path: '/trends',
    name: 'Trends',
    component: TrendsPage,
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
