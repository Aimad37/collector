<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useAuthStore } from './stores/auth'
import { RouterView, RouterLink } from 'vue-router'

const authStore = useAuthStore()
const scrolled = ref(false)
const menuOpen = ref(false)

function handleScroll() {
  scrolled.value = window.scrollY > 20
}

onMounted(async () => {
  await authStore.initKeycloak()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<template>
  <div class="app">
    <nav :class="['navbar', { scrolled }]">
    <div class="nav-content">
      <RouterLink to="/" class="logo">
        <span class="logo-icon">◈</span>
        <span class="logo-text">Collector</span>
      </RouterLink>

      <div class="nav-center">
        <RouterLink to="/" class="nav-link">Catalogue</RouterLink>
        <RouterLink to="/dashboard" class="nav-link" v-if="authStore.isAuthenticated">
          Mon espace
        </RouterLink>
      </div>

      <div class="nav-right">
        <template v-if="authStore.isAuthenticated">
          <RouterLink to="/annonce/nouvelle" class="nav-btn-sell">
            <span>+</span> Vendre
          </RouterLink>
          <div class="user-pill">
            <div class="user-dot"></div>
            <span>{{ authStore.username }}</span>
            <button @click="authStore.logout" class="logout-icon" title="Déconnexion">
              ⏻
            </button>
          </div>
        </template>
        <template v-else>
          <button @click="authStore.login" class="nav-btn-primary">
            Connexion
          </button>
        </template>
      </div>
    </div>
  </nav>

    <main>
      <RouterView v-slot="{ Component }">
        <Transition name="fade" mode="out-in">
          <component :is="Component" />
        </Transition>
      </RouterView>
    </main>
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap');

*, *::before, *::after {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --bg: #000000;
  --surface: #0a0a0a;
  --card: #111111;
  --card-hover: #161616;
  --border: rgba(255,255,255,0.06);
  --border-hover: rgba(255,255,255,0.15);
  --text: #ffffff;
  --text-secondary: #888888;
  --text-muted: #444444;
  --accent: #ffffff;
  --gold: #c9a84c;
  --gold-light: #f0c060;
  --purple: #7c3aed;
  --blue: #2563eb;
  --green: #10b981;
  --red: #ef4444;
  --gradient: linear-gradient(135deg, #7c3aed, #2563eb);
  --gradient-gold: linear-gradient(135deg, #c9a84c, #f0c060);
}

html {
  scroll-behavior: smooth;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  background: var(--bg);
  color: var(--text);
  min-height: 100vh;
  -webkit-font-smoothing: antialiased;
}

.app {
  min-height: 100vh;
}

/* NAVBAR */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 1rem 0;
  transition: all 0.3s ease;
  border-bottom: 1px solid transparent;
}

.navbar.scrolled {
  background: rgba(0, 0, 0, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom-color: var(--border);
}

.nav-content {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 2rem;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  text-decoration: none;
  flex-shrink: 0;
}

.logo-icon {
  font-size: 1.5rem;
  background: var(--gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.logo-text {
  font-size: 1.2rem;
  font-weight: 700;
  color: var(--text);
  letter-spacing: -0.5px;
}

.nav-center {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(255,255,255,0.04);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 0.4rem;
}

.nav-link {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.88rem;
  font-weight: 500;
  padding: 0.4rem 0.9rem;
  border-radius: 8px;
  transition: all 0.2s;
}

.nav-link:hover {
  color: var(--text);
  background: rgba(255,255,255,0.05);
}

.nav-link.router-link-active {
  color: var(--text);
  background: rgba(255,255,255,0.08);
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  flex-shrink: 0;
}

.nav-btn-sell {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  background: var(--gold);
  color: #000;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 0.88rem;
  font-weight: 700;
  text-decoration: none;
  transition: opacity 0.2s;
}

.nav-btn-sell:hover {
  opacity: 0.85;
}

.user-pill {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  background: rgba(255,255,255,0.04);
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 0.4rem 0.8rem;
}

.user-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--green);
  flex-shrink: 0;
}

.user-pill span {
  color: var(--text);
  font-size: 0.88rem;
  font-weight: 500;
}

.logout-icon {
  background: transparent;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 1rem;
  padding: 0;
  transition: color 0.2s;
  line-height: 1;
}

.logout-icon:hover {
  color: var(--red);
}

.nav-btn-primary {
  background: var(--text);
  color: var(--bg);
  border: none;
  padding: 0.5rem 1.2rem;
  border-radius: 8px;
  font-size: 0.88rem;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
  font-family: inherit;
}

.nav-btn-primary:hover {
  opacity: 0.85;
}

/* TRANSITIONS */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* SCROLLBAR */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: var(--bg);
}

::-webkit-scrollbar-thumb {
  background: #333;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}
</style>