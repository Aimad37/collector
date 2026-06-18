<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAnnoncesStore } from '../stores/annonces'
import api from '../services/api'
import {
  Upload, Tag, DollarSign, ChevronRight,
  ChevronLeft, Check, Package, Music,
  Gamepad2, BookOpen, Trophy, Shirt
} from 'lucide-vue-next'

const router = useRouter()
const annoncesStore = useAnnoncesStore()

const step = ref(1)
const totalSteps = 3

const form = ref({
  titre: '',
  description: '',
  prix: '',
  fraisPort: 0,
  categorie: '',
  imageUrl: ''
})

const imagePreview = ref(null)
const uploadLoading = ref(false)
const erreurs = ref({})
const succes = ref(false)
const dragOver = ref(false)

const categories = [
  { key: 'SNEAKERS',   label: 'Sneakers',   icon: Shirt },
  { key: 'FIGURINES',  label: 'Figurines',  icon: Trophy },
  { key: 'VINYLES',    label: 'Vinyles',    icon: Music },
  { key: 'JEUX_VIDEO', label: 'Jeux vidéo', icon: Gamepad2 },
  { key: 'BD_MANGAS',  label: 'BD & Mangas',icon: BookOpen },
  { key: 'AUTRE',      label: 'Autre',      icon: Package },
]

const steps = [
  { number: 1, label: 'Photo' },
  { number: 2, label: 'Détails' },
  { number: 3, label: 'Prix' },
]

async function handleImage(file) {
  if (!file || !file.type.startsWith('image/')) return
  imagePreview.value = URL.createObjectURL(file)
  uploadLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const response = await api.post('/images/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    form.value.imageUrl = response.data.url
  } catch (err) {
    erreurs.value.image = 'Erreur upload image'
  } finally {
    uploadLoading.value = false
  }
}

function onFileChange(e) { handleImage(e.target.files[0]) }
function onDrop(e) {
  dragOver.value = false
  handleImage(e.dataTransfer.files[0])
}

function validerStep() {
  erreurs.value = {}
  if (step.value === 1 && !form.value.imageUrl) {
    erreurs.value.image = 'Ajoutez une photo'
    return false
  }
  if (step.value === 2) {
    if (!form.value.titre || form.value.titre.length < 5)
      erreurs.value.titre = 'Minimum 5 caractères'
    if (!form.value.description)
      erreurs.value.description = 'Description obligatoire'
    if (!form.value.categorie)
      erreurs.value.categorie = 'Choisissez une catégorie'
  }
  if (step.value === 3) {
    if (!form.value.prix || form.value.prix <= 0)
      erreurs.value.prix = 'Prix invalide'
    if (form.value.prix > 50000)
      erreurs.value.prix = 'Maximum 50 000 €'
  }
  return Object.keys(erreurs.value).length === 0
}

function nextStep() {
  if (validerStep()) step.value++
}

function prevStep() {
  step.value--
  erreurs.value = {}
}

async function soumettre() {
  if (!validerStep()) return
  try {
    await annoncesStore.creerAnnonce(form.value)
    succes.value = true
    setTimeout(() => router.push('/dashboard'), 2500)
  } catch (err) {
    erreurs.value.global = 'Erreur lors de la création'
  }
}
</script>

