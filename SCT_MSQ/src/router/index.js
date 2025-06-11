import { createRouter, createWebHistory } from 'vue-router'
import MsqView from '../views/MsqView.vue'
import IndexView from '../views/index.vue';
import Login from '../views/admin/Login.vue'
import Main from '../views/admin/Main.vue'
import QuestionnaireReview from '../views/admin/QuestionnaireReview.vue'
import QuestionnaireManage from '../views/admin/QuestionnaireManage.vue'
import ImageManage from '../views/admin/ImageManage.vue'
import MsqReviewView from '../views/admin/components/MsqReviewView.vue'
import SubmitSuccess from '../views/SubmitSuccess.vue'

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
      path: '/msq/success',
      name: 'submitSuccess',
      component: SubmitSuccess
    },
    {
      path: '/admin/login',
      name: 'adminLogin',
      component: Login
    },
    {
      path: '/admin',
      redirect: '/admin/main'
    },
    {
      path: '/admin/main',
      name: 'adminMain',
      component: Main,
      redirect: '/admin/main/questionnaire-review',
      children: [
        {
          path: 'questionnaire-review',
          name: 'questionnaireReview',
          component: QuestionnaireReview
        },
        {
          path: 'questionnaire-manage',
          name: 'questionnaireManage',
          component: QuestionnaireManage
        },
        {
          path: 'image-manage',
          name: 'imageManage',
          component: ImageManage
        },
        {
          path: 'msq-review/:id',
          name: 'msq-review',
          component: MsqReviewView
        }
      ]
    }
  ]
})

export default router 