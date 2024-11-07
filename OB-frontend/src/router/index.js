import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LogInView from '@/views/LogInView.vue';
import RegistrationView from '@/views/RegisterView.vue';
import CreatePostView from '@/views/CreatePostView.vue';  
import PostsView from '@/views/PostsView.vue';
import FollowedPostsView from '@/views/FollowedPostsView.vue';
import NearbyPostsView from '@/views/NearbyPostsView.vue';
import TrendsView from '@/views/TrendsView.vue';
import ChatView from '@/views/ChatView.vue';
import ProfileView from '@/views/ProfileView.vue';

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
      path: '/FollowedPosts',
      name: 'FollowedPosts',
      component: FollowedPostsView,
    },
    {
      path: '/Trends',
      name: 'Trends',
      component: TrendsView,
    },
    {
      path: '/NearbyPosts',
      name: 'NearbyPosts',
      component: NearbyPostsView,
    },
    {
      path: '/Chat',
      name: 'Chat',
      component: ChatView,
    },
    {
      path: '/Profile',
      name: 'Profile',
      component: ProfileView,
    }
  ]
})

export default router