<template>
  <div class="form-page">
    <div class="form-container">

      <!-- Header -->
      <div class="form-header">
        <h1>Publier une <span class="gradient-text">annonce</span></h1>
        <p>Vendez votre pièce rare en quelques étapes</p>
      </div>

      <!-- Steps indicator -->
      <div class="steps-indicator">
        <div
          v-for="s in steps"
          :key="s.number"
          :class="['step-item', {
            active: step === s.number,
            done: step > s.number
          }]"
        >
          <div class="step-circle">
            <Check v-if="step > s.number" :size="14" />
            <span v-else>{{ s.number }}</span>
          </div>
          <span class="step-label">{{ s.label }}</span>
        </div>

        <div class="steps-line">
          <div
            class="steps-progress"
            :style="{ width: `${((step - 1) / (totalSteps - 1)) * 100}%` }"
          ></div>
        </div>
      </div>

      <!-- Success -->
      <div v-if="succes" class="succes-card">
        <div class="succes-icon">
          <Check :size="32" />
        </div>
        <h3>Annonce publiée !</h3>
        <p>Validation automatique en cours...</p>
      </div>

      <div v-else class="form-card">

        <!-- STEP 1 — Photo -->
        <Transition name="slide" mode="out-in">
          <div v-if="step === 1" key="step1" class="step-content">
            <div class="step-title">
              <Upload :size="20" />
              <span>Photo de l'article</span>
            </div>

            <div
              :class="['upload-zone', { 'drag-over': dragOver, 'has-image': imagePreview }]"
              @dragover.prevent="dragOver = true"
              @dragleave="dragOver = false"
              @drop.prevent="onDrop"
              @click="$refs.fileInput.click()"
            >
              <input
                ref="fileInput"
                type="file"
                accept="image/*"
                @change="onFileChange"
                hidden
              />

              <div v-if="uploadLoading" class="upload-loading">
                <div class="spinner"></div>
                <span>Upload en cours...</span>
              </div>

              <img
                v-else-if="imagePreview"
                :src="imagePreview"
                class="preview-image"
              />

              <div v-else class="upload-placeholder">
                <Upload :size="32" class="upload-icon" />
                <p>Glissez une photo ou cliquez</p>
                <span>PNG, JPG jusqu'à 10MB</span>
              </div>
            </div>

            <span v-if="erreurs.image" class="erreur">{{ erreurs.image }}</span>
          </div>
        </Transition>

        <!-- STEP 2 — Détails -->
        <Transition name="slide" mode="out-in">
          <div v-if="step === 2" key="step2" class="step-content">
            <div class="step-title">
              <Tag :size="20" />
              <span>Détails de l'article</span>
            </div>

            <div class="form-group">
              <label>Titre *</label>
              <input
                v-model="form.titre"
                type="text"
                placeholder="Ex: Nike Air Jordan 1 Chicago 1985"
                :class="{ error: erreurs.titre }"
                autofocus
              />
              <span v-if="erreurs.titre" class="erreur">{{ erreurs.titre }}</span>
            </div>

            <div class="form-group">
              <label>Description *</label>
              <textarea
                v-model="form.description"
                rows="3"
                placeholder="État, origine, accessoires inclus..."
                :class="{ error: erreurs.description }"
              />
              <span v-if="erreurs.description" class="erreur">{{ erreurs.description }}</span>
            </div>

            <div class="form-group">
              <label>Catégorie *</label>
              <div class="categories-grid">
                <button
                  v-for="cat in categories"
                  :key="cat.key"
                  type="button"
                  :class="['cat-btn', { active: form.categorie === cat.key }]"
                  @click="form.categorie = cat.key"
                >
                  <component :is="cat.icon" :size="18" />
                  <span>{{ cat.label }}</span>
                </button>
              </div>
              <span v-if="erreurs.categorie" class="erreur">{{ erreurs.categorie }}</span>
            </div>
          </div>
        </Transition>

        <!-- STEP 3 — Prix -->
        <Transition name="slide" mode="out-in">
          <div v-if="step === 3" key="step3" class="step-content">
            <div class="step-title">
              <DollarSign :size="20" />
              <span>Prix et livraison</span>
            </div>

            <div class="form-group">
              <label>Prix de vente *</label>
              <div class="input-prefix">
                <span>€</span>
                <input
                  v-model="form.prix"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0.00"
                  :class="{ error: erreurs.prix }"
                  autofocus
                />
              </div>
              <span v-if="erreurs.prix" class="erreur">{{ erreurs.prix }}</span>
            </div>

            <div class="form-group">
              <label>Frais de port (0 = gratuit)</label>
              <div class="input-prefix">
                <span>€</span>
                <input
                  v-model="form.fraisPort"
                  type="number"
                  min="0"
                  step="0.01"
                  placeholder="0.00"
                />
              </div>
            </div>

            <!-- Récap -->
            <div class="recap">
              <h4>Récapitulatif</h4>
              <div class="recap-image" v-if="imagePreview">
                <img :src="imagePreview" alt="preview" />
              </div>
              <div class="recap-info">
                <span class="recap-titre">{{ form.titre }}</span>
                <span class="recap-prix">{{ form.prix }} € + {{ form.fraisPort }} € port</span>
                <span class="recap-cat">{{ form.categorie }}</span>
              </div>
            </div>

            <div v-if="erreurs.global" class="erreur-global">
              {{ erreurs.global }}
            </div>
          </div>
        </Transition>

        <!-- Navigation -->
        <div class="form-nav">
          <button
            v-if="step > 1"
            type="button"
            class="btn-prev"
            @click="prevStep"
          >
            <ChevronLeft :size="18" />
            Retour
          </button>

          <div v-else></div>

          <button
            v-if="step < totalSteps"
            type="button"
            class="btn-next"
            @click="nextStep"
            :disabled="uploadLoading"
          >
            Suivant
            <ChevronRight :size="18" />
          </button>

          <button
            v-else
            type="button"
            class="btn-submit"
            @click="soumettre"
            :disabled="annoncesStore.loading"
          >
            <span v-if="annoncesStore.loading">Publication...</span>
            <span v-else>Publier →</span>
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
.form-page {
  min-height: 100vh;
  padding: 7rem 2rem 4rem;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.form-container {
  width: 100%;
  max-width: 520px;
}

.form-header {
  text-align: center;
  margin-bottom: 2rem;
}

.form-header h1 {
  font-size: 2rem;
  font-weight: 800;
  letter-spacing: -1px;
  margin-bottom: 0.4rem;
}

.gradient-text {
  background: linear-gradient(135deg, #c9a84c, #f0c060);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.form-header p {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

/* STEPS INDICATOR */
.steps-indicator {
  position: relative;
  display: flex;
  justify-content: space-between;
  margin-bottom: 2rem;
  padding: 0 1rem;
}

.steps-line {
  position: absolute;
  top: 16px;
  left: 2rem;
  right: 2rem;
  height: 1px;
  background: var(--border);
  z-index: 0;
}

.steps-progress {
  height: 100%;
  background: var(--text);
  transition: width 0.4s ease;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.4rem;
  position: relative;
  z-index: 1;
}

.step-circle {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--surface);
  border: 1px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--text-muted);
  transition: all 0.3s;
}

.step-item.active .step-circle {
  background: var(--text);
  color: var(--bg);
  border-color: var(--text);
}

.step-item.done .step-circle {
  background: var(--green);
  color: white;
  border-color: var(--green);
}

.step-label {
  font-size: 0.75rem;
  color: var(--text-muted);
  font-weight: 500;
}

.step-item.active .step-label {
  color: var(--text);
}

/* SUCCESS */
.succes-card {
  background: rgba(16,185,129,0.08);
  border: 1px solid rgba(16,185,129,0.2);
  border-radius: 16px;
  padding: 3rem;
  text-align: center;
}

.succes-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: var(--green);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
  color: white;
}

