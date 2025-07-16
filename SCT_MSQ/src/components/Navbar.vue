<template>
  <nav class="navbar">
    <div class="nav-left">
      <img 
        :src="logoUrl" 
        alt="Logo" 
        class="logo-image"
        @click="goToHome"
        @error="handleImageError"
      />
      <div class="logo" @click="goToHome">{{ serverShortName }}</div>
    </div>
    <!-- PC端按钮 -->
    <div class="nav-right nav-pc">
      <el-button type="text" class="nav-btn" @click="goToResult">查询问卷结果</el-button>
      <el-button type="text" class="nav-btn" @click="goToOverview">浏览{{ serverShortName }}</el-button>
      <el-button v-if="showStockListTool" type="text" class="nav-btn" @click="goBuildTool">备货列表生成工具</el-button>
    </div>
    <!-- 手机端汉堡菜单 -->
    <div class="nav-mobile" ref="menuRef">
      <button class="hamburger-btn" :class="{ open: menuOpen }" @click="toggleMenu" aria-label="菜单">
        <span></span>
        <span></span>
        <span></span>
      </button>
      <div v-show="menuOpen" class="mobile-menu-mask">
        <div class="mobile-full-menu auto-height" :class="{ show: menuOpen }">
          <div class="mobile-full-menu-content">
            <ul class="mobile-menu">
              <li @click="handleMenuClick(goToResult)">查询问卷结果</li>
              <li @click="handleMenuClick(goToOverview)">浏览{{ serverShortName }}</li>
              <li v-if="showStockListTool" @click="handleMenuClick(goBuildTool)">备货列表生成工具</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import { getConfig } from '@/api/System'
import { useRouter } from 'vue-router'

const configMap = ref({})
const logoUrl = ref('')
const serverShortName = ref('')
const showStockListTool = ref(false)
const router = useRouter()
const menuOpen = ref(false)
const menuRef = ref(null)

function toggleMenu(e) {
  e.stopPropagation()
  if (menuOpen.value) {
    menuOpen.value = false
    return
  }
  menuOpen.value = true
}

function handleClickOutside(event) {
  if (menuOpen.value && menuRef.value && !menuRef.value.contains(event.target)) {
    menuOpen.value = false
  }
}

watch(menuOpen, (val) => {
  if (val) {
    document.addEventListener('mousedown', handleClickOutside)
    document.addEventListener('touchstart', handleClickOutside)
  } else {
    document.removeEventListener('mousedown', handleClickOutside)
    document.removeEventListener('touchstart', handleClickOutside)
  }
})

onBeforeUnmount(() => {
  document.removeEventListener('mousedown', handleClickOutside)
  document.removeEventListener('touchstart', handleClickOutside)
})

function handleMenuClick(fn) {
  menuOpen.value = false
  setTimeout(() => {
    fn()
  }, 250) // 动画时长和菜单关闭一致
}

async function fetchConfig() {
  const CACHE_KEY = 'navbar_config_cache'
  const CACHE_EXPIRE = 10 * 60 * 1000 // 5分钟
  const cacheStr = localStorage.getItem(CACHE_KEY)
  if (cacheStr) {
    try {
      const cache = JSON.parse(cacheStr)
      if (Date.now() - cache.time < CACHE_EXPIRE) {
        applyConfig(cache.data)
        return
      }
    } catch (e) {}
  }
  try {
    const res = await getConfig()
    if (res && res.data) {
      localStorage.setItem(CACHE_KEY, JSON.stringify({
        time: Date.now(),
        data: res.data
      }))
      applyConfig(res.data)
    }
  } catch (e) {}
}

function applyConfig(data) {
  const map = {}
  data.forEach(item => {
    map[item.configKey] = item.configValue
  })
  configMap.value = map
  logoUrl.value = map['logo'] || ''
  serverShortName.value = map['server_short_name'] || ''
  showStockListTool.value = map['stock_list_tool'] === '1'
}

