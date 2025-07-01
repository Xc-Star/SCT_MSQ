<template>
  <div>
    <Navbar />
    <div class="developing-container">
      <div class="developing-text">功能开发中……</div>
    </div>
  </div>
</template>

<script>
import Navbar from '@/components/Navbar.vue'

export default {
  name: 'Overview',
  components: {
    Navbar
  }
}
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