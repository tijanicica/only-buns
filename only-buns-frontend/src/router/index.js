import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '@/views/HomePage.vue';
import LoginPage from '@/views/LoginPage.vue';
import RegisterPage from '@/views/RegisterPage.vue';
import UserProfilePage from '@/views/UserProfilePage.vue';
import UserHomePage from '@/views/UserHomePage.vue'; 
import YourProfilePage from '@/views/YourProfilePage.vue';
import ChatPage from '@/views/ChatPage.vue';
import FriendsPostsPage from '@/views/FriendsPostsPage.vue';
import MapPage from '@/views/MapPage.vue';
import TrendsPage from '@/views/TrendsPage.vue';
import AdminHomePage from '@/views/AdminHomePage.vue';
import PostDetailsPage from '@/views/PostDetailsPage.vue'; 
import PostCreationPage from '@/views/PostCreationPage.vue';

import { jwtDecode } from 'jwt-decode';

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
    beforeEnter: (to, from, next) => {
      const token = localStorage.getItem("token");
      if (token) {
        const decodedToken = jwtDecode(token);
        if (decodedToken.role === "USER") {
          next();  // allow access to UserHome
        } else {
          alert("You are not authorized to view this page.");
          next("/login");  // redirect to login if not authorized
        }
      } else {
        next("/login");  // redirect to login if no token found
      }
    },
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
    path: '/post-creation',
    name: 'PostCreation',
    component: PostCreationPage,
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
  // Admin Home page route
  {
    path: '/admin-home',
    name: 'AdminHome',
    component: AdminHomePage,
    beforeEnter: (to, from, next) => {
      const token = localStorage.getItem("token");
      if (token) {
        const decodedToken = jwtDecode(token);
        if (decodedToken.role === "ADMIN") {
          next();  // allow access to AdminHome
        } else {
          alert("You are not authorized to view this page.");
          next("/login");  // redirect to login if not authorized
        }
      } else {
        next("/login");  // redirect to login if no token found
      }
    },
    
  },{
    path: '/post/:id',  // Parametar za id posta
    name: 'PostDetails',
    component: PostDetailsPage, // Nova stranica za prikaz detalja posta
    props: true,  // Omogućava automatski prosleđivanje parametara
  },
];

const router = createRouter({

  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
