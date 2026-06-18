<script setup>
import { onMounted, ref } from 'vue'
import { useAnnoncesStore } from '../stores/annonces'
import { useAuthStore } from '../stores/auth'
import { notificationService } from '../services/api'
import {
  Package, Bell, Plus, TrendingUp,
  CheckCircle, Clock, XCircle, ShoppingBag
} from 'lucide-vue-next'

const annoncesStore = useAnnoncesStore()
const authStore = useAuthStore()
const notifications = ref([])
const activeTab = ref('annonces')

onMounted(async () => {
  await annoncesStore.fetchMesAnnonces()
  try {
    const response = await notificationService.getNotifications()
    notifications.value = response.data
  } catch (err) {
    console.error(err)
  }
})

const statutConfig = {
  EN_ATTENTE: { label: 'En attente',  icon: Clock,        color: '#f59e0b' },
  VALIDEE:    { label: 'Validée',     icon: CheckCircle,  color: '#10b981' },
  REJETEE:    { label: 'Rejetée',     icon: XCircle,      color: '#ef4444' },
  VENDUE:     { label: 'Vendue',      icon: ShoppingBag,  color: '#7c3aed' },
}

const notifConfig = {
  NOUVELLE_ANNONCE: '🆕',
  ANNONCE_VALIDEE:  '✅',
  ANNONCE_REJETEE:  '❌',
  CHANGEMENT_PRIX:  '💰',
}

async function marquerLue(id) {
  await notificationService.marquerCommeLue(id)
  const notif = notifications.value.find(n => n.id === id)
  if (notif) notif.lu = true
}

const nonLues = ref(0)
</script>

<template>
  <div class="dashboard">
    <div class="container">

      <!-- Header -->
<!-- Header simplifié -->
    <div class="dash-header">
      <h1 class="dash-title">Mon espace</h1>
    </div>

      <!-- Stats -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon" style="background: rgba(124,58,237,0.1)">
            <Package :size="20" style="color: #7c3aed" />
          </div>
          <div>
            <div class="stat-value">{{ annoncesStore.mesAnnonces.length }}</div>
            <div class="stat-label">Annonces</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" style="background: rgba(16,185,129,0.1)">
            <CheckCircle :size="20" style="color: #10b981" />
          </div>
          <div>
            <div class="stat-value">
              {{ annoncesStore.mesAnnonces.filter(a => a.statut === 'VALIDEE').length }}
            </div>
            <div class="stat-label">Validées</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" style="background: rgba(245,158,11,0.1)">
            <Clock :size="20" style="color: #f59e0b" />
          </div>
          <div>
            <div class="stat-value">
              {{ annoncesStore.mesAnnonces.filter(a => a.statut === 'EN_ATTENTE').length }}
            </div>
            <div class="stat-label">En attente</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" style="background: rgba(239,68,68,0.1)">
            <Bell :size="20" style="color: #ef4444" />
          </div>
          <div>
            <div class="stat-value">
              {{ notifications.filter(n => !n.lu).length }}
            </div>
            <div class="stat-label">Non lues</div>
          </div>
        </div>
      </div>

      <!-- Tabs -->
      <div class="tabs">
        <button
          :class="['tab', { active: activeTab === 'annonces' }]"
          @click="activeTab = 'annonces'"
        >
          <Package :size="16" />
          Mes annonces
        </button>
        <button
          :class="['tab', { active: activeTab === 'notifications' }]"
          @click="activeTab = 'notifications'"
        >
          <Bell :size="16" />
          Notifications
          <span v-if="notifications.filter(n => !n.lu).length > 0" class="badge">
            {{ notifications.filter(n => !n.lu).length }}
          </span>
        </button>
      </div>

      <!-- Annonces tab -->
      <div v-if="activeTab === 'annonces'" class="tab-content">
        <div v-if="annoncesStore.loading" class="loading">
          Chargement...
        </div>

        <div v-else-if="annoncesStore.mesAnnonces.length === 0" class="empty-state">
          <Package :size="40" class="empty-icon" />
          <h3>Aucune annonce</h3>
          <p>Publiez votre première annonce !</p>
          <RouterLink to="/annonce/nouvelle" class="btn-nouvelle">
            <Plus :size="16" />
            Créer une annonce
          </RouterLink>
        </div>

        <div v-else class="annonces-list">
          <div
            v-for="annonce in annoncesStore.mesAnnonces"
            :key="annonce.id"
            class="annonce-row"
          >
            <div class="annonce-image">
              <img
                :src="annonce.imageUrl || 'https://via.placeholder.com/60x60/111/333'"
                :alt="annonce.titre"
              />
            </div>

            <div class="annonce-info">
              <h4>{{ annonce.titre }}</h4>
              <span class="annonce-cat">{{ annonce.categorie.replace('_', ' ') }}</span>
            </div>

            <div class="annonce-prix">
              {{ annonce.prix }} €
            </div>

            <div
              class="annonce-statut"
              :style="{ color: statutConfig[annonce.statut]?.color }"
            >
              <component
                :is="statutConfig[annonce.statut]?.icon"
                :size="16"
              />
              {{ statutConfig[annonce.statut]?.label }}
            </div>
          </div>
        </div>
      </div>

      <!-- Notifications tab -->
      <div v-if="activeTab === 'notifications'" class="tab-content">
        <div v-if="notifications.length === 0" class="empty-state">
          <Bell :size="40" class="empty-icon" />
          <h3>Aucune notification</h3>
          <p>Vos notifications apparaîtront ici</p>
        </div>

        <div v-else class="notifs-list">
          <div
            v-for="notif in notifications"
            :key="notif.id"
            :class="['notif-row', { 'non-lue': !notif.lu }]"
          >
            <span class="notif-icon">{{ notifConfig[notif.type] }}</span>

            <div class="notif-content">
              <p>{{ notif.message }}</p>
              <small>{{ new Date(notif.createdAt).toLocaleDateString('fr-FR') }}</small>
            </div>

            <button
              v-if="!notif.lu"
              @click="marquerLue(notif.id)"
              class="btn-lue"
            >
              ✓
            </button>
          </div>
        </div>
      </div>
    <div class="dash-footer" v-if="activeTab === 'annonces'">
      <RouterLink to="/annonce/nouvelle" class="btn-nouvelle">
        <Plus :size="18" />
        Nouvelle annonce
      </RouterLink>
    </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  min-height: 100vh;
  padding: 7rem 2rem 4rem;
}

