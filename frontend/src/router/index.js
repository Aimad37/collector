import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/DashboardView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/annonce/nouvelle',
      name: 'nouvelle-annonce',
      component: () => import('../views/AnnonceFormView.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// Vérifie l'authentification avant chaque navigation
router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    authStore.login()
    return false
  }
})

export default router