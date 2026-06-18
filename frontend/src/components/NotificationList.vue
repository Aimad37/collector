<script setup>
import { notificationService } from '../services/api'

const props = defineProps({
  notifications: {
    type: Array,
    required: true
  }
})

async function marquerLue(id) {
  await notificationService.marquerCommeLue(id)
  const notif = props.notifications.find(n => n.id === id)
  if (notif) notif.lu = true
}

function typeIcon(type) {
  const icons = {
    NOUVELLE_ANNONCE: '🆕',
    ANNONCE_VALIDEE: '✅',
    ANNONCE_REJETEE: '❌',
    CHANGEMENT_PRIX: '💰'
  }
  return icons[type] || '🔔'
}
</script>

<template>
  <div class="notifications">
    <div v-if="notifications.length === 0" class="empty">
      Aucune notification.
    </div>

    <div
      v-for="notif in notifications"
      :key="notif.id"
      :class="['notif', { 'non-lue': !notif.lu }]"
    >
      <span class="icon">{{ typeIcon(notif.type) }}</span>
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
</template>

<style scoped>
.notifications {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.notif {
  display: flex;
  align-items: flex-start;
  gap: 0.75rem;
  padding: 0.75rem;
  border-radius: 6px;
  background: #f5f5f5;
}

.non-lue {
  background: #fff3f5;
  border-left: 3px solid #e94560;
}

.icon {
  font-size: 1.2rem;
}

.notif-content {
  flex: 1;
}

.notif-content p {
  font-size: 0.9rem;
  color: #333;
  margin-bottom: 0.25rem;
}

.notif-content small {
  color: #999;
  font-size: 0.8rem;
}

.btn-lue {
  background: #27ae60;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  cursor: pointer;
  font-size: 0.8rem;
}

.empty {
  text-align: center;
  padding: 2rem;
  color: #666;
}
</style>