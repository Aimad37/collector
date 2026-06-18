<script setup>
import { ref } from 'vue'

const props = defineProps({
  annonce: {
    type: Object,
    required: true
  }
})

const card = ref(null)
const rotateX = ref(0)
const rotateY = ref(0)
const glowX = ref(50)
const glowY = ref(50)

function handleMouseMove(e) {
  const rect = card.value.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  const centerX = rect.width / 2
  const centerY = rect.height / 2

  rotateX.value = ((y - centerY) / centerY) * -8
  rotateY.value = ((x - centerX) / centerX) * 8
  glowX.value = (x / rect.width) * 100
  glowY.value = (y / rect.height) * 100
}

function handleMouseLeave() {
  rotateX.value = 0
  rotateY.value = 0
  glowX.value = 50
  glowY.value = 50
}

const categorieConfig = {
  SNEAKERS:  { icon: '👟', color: '#f97316' },
  FIGURINES: { icon: '🏆', color: '#a855f7' },
  VINYLES:   { icon: '🎵', color: '#3b82f6' },
  JEUX_VIDEO:{ icon: '🎮', color: '#10b981' },
  BD_MANGAS: { icon: '📚', color: '#ef4444' },
  AUTRE:     { icon: '📦', color: '#6b7280' },
}

function formatPrix(prix) {
  return new Intl.NumberFormat('fr-FR').format(prix)
}
</script>

<template>
  <div
    ref="card"
    class="card"
    @mousemove="handleMouseMove"
    @mouseleave="handleMouseLeave"
    :style="{
      transform: `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`,
      '--glow-x': `${glowX}%`,
      '--glow-y': `${glowY}%`,
      '--cat-color': categorieConfig[annonce.categorie]?.color
    }"
  >
    <!-- Glow effect -->
    <div class="card-glow"></div>

    <!-- Image -->
    <div class="card-image">
      <img
        :src="annonce.imageUrl || 'https://via.placeholder.com/400x300/111/333?text=No+Image'"
        :alt="annonce.titre"
        loading="lazy"
      />
      <div class="image-overlay"></div>

      <!-- Badge catégorie -->
      <div class="badge-categorie">
        <span>{{ categorieConfig[annonce.categorie]?.icon }}</span>
        <span>{{ annonce.categorie.replace('_', ' ') }}</span>
      </div>

      <!-- Prix flottant -->
      <div class="prix-flottant">
        {{ formatPrix(annonce.prix) }} €
      </div>
    </div>

    <!-- Body -->
    <div class="card-body">
      <h3 class="card-titre">{{ annonce.titre }}</h3>
      <p class="card-description">{{ annonce.description }}</p>

      <div class="card-footer">
        <div class="vendeur-info">
          <div class="vendeur-avatar">
            {{ annonce.vendeurUsername.charAt(0).toUpperCase() }}
          </div>
          <span class="vendeur-name">@{{ annonce.vendeurUsername }}</span>
        </div>

        <div class="port-info" v-if="annonce.fraisPort > 0">
          <span class="port-label">Port</span>
          <span class="port-value">{{ annonce.fraisPort }} €</span>
        </div>
        <div class="port-info" v-else>
          <span class="port-gratuit">Port offert</span>
        </div>
      </div>
    </div>

    <!-- CTA -->
    <div class="card-cta">
      <button class="btn-voir">Voir l'annonce →</button>
    </div>
  </div>
</template>

<style scoped>
.card {
  position: relative;
  background: var(--card);
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid var(--border);
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  transform-style: preserve-3d;
  will-change: transform;
  cursor: pointer;
}

.card:hover {
  border-color: rgba(255,255,255,0.12);
  box-shadow:
    0 20px 60px rgba(0,0,0,0.5),
    0 0 0 1px rgba(255,255,255,0.05);
}

/* GLOW EFFECT */
.card-glow {
  position: absolute;
  inset: 0;
  z-index: 0;
  opacity: 0;
  transition: opacity 0.3s ease;
  background: radial-gradient(
    circle at var(--glow-x) var(--glow-y),
    rgba(var(--cat-color), 0.08) 0%,
    transparent 60%
  );
  pointer-events: none;
}

.card:hover .card-glow {
  opacity: 1;
}

/* IMAGE */
.card-image {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
  display: block;
}

.card:hover .card-image img {
  transform: scale(1.08);
}

.image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    transparent 40%,
    rgba(0,0,0,0.8) 100%
  );
}

/* BADGE */
.badge-categorie {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  align-items: center;
  gap: 0.3rem;
  background: rgba(0,0,0,0.7);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255,255,255,0.1);
  color: white;
  padding: 0.3rem 0.7rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* PRIX FLOTTANT */
.prix-flottant {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: var(--gradient-gold);
  color: #000;
  padding: 0.4rem 0.8rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 800;
  letter-spacing: -0.5px;
}

/* BODY */
.card-body {
  position: relative;
  z-index: 1;
  padding: 1.2rem;
}

.card-titre {
  color: var(--text);
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: -0.3px;
}

.card-description {
  color: var(--text-secondary);
  font-size: 0.82rem;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 1rem;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 0.8rem;
  border-top: 1px solid var(--border);
}

.vendeur-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.vendeur-avatar {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: var(--gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 700;
  color: white;
}

.vendeur-name {
  color: var(--text-muted);
  font-size: 0.8rem;
}

.port-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.port-label {
  color: var(--text-muted);
  font-size: 0.7rem;
  text-transform: uppercase;
}

.port-value {
  color: var(--text-secondary);
  font-size: 0.8rem;
  font-weight: 500;
}

.port-gratuit {
  color: var(--green);
  font-size: 0.78rem;
  font-weight: 600;
}

/* CTA */
.card-cta {
  padding: 0 1.2rem 1.2rem;
}

.btn-voir {
  width: 100%;
  background: transparent;
  border: 1px solid var(--border);
  color: var(--text-secondary);
  padding: 0.6rem;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
  opacity: 0;
  transform: translateY(4px);
  transition: all 0.2s ease;
}

.card:hover .btn-voir {
  opacity: 1;
  transform: translateY(0);
  border-color: var(--border-hover);
  color: var(--text);
}

.btn-voir:hover {
  background: var(--card-hover);
}
</style>