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
import UsersView from '@/views/UsersView.vue';
import PostDetailsView from '@/views/PostDetailsView.vue';
import CheckUserView from '@/views/CheckUserView.vue';
import AnalyticsView from '@/views/AnalyticsView.vue';
import ChatRoomView from '@/views/ChatRoomView.vue';
import MyPostView from '@/views/MyPostView.vue';
import MyFollowersView from '@/views/MyFollowersView.vue';
import MyFollowingView from '@/views/MyFollowingView.vue';
import MapView from '@/views/MapView.vue';

import axios from 'axios';

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
      meta: {
        roles: ['registered']
      }
    },
    {
      path: '/Posts',
      name: 'Posts',
      component: PostsView,
    },
    {
      path: '/MyPosts',
      name: 'MyPosts',
      component: MyPostView,
      meta: {
        roles: ['admin', 'registered']
      }
    },
    {
      path: '/MyFollowing',
      name: 'MyFollowing',
      component: MyFollowingView,
      meta: {
        roles: ['registered']
      }
    },
    {
      path: '/MyFollowers',
      name: 'MyFollowers',
      component: MyFollowersView,
      meta: {
        roles: ['registered']
      }
    },
    {
      path: '/FollowedPosts',
      name: 'FollowedPosts',
      component: FollowedPostsView,
      meta: {
        roles: ['admin', 'registered']
      }
    },
    {
      path: '/Trends',
      name: 'Trends',
      component: TrendsView,
      meta: {
        roles: ['admin', 'registered']
      }
    },
    {
      path: '/NearbyPosts',
      name: 'NearbyPosts',
      component: NearbyPostsView,
      meta: {
        roles: ['admin', 'registered']
      }
    },
    {
      path: '/Chat',
      name: 'Chat',
      component: ChatView,
      meta: {
        roles: ['admin', 'registered']
      }
    },
    {
      path: '/ChatRoom/:roomId',
      name: 'ChatRoom',
      component: ChatRoomView,
      meta: {
        roles: ['admin', 'registered']
      }
    },
    {
      path: '/Profile',
      name: 'Profile',
      component: ProfileView,
      meta: {
        roles: ['admin', 'registered']
      }
    },
    {
      path: '/Map',
      name: 'Map',
      component: MapView,
      meta: {
        roles: ['admin', 'registered']
      }
    },
    {
      path: '/Users',
      name: 'Users',
      component: UsersView,
      meta: {
        roles: ['admin']
      }
    },
    {
      path: '/PostDetails/:postId',
      name: 'PostDetails',
      component: PostDetailsView
    },
    {
      path: '/Analytics',
      name: 'Analytics',
      component: AnalyticsView,
      meta: {
        roles: ['admin']
      }
    },
    {
      path: '/CheckUser/:userId',
      name: 'CheckUser',
      component: CheckUserView,
      meta: {
        roles: ['admin', 'registered']
      },
      // beforeEnter: async (to, from, next) => {
      //   console.log('USLOOO')
      //   try{
      //     const token = localStorage.getItem('token');
      //     if (!token) {
      //       return next({ name: 'Login' });
      //     }

      //     const response = await axios.post("http://localhost:8081/api/authentication/jwt/decode", { token })
      //     const loggedInUserId = response.data.id;
      //     const userIdFromRoute = to.params.userId;
      
      //     if (loggedInUserId === userIdFromRoute) {
      //       console.log('ASBVQASVBAS')
      //       return next(from.fullPath);
      //     } else {
      //       next();
      //     }
      //   } catch (error) {
      //   console.error('Error decoding token:', error);
      //   next({ name: 'Login' });
      //   }
      // }
    }
  ]
})

//Global navigation guard
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token');

  if (token) {
    try {
      const response = await axios.post("http://localhost:8081/api/authentication/jwt/decode", { token })
      console.log(response)
      //console.log('ISPIS U ROUTERU')

      if (response.status == 200) {
        let { id, username, role } = response.data
        //console.log(id, username, role)

        if (to.meta.roles) {
          if (to.meta.roles.includes(role)) {
            return next();
          } else {
            return next({ name: "home" });
          }
        } else {
          return next();
        }
      }
    } catch (error) {
      console.error('Failed to decode token:', error);
      // Redirect to the login page if there is an error decoding the token
      return next({ name: 'home' });
    }
  } else {
    if (to.meta.roles) {
      return next({ name: "home" });
    }
  }

  // Allow navigation to proceed if no token or if token is valid
  next();
});

export default router
