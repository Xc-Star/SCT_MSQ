<template>
  <!-- 导航栏 -->
  <Navbar />
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import Navbar from '@/components/Navbar.vue'
import { getConfig } from '@/api/System'

const backgroundImageUrl = ref('')
const isMobile = ref(false)
const configMap = ref({})
const mainPage = ref(null)

function handleResize() {
  const wasMobile = isMobile.value
  checkDeviceType()
  if (wasMobile !== isMobile.value) {
    getBackgroundImage()
  }
}

function checkDeviceType() {
  isMobile.value = window.innerWidth <= 768 || /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

async function fetchConfig() {
  try {
    const res = await getConfig()
    if (res && res.data) {
      const map = {}
      res.data.forEach(item => {
        map[item.configKey] = item.configValue
      })
      configMap.value = map
    }
  } catch (e) {
    // 可选：错误处理
  }
}

async function getBackgroundImage() {
  if (isMobile.value) {
    backgroundImageUrl.value = configMap.value['phone_main_background'] || ''
  } else {
    backgroundImageUrl.value = configMap.value['main_background'] || ''
  }
  setBackgroundImage()
}

function setBackgroundImage() {
  if (backgroundImageUrl.value && mainPage.value) {
    mainPage.value.style.backgroundImage = `url('${backgroundImageUrl.value}')`
    mainPage.value.style.backgroundSize = 'cover'
    mainPage.value.style.backgroundPosition = 'center'
    mainPage.value.style.backgroundRepeat = 'no-repeat'
    mainPage.value.style.backgroundAttachment = 'fixed'
  }
}

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
  return ''
}

onMounted(async () => {
  // 强制跳转到指定URL
  // window.location.href = 'http://v4.sctserver.top:81/sctserver'
  window.location.href = 'http://localhost:5173/sctserver'
  
  // 以下代码将不会执行，因为页面已经跳转
  document.title = getServerShortName() + '官网'
  checkDeviceType()
  await fetchConfig()
  getBackgroundImage()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
</style>