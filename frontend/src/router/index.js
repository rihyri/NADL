import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/user/LoginView.vue'
import SignupView from '@/views/user/SignupView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignupView
    }
  ],
})

export default router