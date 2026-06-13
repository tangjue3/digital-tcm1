import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      alias: '/login',
      component: () => import('../views/Login.vue'),
    },
    {
      path: '/home',
      name: 'home',
      redirect: '/home/dashboard',
      component: () => import('../views/Layout.vue'),
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('../views/HomeNew.vue'),
        },
        {
          path: 'appUser',
          name: 'appUser',
          component: () => import('../views/AppUserManagement.vue'),
        },
        {
          path: 'cglist',
          name: 'cglist',
          component: () => import('../views/cgList.vue'),
        },
        {
          path: 'medicalCaseAnalysis',
          name: 'medicalCaseAnalysis',
          component: () => import('../views/MedicalCaseAnalysis.vue'),
        },
        {
          path: 'aiTCMIntelligentAgent',
          name: 'aiTCMIntelligentAgent',
          component: () => import('../views/AITCMIntelligentAgent.vue'),
        },
        {
          path: 'detailPage',
          name: 'detailPage',
          component: () => import('../views/detailPage.vue'),
        },
        {
          path: 'patientReportDetail',
          name: 'patientReportDetail',
          component: () => import('../views/PatientReportDetail.vue'),
        },
        {
          path: 'caseAnalysisDetail',
          name: 'caseAnalysisDetail',
          component: () => import('../views/CaseAnalysisDetail.vue'),
        },

        {
          path: 'aiTCMKnowledgeBase',
          name: 'aiTCMKnowledgeBase',
          component: () => import('../views/AITCMKnowledgeBase.vue'),
        },
        {
          path: 'medicalChat',
          name: 'medicalChat',
          component: () => import('../views/MedicalChat.vue'),
        },
      ],
    },
    {
      path: '/syList',
      name: 'syList',
      component: () => import('../views/syList.vue'),
    },
    {
      path: '/fzsy',
      name: 'fzsy',
      component: () => import('../views/fzsy.vue'),
    },
    {
      path: '/result',
      name: 'result',
      component: () => import('../views/result.vue'),
    },
  ],
})

const whiteList = ['/', '/login']

router.beforeEach((to, from, next) => {
  const token = getToken()

  if (token) {
    if (whiteList.includes(to.path)) {
      next('/home')
      return
    }
    next()
    return
  }

  if (whiteList.includes(to.path)) {
    next()
    return
  }

  next({
    path: '/',
    query: {
      redirect: to.fullPath,
    },
  })
})

export default router
