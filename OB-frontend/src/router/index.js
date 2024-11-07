import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LogInView from '@/views/LogInView.vue';
import RegistrationView from '@/views/RegisterView.vue';
import CreatePostView from '@/views/CreatePostView.vue';  
import PostsView from '@/views/PostsView.vue';
import UsersView from '@/views/UsersView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/LogIn',
      name: 'LogIn',
      component: LogInView
    },
    {
      path: '/Registration',
      name: 'Registration',
      component: RegistrationView,
    },
    {
      path: '/CreatePost',
      name: 'CreatePost',
      component: CreatePostView,
    },
    {
      path: '/Posts',
      name: 'Posts',
      component: PostsView,
    },
    {
      path: '/Users',
      name: 'Users',
      component: UsersView,
    }
  ]
})

export default router
