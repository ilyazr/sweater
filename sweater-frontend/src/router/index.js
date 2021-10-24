import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from "@/views/Home"
import UserProfileChange from "../views/UserProfileChange";

Vue.use(VueRouter)

const checkTokens = () => {
  let token = localStorage.getItem('user-token');
  return !!token;
}

const isAuthenticated = (to, from, next) => {
  let loggedIn = checkTokens();
  if (loggedIn) {
    next();
  } else {
    next('/login');
  }
}

const routes = [
  {
    path: '/home',
    name: 'Home',
    component: Home,
    children: [
      {
        path: '/profile',
        name: 'UserProfileNew',
        component: () => import('@/views/UserProfileNew'),
      },
      {
        path: '/change/:property',
        name: UserProfileChange,
        component: () => import('@/views/UserProfileChange'),
      },
      {
        path: '/wallmock',
        name: 'WallMock',
        component: () => import('@/views/WallMock'),
      },
      {
        path: '/wall/:username',
        name: 'Wall',
        component: () => import('@/views/Wall'),
      },
      {
        path: '/chat/:username?',
        name: 'Chat',
        component: () => import('@/views/Chat'),
      },
      {
        path: '/people',
        name: 'People',
        component: () => import('@/views/People'),
      },
      {
        path: '/friends/:username',
        name: 'Friends',
        component: () => import('@/views/Friends'),
      },
    ],
    beforeEnter: isAuthenticated
  },
  {
    path: '/login',
    name: 'Starter',
    component: () => import("@/views/Starter"),
    beforeEnter: (to, from, next) => {
      let loggedIn = checkTokens();
      if (loggedIn) {
        next(`/wall/${JSON.parse(localStorage.getItem('user-data')).username}`);
      } else {
        next();
      }
    }
  },
  {
    path: '*',
    redirect: '/login',
    beforeEnter: isAuthenticated
  },

]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
});

export default router
