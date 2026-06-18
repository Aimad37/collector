<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAnnoncesStore } from '../stores/annonces'
import AnnonceCard from '../components/AnnonceCard.vue'
import {
  LayoutGrid, Footprints, Trophy,
  Disc3, Gamepad2, BookOpen, Package
} from 'lucide-vue-next'
const annoncesStore = useAnnoncesStore()
const search = ref('')
const categorieActive = ref('TOUS')
const loaded = ref(false)

const categories = [
  { key: 'TOUS',      label: 'Tout voir',   icon: LayoutGrid },
  { key: 'SNEAKERS',  label: 'Sneakers',    icon: Footprints },
  { key: 'FIGURINES', label: 'Figurines',   icon: Trophy },
  { key: 'VINYLES',   label: 'Vinyles',     icon: Disc3 },
  { key: 'JEUX_VIDEO',label: 'Jeux vidéo',  icon: Gamepad2 },
  { key: 'BD_MANGAS', label: 'BD & Mangas', icon: BookOpen },
  { key: 'AUTRE',     label: 'Autre',       icon: Package },
]

const annoncesFiltered = computed(() => {
  return annoncesStore.annonces
    .filter(a => categorieActive.value === 'TOUS' || a.categorie === categorieActive.value)
    .filter(a =>
      a.titre.toLowerCase().includes(search.value.toLowerCase()) ||
      a.description.toLowerCase().includes(search.value.toLowerCase())
    )
})

onMounted(async () => {
  await annoncesStore.fetchAnnonces()
  setTimeout(() => loaded.value = true, 100)
})
</script>

<template>
  <div class="home">

    <!-- HERO -->
    <section class="hero">
      <div class="hero-bg">
        <div class="orb orb-1"></div>
        <div class="orb orb-2"></div>
        <div class="orb orb-3"></div>
      </div>

      <div class="hero-content">
        <div class="hero-badge">✦ Marketplace de collection</div>
        <h1 class="hero-title">
          Trouvez la pièce
          <span class="gradient-text">rare</span>
          qui vous manque
        </h1>
        <p class="hero-sub">
          Des milliers d'objets vintage et de collection entre passionnés
        </p>

        <div class="search-bar">
          <span class="search-icon">⌕</span>
          <input
            v-model="search"
            type="text"
            placeholder="Rechercher une pièce rare..."
            class="search-input"
          />
          <button class="search-btn">Rechercher</button>
        </div>

        <div class="hero-stats">
          <div class="stat">
            <span class="stat-number">{{ annoncesStore.annonces.length }}</span>
            <span class="stat-label">Annonces</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat">
            <span class="stat-number">6</span>
            <span class="stat-label">Catégories</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat">
            <span class="stat-number">100%</span>
            <span class="stat-label">Sécurisé</span>
          </div>
        </div>
      </div>
    </section>

    <!-- CATALOGUE -->
    <section class="catalogue">
      <div class="container">

        <!-- Filtres -->
      <div class="filtres-wrapper">
        <div class="filtres">
          <button
            v-for="cat in categories"
            :key="cat.key"
            :class="['filtre-btn', { active: categorieActive === cat.key }]"
            @click="categorieActive = cat.key"
          >
            <component :is="cat.icon" :size="15" />
            <span>{{ cat.label }}</span>
          </button>
        </div>
      </div>

        <!-- Résultats -->
        <div class="results-header">
          <span class="results-count">
            {{ annoncesFiltered.length }} résultat{{ annoncesFiltered.length > 1 ? 's' : '' }}
          </span>
        </div>

        <!-- Loading -->
        <div v-if="annoncesStore.loading" class="loading-grid">
          <div v-for="n in 6" :key="n" class="skeleton-card">
            <div class="skeleton-image"></div>
            <div class="skeleton-body">
              <div class="skeleton-line"></div>
              <div class="skeleton-line short"></div>
            </div>
          </div>
        </div>

        <!-- Grid annonces -->
        <div v-else-if="annoncesFiltered.length > 0" class="annonces-grid">
          <AnnonceCard
            v-for="(annonce, index) in annoncesFiltered"
            :key="annonce.id"
            :annonce="annonce"
            :style="{ animationDelay: `${index * 0.05}s` }"
            class="card-animate"
          />
        </div>

        <!-- Empty state -->
        <div v-else class="empty-state">
          <div class="empty-icon">◈</div>
          <h3>Aucun résultat</h3>
          <p>Essayez une autre recherche ou catégorie</p>
        </div>

      </div>
    </section>

  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
}

