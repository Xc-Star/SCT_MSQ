import { createRouter, createWebHistory } from 'vue-router'
import MsqView from '../views/MsqView.vue'
import IndexView from '../views/index.vue';
import Login from '../views/admin/Login.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'index',
      component: IndexView
    },
    {
      path: '/msq',
      name: 'msq',
      component: MsqView
    },
    {
      path: '/admin/login',
      name: 'adminLogin',
      component: Login
    }
  ]
})

export default router 