.container {
  max-width: 900px;
  margin: 0 auto;
}

/* HEADER */
.dash-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.dash-greeting {
  color: var(--text-muted);
  font-size: 0.9rem;
  margin-bottom: 0.2rem;
}

.dash-title {
  font-size: 2rem;
  font-weight: 800;
  letter-spacing: -1px;
}

.wave {
  font-style: normal;
}

.btn-nouvelle {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: var(--text);
  color: var(--bg);
  border: none;
  padding: 0.7rem 1.2rem;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: opacity 0.2s;
  font-family: inherit;
}

.btn-nouvelle:hover {
  opacity: 0.85;
}

/* STATS */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 1.2rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  transition: border-color 0.2s;
}

.stat-card:hover {
  border-color: var(--border-hover);
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text);
}

.stat-label {
  font-size: 0.78rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* TABS */
.tabs {
  display: flex;
  gap: 0.5rem;
  border-bottom: 1px solid var(--border);
  margin-bottom: 1.5rem;
}

.tab {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: transparent;
  border: none;
  color: var(--text-muted);
  padding: 0.8rem 1rem;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  font-family: inherit;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  transition: all 0.2s;
}

.tab:hover {
  color: var(--text);
}

.tab.active {
  color: var(--text);
  border-bottom-color: var(--text);
}

.badge {
  background: var(--red);
  color: white;
  font-size: 0.7rem;
  padding: 0.1rem 0.4rem;
  border-radius: 10px;
  font-weight: 700;
}

/* TAB CONTENT */
.tab-content {
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ANNONCES LIST */
.annonces-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.annonce-row {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 1rem;
  transition: border-color 0.2s;
}

.annonce-row:hover {
  border-color: var(--border-hover);
}

.annonce-image img {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
}

.annonce-info {
  flex: 1;
}

.annonce-info h4 {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 0.2rem;
}

.annonce-cat {
  font-size: 0.78rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.annonce-prix {
  font-size: 1rem;
  font-weight: 700;
  color: var(--gold);
}

.annonce-statut {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.85rem;
  font-weight: 500;
  min-width: 100px;
  justify-content: flex-end;
}

/* EMPTY STATE */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;
}

.empty-icon {
  color: var(--text-muted);
  margin-bottom: 0.5rem;
}

.empty-state h3 {
  font-size: 1.1rem;
  color: var(--text);
}

.empty-state p {
  color: var(--text-muted);
  font-size: 0.9rem;
}

/* NOTIFICATIONS */
.notifs-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.notif-row {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 1rem;
  transition: all 0.2s;
}

.notif-row.non-lue {
  border-color: rgba(201, 168, 76, 0.3);
  background: rgba(201, 168, 76, 0.03);
}

.notif-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.notif-content {
  flex: 1;
}

.notif-content p {
  font-size: 0.9rem;
  color: var(--text);
  margin-bottom: 0.2rem;
}

.notif-content small {
  color: var(--text-muted);
  font-size: 0.78rem;
}

.btn-lue {
  background: rgba(16,185,129,0.1);
  color: var(--green);
  border: 1px solid rgba(16,185,129,0.2);
  border-radius: 6px;
  width: 28px;
  height: 28px;
  cursor: pointer;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.btn-lue:hover {
  background: rgba(16,185,129,0.2);
}

.loading {
  text-align: center;
  padding: 3rem;
  color: var(--text-muted);
}
.dash-footer {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
}
</style>