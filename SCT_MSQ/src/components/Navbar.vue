<template>
  <nav class="navbar" :class="{ 'is-floating': isFloating }">
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
      <el-button type="text" class="nav-btn" @click="goToHome">首页</el-button>
      <el-button type="text" class="nav-btn" @click="goToResult">查询问卷结果</el-button>
      <el-button type="text" class="nav-btn" @click="goToOverview">浏览{{ serverShortName }}</el-button>
      <el-button v-if="showStockListTool" type="text" class="nav-btn" @click="goBuildTool">备货列表生成工具</el-button>
    </div>
    <!-- 手机端汉堡菜单 -->
    <div class="nav-mobile" ref="menuRef">
      <button class="hamburger-btn" :class="{ open: menuOpen }" @click="toggleMenu" :aria-label="menuOpen ? '关闭菜单' : '打开菜单'" :aria-expanded="menuOpen" aria-controls="mobile-navigation">
        <span></span>
        <span></span>
        <span></span>
      </button>
      <div v-show="menuOpen" class="mobile-menu-mask">
        <div id="mobile-navigation" class="mobile-full-menu auto-height" :class="{ show: menuOpen }">
          <div class="mobile-full-menu-content">
            <ul class="mobile-menu">
              <li @click="handleMenuClick(goToHome)">首页</li>
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
const isFloating = ref(false)
let desktopViewport

function updateFloating() {
  isFloating.value = desktopViewport?.matches && window.scrollY > 0
}

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
  window.removeEventListener('scroll', updateFloating)
  desktopViewport?.removeEventListener('change', updateFloating)
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
  try {
    const res = await getConfig()
    if (res && res.data) {
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
  // window.location.href = 'http://localhost:5173/sctserver'
  router.push('/sctserver')
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
  desktopViewport = window.matchMedia('(min-width: 769px)')
  updateFloating()
  window.addEventListener('scroll', updateFloating, { passive: true })
  desktopViewport.addEventListener('change', updateFloating)
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
  background-color: var(--shell);
  backdrop-filter: blur(2px) saturate(1.8);
  -webkit-backdrop-filter: blur(2px) saturate(1.8);
  box-shadow: var(--shadow-soft), var(--glass-spec);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  outline: 1px solid var(--shell-border);
}

@media (min-width: 769px) {
  .navbar {
    left: 50%;
    right: auto;
    width: 100%;
    box-sizing: border-box;
    border-radius: 0;
    transform: translate(-50%, 0);
    transition: width 0.5s var(--ease-out), height 0.5s var(--ease-out),
      padding 0.5s var(--ease-out), border-radius 0.5s var(--ease-out),
      transform 0.55s var(--ease-spring), box-shadow 0.3s;
  }
  .navbar.is-floating {
    width: min(1100px, calc(100% - 32px));
    height: 56px;
    padding: 0 20px;
    border-radius: var(--radius-pill);
    transform: translate(-50%, 12px);
    box-shadow: var(--shadow-lift), var(--glass-spec);
  }
  .nav-left { min-width: 0; }
  .logo {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .nav-pc { flex-shrink: 0; }
}
@media (min-width: 769px) and (max-width: 1000px) {
  .navbar .nav-pc { gap: 8px; }
  .navbar .nav-pc .nav-btn { padding-inline: 8px; }
}
@media (prefers-reduced-motion: reduce) {
  .navbar { transition: none; }
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-image {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  object-fit: cover;
  /* box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); */
  transition: transform 0.3s ease;
}

/* .logo-image:hover {
  transform: scale(1.05);
} */

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--aqua-500);
  letter-spacing: 0;
  cursor: pointer;
  transition: color 0.3s ease;
}

.logo:hover {
  color: var(--aqua-600);
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-btn {
  font-size: 1rem;
  color: var(--ink-700);
  font-weight: 500;
}

.nav-btn:hover {
  color: var(--ink-900);
  background: var(--count-bg);
}

.nav-pc {
  display: flex;
  align-items: center;
  gap: 20px;
}
.nav-pc .nav-btn {
  padding-inline: 14px;
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
  background: var(--sct-button-bg);
  border: none;
  border-radius: var(--radius-md);
  box-shadow: inset 0 0 0 1px var(--shell-border), var(--glass-spec);
  backdrop-filter: blur(2px) saturate(1.8);
  -webkit-backdrop-filter: blur(2px) saturate(1.8);
  cursor: pointer;
  padding: 0;
  transition: background 0.2s;
  position: relative;
  z-index: 1001;
  outline: none;
  -webkit-tap-highlight-color: transparent;
}
.hamburger-btn:hover,
.hamburger-btn:active {
  background: var(--sct-button-hover-bg);
  box-shadow: inset 0 0 0 1px var(--aqua-400), var(--glass-spec);
}
.hamburger-btn:focus-visible {
  outline: 3px solid var(--focus-ring);
  outline-offset: 3px;
  border-radius: var(--radius-md);
}
.hamburger-btn span {
  display: block;
  width: 22px;
  height: 2px;
  margin: 3px 0;
  background: var(--ink-700);
  border-radius: 2px;
  transition: transform 0.25s var(--ease-out), opacity 0.2s;
}
.hamburger-btn.open span:nth-child(1) {
  transform: translateY(8px) rotate(45deg);
}
.hamburger-btn.open span:nth-child(2) {
  opacity: 0;
}
.hamburger-btn.open span:nth-child(3) {
  transform: translateY(-8px) rotate(-45deg);
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
  background: color-mix(in srgb, var(--mist-50) 70%, var(--shell));
  backdrop-filter: blur(18px) saturate(1.4);
  -webkit-backdrop-filter: blur(18px) saturate(1.4);
  z-index: 999;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  box-shadow: var(--shadow-soft), var(--glass-spec);
  border-radius: 0 0 var(--radius-md) var(--radius-md);
  height: auto;
  min-height: unset;
  max-height: 80vh;
  overflow: hidden;
  will-change: opacity, transform;
  opacity: 0;
  transform: translateY(-30px) scaleY(0.95);
  pointer-events: none;
  transition: opacity 0.3s cubic-bezier(.4,2,.6,1), transform 0.3s cubic-bezier(.4,2,.6,1);
  outline: 1px solid var(--shell-border);
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
  color: var(--ink-900);
  text-align: center;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  border-radius: 0;
  margin: 0 18px;
}
.mobile-menu li:hover {
  background: var(--shell);
  color: var(--aqua-600);
}
</style> 