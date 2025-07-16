<template>
    <div class="main-page" ref="mainPage">
      <!-- 导航栏 -->
      <Navbar />
  
      <!-- 主要内容区域 -->
      <div class="home-container" ref="homeContainer">
        <div class="title-container">
          <h1 class="welcome-title">Welcome</h1>
          <h1 class="main-title">{{ configMap.server_name }}</h1>
          <p style="color: #000; font-weight: 700; font-size: 1rem;">{{ configMap.main_title }}</p>
          <p style="color: #000; font-weight: 700; font-size: 1rem;">{{ configMap.main_description }}</p>
        </div>
        <div class="button-container">
          <p class="instruction-text">请选择以下任意一项进行审核</p>
          <el-button v-if="configMap.redstone_msq === '1'" type="primary" size="large" @click="goToMsqView('redstone')">红石问卷</el-button>
          <el-button v-if="configMap.architectural_msq === '1'" type="primary" size="large" @click="goToMsqView('architectural')">建筑问卷</el-button>
          <el-button v-if="configMap.logistics_msq === '1'" type="primary" size="large" @click="goToMsqView('logistics')">后勤问卷</el-button>
          <el-button v-if="configMap.other_msq === '1'" type="primary" size="large" @click="goToMsqView('other')">其他问卷</el-button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, onBeforeUnmount } from 'vue'
  import Navbar from '@/components/Navbar.vue'
  import { getConfig } from '@/api/System'
  
  const backgroundImageUrl = ref('')
  const isMobile = ref(false)
  const configMap = ref({})
  const mainPage = ref(null)
  const homeContainer = ref(null)
  
  function goToMsqView(type) {
    window.$router ? window.$router.push(`/msq?type=${type}`) : (location.href = `/msq?type=${type}`)
  }
  
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
  /* 移除卡片感相关样式，优化整体美观度 */
  .main-page {
    min-height: 100vh;
    position: relative;
    overflow: hidden;
  }
  .main-page::after {
    content: "";
    position: absolute;
    inset: 0;
    z-index: 0;
    background: rgba(255,255,255,0.38);
    backdrop-filter: blur(8px);
    pointer-events: none;
  }
  
  .home-container {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-width: 0;
    width: 100vw;
    max-width: 100vw;
    min-height: unset;
    text-align: center;
    z-index: 1;
    background: none;
    border-radius: 0;
    box-shadow: none;
    margin: 0;
    padding: 0px 16px 40px 16px;
    backdrop-filter: none;
  }
  
  .title-container {
    margin-bottom: 40px;
  }
  
  .welcome-title {
    font-size: 2.5rem;
    color: #409EFF;
    margin: 0 0 8px 0;
    font-weight: 700;
    letter-spacing: 2px;
    text-shadow: 2px 2px 8px rgba(64,158,255,0.13);
    background: linear-gradient(90deg, #409EFF 30%, #66b1ff 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
  
  .main-title {
    font-size: 3.2rem;
    margin: 0 0 8px 0;
    font-weight: bold;
    background: linear-gradient(90deg, #409EFF 40%, #a0cfff 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    text-shadow: 3px 3px 12px rgba(64,158,255,0.10);
  }
  
  .title-container p {
    color: #222;
    font-weight: 600;
    font-size: 1.25rem;
    margin: 6px 0;
    letter-spacing: 1px;
  }
  
  .button-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 22px;
    margin-top: 18px;
    width: 100%;
  }
  
  .instruction-text {
    font-size: 1.15rem;
    color: #409EFF;
    margin: 0 0 12px 0;
    font-weight: 500;
    letter-spacing: 1px;
    text-shadow: 1px 1px 4px rgba(0,0,0,0.10);
  }
  
  .button-container .el-button {
    font-size: 1.15rem;
    padding: 0 30px;
    min-width: 220px;
    border-radius: 5px;
    background: unset;
    color: #222;
    box-shadow: none;
    border: unset;
    transition: all 0.22s cubic-bezier(.4,0,.2,1);
    font-weight: 600;
    letter-spacing: 1px;
    margin-left: 0 !important;
    margin-right: 0 !important;
    display: flex;
    align-items: center;
    justify-content: center;
    text-shadow: 1px 1px 4px rgba(0,0,0,0.13);
    line-height: 1.2;
    height: 56px;
    box-sizing: border-box;
  }
  .button-container .el-button:hover {
    background: rgba(255,255,255,0.55);
    backdrop-filter: blur(6px);
    -webkit-backdrop-filter: blur(6px);
    /* transform: scale(1.1); */
    box-shadow: 0 6px 18px 0 rgba(64,158,255,0.13);
    text-shadow: 2px 2px 8px rgba(0,0,0,0.18);
  }
  
  /* 手机端适配 */
  @media screen and (max-width: 768px) {
    .home-container {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      padding: 18px 8px 24px 8px;
      padding-top: 80px;
      margin-top: 0;
      min-width: 0;
      width: 100vw;
      max-width: 98vw;
      min-height: unset;
      box-sizing: border-box;
      overflow-y: auto;
      border-radius: 0;
    }
    .title-container {
      margin-bottom: 18px;
    }
    .welcome-title { font-size: 2.5rem; }
    .main-title { font-size: 3rem; }
    .button-container {
      gap: 10px;
      margin-top: 8px;
      width: 100%;
    }
    .instruction-text {
      font-size: 1rem;
      margin-bottom: 6px;
    }
    .button-container .el-button {
      font-size: 1rem;
      padding: 10px 8px;
      min-width: 120px;
      border-radius: 5px;
      height: 40px;
    }
  }
  @media screen and (max-width: 480px) {
    .home-container {
      padding: 8px 2px 16px 2px;
      padding-top: 70px;
      margin-top: 0;
      max-width: 99vw;
      min-width: 0;
      width: 100vw;
      min-height: unset;
      border-radius: 0;
    }
    .welcome-title { font-size: 2rem; }
    .main-title { font-size: 2.5rem; }
    .instruction-text {
      font-size: 0.9rem;
    }
    .button-container .el-button {
      font-size: 0.9rem;
      padding: 8px 4px;
      min-width: 90px;
      border-radius: 3px;
      height: 34px;
    }
  }
  
  @media screen and (max-width: 360px) {
    .welcome-title { font-size: 1.5rem; }
    .main-title { font-size: 2rem; }
    .instruction-text {
      font-size: 0.8rem;
    }
    .button-container .el-button {
      font-size: 0.8rem;
      padding: 6px 2px;
      min-width: 70px;
      border-radius: 2px;
      height: 28px;
    }
  }
  </style>