function goToHome() {
  // window.location.href = 'http://v4.sctserver.top:81/sctserver'
  window.location.href = 'http://localhost:5173/sctserver'
}
function goToOverview() {
  router.push('/overview')
}
function goBuildTool() {
  router.push('/build-tool')
}
function goToResult() {
  router.push('/msq/result')
}
function handleImageError() {
  // 图片加载失败时的处理
  console.warn('Logo图片加载失败')
}

onMounted(async () => {
  await fetchConfig()
})
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  height: 60px;
  background-color: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-image {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  object-fit: cover;
  /* box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); */
  transition: transform 0.3s ease;
}

.logo-image:hover {
  transform: scale(1.05);
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  color: #409EFF;
  letter-spacing: 2px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.logo:hover {
  color: #337ecc;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-btn {
  font-size: 1rem;
  color: #606266;
}

.nav-btn:hover {
  color: #409EFF;
}

.nav-pc {
  display: flex;
  align-items: center;
  gap: 20px;
}
.nav-mobile {
  display: none;
}

/* 手机端适配 */
@media (max-width: 768px) {
  .nav-pc {
    display: none;
  }
  .nav-mobile {
    display: flex;
    align-items: center;
  }
  .logo {
    font-size: 1.1rem;
  }
}

.hamburger-btn {
  width: 38px;
  height: 38px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: transparent;
  border: none;
  border-radius: 0;
  box-shadow: none;
  cursor: pointer;
  padding: 0;
  transition: background 0.2s;
  position: relative;
  z-index: 1001;
  outline: none;
  -webkit-tap-highlight-color: transparent;
}
.hamburger-btn:hover,
.hamburger-btn:active,
.hamburger-btn:focus {
  background: transparent !important;
  box-shadow: none !important;
  outline: none !important;
  border: none !important;
}
.hamburger-btn span {
  display: block;
  width: 22px;
  height: 3px;
  margin: 3px 0;
  background: #409EFF;
  border-radius: 2px;
  transition: all 0.3s cubic-bezier(.4,2,.6,1);
}
.hamburger-btn.open span:nth-child(1) {
  transform: translateY(6px) rotate(45deg);
}
.hamburger-btn.open span:nth-child(2) {
  opacity: 0;
}
.hamburger-btn.open span:nth-child(3) {
  transform: translateY(-6px) rotate(-45deg);
}

/* 菜单弹出动画 */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transform-origin: top;
}
.slide-fade-enter-active {
  animation: menuDown 0.35s cubic-bezier(.4,2,.6,1);
}
.slide-fade-leave-active {
  animation: menuUp 0.25s cubic-bezier(.4,2,.6,1) reverse;
}
@keyframes menuDown {
  from { opacity: 0; transform: translateY(-30px) scaleY(0.95); }
  to { opacity: 1; transform: translateY(0) scaleY(1); }
}
@keyframes menuUp {
  from { opacity: 1; transform: translateY(0) scaleY(1); }
  to { opacity: 0; transform: translateY(-30px) scaleY(0.95); }
}

.mobile-menu-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 998;
  background: transparent;
}
.mobile-full-menu.auto-height {
  position: fixed;
  top: 60px;
  left: 0;
  right: 0;
  background: rgba(255,255,255,0.7);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  z-index: 999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  border-radius: 0 0 16px 16px;
  height: auto;
  min-height: unset;
  max-height: 80vh;
  overflow: hidden;
  will-change: opacity, transform;
  opacity: 0;
  transform: translateY(-30px) scaleY(0.95);
  pointer-events: none;
  transition: opacity 0.3s cubic-bezier(.4,2,.6,1), transform 0.3s cubic-bezier(.4,2,.6,1);
}
.mobile-full-menu.auto-height.show {
  opacity: 1;
  transform: translateY(0) scaleY(1);
  pointer-events: auto;
}

.mobile-full-menu-content {
  width: 100%;
  padding: 0 0 10px 0;
  margin-top: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.mobile-menu {
  list-style: none;
  padding: 0;
  margin: 0;
  width: 100%;
  background: transparent;
}
.mobile-menu li {
  padding: 18px 0;
  font-size: 1.15rem;
  color: #333;
  text-align: center;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  border-radius: 0;
  margin: 0 18px;
}
.mobile-menu li:hover {
  background: #e6f0ff;
  color: #409EFF;
}
</style> 