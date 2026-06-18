import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Ajoute automatiquement le token Keycloak à chaque requête
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const annonceService = {
  // Récupère toutes les annonces validées (public)
  getAnnonces() {
    return api.get('/annonces')
  },

  // Crée une nouvelle annonce (vendeur connecté)
  creerAnnonce(annonce) {
    return api.post('/annonces', annonce)
  },

  // Récupère les annonces du vendeur connecté
  getMesAnnonces() {
    return api.get('/annonces/mes-annonces')
  }
}

export const notificationService = {
  // Récupère les notifications de l'utilisateur connecté
  getNotifications() {
    return api.get('/notifications')
  },

  // Marque une notification comme lue
  marquerCommeLue(id) {
    return api.put(`/notifications/${id}/lue`)
  }
}

export default api