<template>
    <div class="main-page" ref="mainPage">
      <!-- 导航栏 -->
      <Navbar />
  
      <!-- 主要内容区域 -->
      <div class="home-container" ref="homeContainer">
        <div class="title-container">
          <h1 class="welcome-title">Welcome</h1>
          <h1 class="main-title">{{ configMap.server_name }}</h1>
          <p style="color: var(--ink-900); font-weight: 700; font-size: 1rem;">{{ configMap.main_title }}</p>
          <p style="color: var(--ink-900); font-weight: 700; font-size: 1rem;">{{ configMap.main_description }}</p>
        </div>
        <div class="button-container">
          <p class="instruction-text">请选择以下任意一项进行审核</p>
          <el-button v-if="configMap.redstone_msq === '1'" class="questionnaire-entry" size="large" @click="goToMsqView('redstone')">红石问卷</el-button>
          <el-button v-if="configMap.architectural_msq === '1'" class="questionnaire-entry" size="large" @click="goToMsqView('architectural')">建筑问卷</el-button>
          <el-button v-if="configMap.logistics_msq === '1'" class="questionnaire-entry" size="large" @click="goToMsqView('logistics')">后勤问卷</el-button>
          <el-button v-if="configMap.other_msq === '1'" class="questionnaire-entry" size="large" @click="goToMsqView('other')">其他问卷</el-button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import Navbar from '@/components/Navbar.vue'
  import { getConfig } from '@/api/System'
  
  const configMap = ref({})
  const mainPage = ref(null)
  const homeContainer = ref(null)
  
  function goToMsqView(type) {
    window.$router ? window.$router.push(`/msq?type=${type}`) : (location.href = `/msq?type=${type}`)
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
  
  onMounted(async () => {
    await fetchConfig()
    document.title = (configMap.value.server_short_name || '') + '官网'
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
    background: transparent;
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
    color: var(--ink-700);
    margin: 0 0 8px 0;
    font-weight: 700;
    letter-spacing: 0;
    text-shadow: none;
  }
  
  .main-title {
    font-size: 3.2rem;
    margin: 0 0 8px 0;
    font-weight: bold;
    color: var(--ink-900);
    text-shadow: none;
  }
  
  .title-container p {
    color: var(--ink-900);
    font-weight: 600;
    font-size: 1.25rem;
    margin: 6px 0;
    letter-spacing: 0;
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
    color: var(--ink-700);
    margin: 0 0 12px 0;
    font-weight: 500;
    letter-spacing: 0;
    text-shadow: none;
  }
  
  .button-container .el-button.questionnaire-entry {
    font-size: 1.15rem;
    padding: 0 30px;
    min-width: 220px;
    border-radius: var(--radius-md);
    background: color-mix(in srgb, var(--shell-strong) 50%, var(--count-bg));
    color: var(--ink-900);
    box-shadow: 0 4px 12px var(--accent-glow), var(--glass-spec);
    border: 1px solid var(--shell-border);
    backdrop-filter: blur(2px) saturate(1.8);
    -webkit-backdrop-filter: blur(2px) saturate(1.8);
    transition: transform 0.25s var(--ease-spring), background-color 0.2s, box-shadow 0.2s;
    font-weight: 500;
    letter-spacing: 0;
    margin-left: 0 !important;
    margin-right: 0 !important;
    display: flex;
    align-items: center;
    justify-content: center;
    text-shadow: none;
    line-height: 1.2;
    height: 56px;
    box-sizing: border-box;
  }
  .button-container .el-button.questionnaire-entry:not(.is-disabled):hover {
    background: var(--sct-button-hover-bg);
    color: var(--ink-900);
    border-color: var(--shell-border);
    transform: none;
    box-shadow: var(--shadow-soft), var(--glass-spec);
  }
  .button-container .el-button.questionnaire-entry:not(.is-disabled):active {
    transform: scale(0.98);
  }
  .button-container .el-button.questionnaire-entry:focus-visible {
    outline: 3px solid var(--focus-ring);
    outline-offset: 3px;
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
    .button-container .el-button.questionnaire-entry {
      font-size: 1rem;
      padding: 10px 8px;
      min-width: 120px;
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
    .button-container .el-button.questionnaire-entry {
      font-size: 0.9rem;
      padding: 8px 4px;
      min-width: 90px;
      height: 34px;
    }
  }
  
  @media screen and (max-width: 360px) {
    .welcome-title { font-size: 1.5rem; }
    .main-title { font-size: 2rem; }
    .instruction-text {
      font-size: 0.8rem;
    }
    .button-container .el-button.questionnaire-entry {
      font-size: 0.8rem;
      padding: 6px 2px;
      min-width: 70px;
      height: 28px;
    }
  }
  </style>