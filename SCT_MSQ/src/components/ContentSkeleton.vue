<template>
  <div class="content-skeleton" :class="`skeleton-${variant}`" role="status" aria-label="内容加载中" aria-busy="true">
    <div class="skeleton-shape" aria-hidden="true"></div>
    <div class="skeleton-lines" aria-hidden="true">
      <span class="skeleton-line"></span>
      <span v-if="variant !== 'card'" class="skeleton-line short"></span>
      <template v-if="variant === 'form' || variant === 'home'">
        <span class="skeleton-field"></span>
        <span class="skeleton-line"></span>
        <span class="skeleton-field"></span>
        <span class="skeleton-line short"></span>
      </template>
    </div>
  </div>
</template>

<script setup>
defineProps({ variant: { type: String, default: 'card' } })
</script>

<style scoped>
.content-skeleton {
  min-width: 0;
  overflow: hidden;
  animation: skeleton-breathe 1.6s ease-in-out infinite;
}
.skeleton-shape, .skeleton-line, .skeleton-field {
  display: block;
  background: var(--count-bg);
  border: 1px solid var(--shell-border);
  border-radius: 6px;
}
.skeleton-line { width: 80%; height: 14px; }
.skeleton-line.short { width: 55%; }
.skeleton-lines { display: grid; gap: 14px; min-width: 0; }
.skeleton-card { border-radius: var(--radius-lg); }
.skeleton-card .skeleton-shape { aspect-ratio: 4 / 3; border-radius: 0; }
.skeleton-card .skeleton-lines { padding: 13px 12px; }
.skeleton-message { display: flex; align-items: center; gap: 12px; padding: 12px 0; }
.skeleton-message .skeleton-shape { width: 40px; height: 40px; flex-shrink: 0; border-radius: 50%; }
.skeleton-message .skeleton-lines { flex: 1; }
.skeleton-form .skeleton-shape, .skeleton-home .skeleton-shape { width: 55%; height: 32px; margin-bottom: 24px; }
.skeleton-form .skeleton-lines, .skeleton-home .skeleton-lines { gap: 24px; }
.skeleton-field { width: 100%; height: 44px; }
.skeleton-home { width: min(680px, 100%); margin: auto; }
.skeleton-home .skeleton-shape { width: 75%; height: 64px; margin: 0 auto 32px; }
.skeleton-home .skeleton-line { margin: auto; }
@keyframes skeleton-breathe { 50% { opacity: 0.45; } }
@media (prefers-reduced-motion: reduce) {
  .content-skeleton { animation: none; }
}
</style>