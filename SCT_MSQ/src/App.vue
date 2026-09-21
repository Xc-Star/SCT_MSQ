<template>
  <router-view></router-view>
</template>

<script setup>
import { useSiteBackground } from '@/composables/useSiteBackground'

useSiteBackground()
</script>

<style>
body {
  margin: 0;
  padding: 0;
  min-height: 100vh;
  /* background-color: skyblue; */
}

#app {
  font-family: var(--sct-font);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--ink-700);
}
</style>