.succes-card h3 {
  font-size: 1.3rem;
  margin-bottom: 0.4rem;
}

.succes-card p {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

/* FORM CARD */
.form-card {
  background: var(--card);
  border: 1px solid var(--border);
  border-radius: 16px;
  padding: 1.5rem;
}

.step-content {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
  min-height: 300px;
}

.step-title {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  color: var(--text-secondary);
  font-size: 0.85rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 0.3rem;
}

/* UPLOAD */
.upload-zone {
  border: 1px dashed var(--border);
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  min-height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-zone:hover,
.upload-zone.drag-over {
  border-color: rgba(255,255,255,0.2);
  background: rgba(255,255,255,0.02);
}

.upload-zone.has-image {
  padding: 0;
  border-style: solid;
  overflow: hidden;
}

.preview-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.upload-icon {
  color: var(--text-muted);
  margin-bottom: 0.3rem;
}

.upload-placeholder p {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.upload-placeholder span {
  color: var(--text-muted);
  font-size: 0.8rem;
}

.upload-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.spinner {
  width: 28px;
  height: 28px;
  border: 2px solid var(--border);
  border-top-color: var(--text);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* FORM GROUPS */
.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.form-group label {
  font-size: 0.8rem;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

input, textarea {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 0.7rem 0.9rem;
  color: var(--text);
  font-size: 0.9rem;
  font-family: inherit;
  transition: all 0.2s;
  width: 100%;
}

input:focus, textarea:focus {
  outline: none;
  border-color: rgba(255,255,255,0.25);
}

input.error, textarea.error {
  border-color: var(--red);
}

.input-prefix {
  display: flex;
  align-items: center;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
  transition: border-color 0.2s;
}

.input-prefix:focus-within {
  border-color: rgba(255,255,255,0.25);
}

.input-prefix span {
  padding: 0 0.8rem;
  color: var(--text-muted);
  font-size: 0.85rem;
  border-right: 1px solid var(--border);
}

.input-prefix input {
  border: none;
  border-radius: 0;
  background: transparent;
}

/* CATEGORIES */
.categories-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 0.5rem;
}

.cat-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.3rem;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 0.7rem 0.5rem;
  color: var(--text-secondary);
  font-size: 0.78rem;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s;
}

.cat-btn:hover {
  border-color: var(--border-hover);
  color: var(--text);
}

.cat-btn.active {
  background: rgba(255,255,255,0.05);
  border-color: var(--text);
  color: var(--text);
}

/* RECAP */
.recap {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 1rem;
  display: flex;
  gap: 1rem;
  align-items: center;
}

.recap h4 {
  font-size: 0.75rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 0.5rem;
  width: 100%;
}

.recap-image img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
}

.recap-info {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  flex: 1;
}

.recap-titre {
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--text);
}

.recap-prix {
  font-size: 0.85rem;
  color: var(--gold);
  font-weight: 600;
}

.recap-cat {
  font-size: 0.75rem;
  color: var(--text-muted);
}

/* ERRORS */
.erreur {
  color: var(--red);
  font-size: 0.78rem;
}

.erreur-global {
  background: rgba(239,68,68,0.08);
  border: 1px solid rgba(239,68,68,0.2);
  border-radius: 8px;
  padding: 0.7rem 0.9rem;
  color: var(--red);
  font-size: 0.85rem;
}

/* NAVIGATION */
.form-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  padding-top: 1.2rem;
  border-top: 1px solid var(--border);
}

.btn-prev {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  background: transparent;
  border: 1px solid var(--border);
  color: var(--text-secondary);
  padding: 0.6rem 1rem;
  border-radius: 8px;
  font-size: 0.85rem;
  font-family: inherit;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-prev:hover {
  border-color: var(--border-hover);
  color: var(--text);
}

.btn-next {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  background: var(--text);
  color: var(--bg);
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 600;
  font-family: inherit;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-next:hover:not(:disabled) {
  opacity: 0.85;
}

.btn-next:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-submit {
  background: var(--gold);
  color: #000;
  border: none;
  padding: 0.6rem 1.5rem;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 700;
  font-family: inherit;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-submit:hover:not(:disabled) {
  opacity: 0.85;
}

.btn-submit:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* TRANSITIONS */
.slide-enter-active,
.slide-leave-active {
  transition: all 0.25s ease;
}

.slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>