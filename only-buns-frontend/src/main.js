import { createApp } from 'vue'
import App from './App.vue'
import router from './router';
import axios from 'axios';

import 'bootstrap/dist/css/bootstrap.min.css'; 
import 'bootstrap'; 

axios.defaults.baseURL = 'http://localhost:8080/api';

createApp(App).use(router).mount('#app');




