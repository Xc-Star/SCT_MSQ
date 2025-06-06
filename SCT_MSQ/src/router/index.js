import { createRouter, createWebHistory } from 'vue-router'
import MsqView from '../views/MsqView.vue'
import IndexView from '../views/index.vue';

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
    }
  ]
})

export default router 