import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import Keycloak from 'keycloak-js'

export const useAuthStore = defineStore('auth', () => {
  const keycloak = ref(null)
  const token = ref(localStorage.getItem('token') || null)
  const userInfo = ref(null)

  const isAuthenticated = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.preferred_username || '')

  // Initialise Keycloak
  async function initKeycloak() {
    const kc = new Keycloak({
      url: 'http://localhost:8180',
      realm: 'collector',
      clientId: 'collector-frontend'
    })

    keycloak.value = kc

    try {
      const authenticated = await kc.init({
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html'
      })

      if (authenticated) {
        token.value = kc.token
        localStorage.setItem('token', kc.token)
        userInfo.value = kc.tokenParsed
      }

      // Rafraîchit le token automatiquement
      setInterval(() => {
        kc.updateToken(70).then(refreshed => {
          if (refreshed) {
            token.value = kc.token
            localStorage.setItem('token', kc.token)
          }
        })
      }, 60000)

    } catch (error) {
      console.error('Erreur Keycloak:', error)
    }
  }

  // Connexion
  function login() {
    keycloak.value?.login()
  }

  // Déconnexion
  function logout() {
    keycloak.value?.logout()
    token.value = null
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return {
    token,
    userInfo,
    isAuthenticated,
    username,
    initKeycloak,
    login,
    logout
  }
})