/* HERO */
.hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  padding: 8rem 2rem 4rem;
}

.hero-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.15;
  animation: float 8s ease-in-out infinite;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: #7c3aed;
  top: -200px;
  left: -200px;
  animation-delay: 0s;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: #2563eb;
  bottom: -150px;
  right: -150px;
  animation-delay: 2s;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: #c9a84c;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.05); }
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 800px;
}

.hero-badge {
  display: inline-block;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.1);
  color: var(--gold);
  padding: 0.4rem 1rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
  margin-bottom: 1.5rem;
  backdrop-filter: blur(10px);
}

.hero-title {
  font-size: clamp(2.5rem, 6vw, 5rem);
  font-weight: 800;
  line-height: 1.1;
  letter-spacing: -2px;
  margin-bottom: 1rem;
  color: var(--text);
}

.gradient-text {
  background: linear-gradient(135deg, #c9a84c, #f0c060, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-sub {
  color: var(--text-secondary);
  font-size: 1.1rem;
  margin-bottom: 2.5rem;
  font-weight: 400;
}

/* SEARCH */
.search-bar {
  display: flex;
  align-items: center;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 16px;
  padding: 0.5rem 0.5rem 0.5rem 1.2rem;
  margin-bottom: 3rem;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
}

.search-bar:focus-within {
  border-color: rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.06);
  box-shadow: 0 0 0 4px rgba(124, 58, 237, 0.1);
}

.search-icon {
  color: var(--text-muted);
  font-size: 1.2rem;
  margin-right: 0.8rem;
}

.search-input {
  flex: 1;
  background: transparent;
  border: none;
  outline: none;
  color: var(--text);
  font-size: 1rem;
  font-family: inherit;
}

.search-input::placeholder {
  color: var(--text-muted);
}

.search-btn {
  background: var(--text);
  color: var(--bg);
  border: none;
  padding: 0.7rem 1.5rem;
  border-radius: 10px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
  font-family: inherit;
}

.search-btn:hover {
  opacity: 0.85;
}

/* STATS */
.hero-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2rem;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text);
}

.stat-label {
  font-size: 0.8rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stat-divider {
  width: 1px;
  height: 30px;
  background: var(--border);
}

/* CATALOGUE */
.catalogue {
  padding: 4rem 0 6rem;
}

.container {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* FILTRES */
.filtres-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

.filtres {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
  justify-content: center;
  background: rgba(255,255,255,0.03);
  border: 1px solid var(--border);
  border-radius: 14px;
  padding: 0.4rem;
}

.filtre-btn {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  background: transparent;
  border: none;
  color: var(--text-secondary);
  padding: 0.5rem 1rem;
  border-radius: 10px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}

.filtre-btn:hover {
  color: var(--text);
  background: rgba(255,255,255,0.05);
}

.filtre-btn.active {
  background: var(--text);
  color: var(--bg);
}

/* RESULTS */
.results-header {
  margin-bottom: 1.5rem;
}

.results-count {
  color: var(--text-muted);
  font-size: 0.9rem;
}

/* GRID */
.annonces-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

/* CARD ANIMATION */
.card-animate {
  animation: cardIn 0.4s ease both;
}

@keyframes cardIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* SKELETON */
.loading-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.skeleton-card {
  background: var(--card);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid var(--border);
}

.skeleton-image {
  height: 220px;
  background: linear-gradient(90deg, #111 25%, #1a1a1a 50%, #111 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-body {
  padding: 1.2rem;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.skeleton-line {
  height: 14px;
  background: linear-gradient(90deg, #111 25%, #1a1a1a 50%, #111 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-line.short {
  width: 60%;
}

@keyframes shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

/* EMPTY */
.empty-state {
  text-align: center;
  padding: 6rem 2rem;
}

.empty-icon {
  font-size: 3rem;
  background: var(--gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 1rem;
}

.empty-state h3 {
  color: var(--text);
  font-size: 1.2rem;
  margin-bottom: 0.5rem;
}

.empty-state p {
  color: var(--text-muted);
  font-size: 0.9rem;
}
</style>