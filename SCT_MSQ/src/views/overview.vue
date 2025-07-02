<template>
  <div>
    <Navbar />
    <div class="developing-container">
      <div class="developing-text">功能开发中……</div>
    </div>
  </div>
</template>

<script setup>
import Navbar from '@/components/Navbar.vue'

function getServerShortName() {
  const CACHE_KEY = 'navbar_config_cache'
  const cacheStr = localStorage.getItem(CACHE_KEY)
  if (cacheStr) {
    try {
      const cache = JSON.parse(cacheStr)
      if (cache.data) {
        const item = cache.data.find(item => item.configKey === 'server_short_name')
        if (item && item.configValue) return item.configValue
      }
    } catch (e) {}
  }
  return 'SCT官网'
}

onMounted(async () => {
  document.title = getServerShortName() + '官网'
})
</script>

<style scoped>
.developing-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}
.developing-text {
  font-size: 2.2rem;
  color: #000;
  font-weight: bold;
  letter-spacing: 2px;
  text-shadow: 2px 2px 8px rgba(0,0,0,0.15);
}
</style> 