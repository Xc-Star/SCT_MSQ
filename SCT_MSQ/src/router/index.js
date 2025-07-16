import { createRouter, createWebHistory } from 'vue-router'
import MsqView from '../views/MsqView.vue'
import IndexView from '../views/index.vue';
import SctServer from '../views/SctServer.vue'
import Login from '../views/admin/Login.vue'
import Main from '../views/admin/Main.vue'
import QuestionnaireReview from '../views/admin/QuestionnaireReview.vue'
import QuestionnaireManage from '../views/admin/QuestionnaireManage.vue'
import SystemManage from '../views/admin/SystemManage.vue'
import MsqReviewView from '../views/admin/components/MsqReviewView.vue'
import SubmitSuccess from '../views/SubmitSuccess.vue'
import AdminManage from '../views/admin/AdminManage.vue'
import ServerMemberManage from '../views/admin/ServerMemberManage.vue'
import BuildTool from '../views/build-tool.vue'
import Overview from '../views/overview.vue'
import MsqResultView from '../views/MsqResultView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'index',
      component: IndexView
    },
    {
      path: '/sctserver',
      name: 'sctserver',
      component: SctServer
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
          path: 'system-manage',
          name: 'systemManage',
          component: SystemManage
        },
        {
          path: 'admin-manage',
          name: 'adminManage',
          component: AdminManage
        },
        {
          path: 'server-member-manage',
          name: 'serverMemberManage',
          component: ServerMemberManage
        },
        {
          path: 'msq-review/:id',
          name: 'msq-review',
          component: MsqReviewView
        }
      ]
    },
    {
      path: '/build-tool',
      name: 'buildTool',
      component: BuildTool
    },
    {
      path: '/overview',
      name: 'overview',
      component: Overview
    },
    {
      path: '/msq/result',
      name: 'msq-result',
      component: MsqResultView
    }
  ]
})

export default router 