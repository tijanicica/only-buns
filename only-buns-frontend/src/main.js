import { createApp } from 'vue'
import App from './App.vue'
import router from './router';
import axios from 'axios';

import 'bootstrap/dist/css/bootstrap.min.css'; 
import 'bootstrap'; 

//import { MdFavorite, MdComment } from 'vue-material-design-icons';
import '@fortawesome/fontawesome-free/css/all.css';


axios.defaults.baseURL = 'http://localhost:8080/api';

const app = createApp(App);



app.use(router).mount('#app');