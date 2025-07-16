<template>
  <div>
    <Navbar />
    <div class="overview-container">
      <!-- 成员留言区 -->
      <section class="overview-section" style="margin-top: 100px;">
        <div class="section-header">
          <h2>成员留言区</h2>
          <span class="more-link" @click="goToDetail('member')">查看更多 ></span>
        </div>
        <div class="section-content">
          <ul class="message-list">
            <li v-for="item in memberMessages.slice(0, 3)" :key="item.id" class="message-item" style="position:relative;">
              <img :src="item.avatar" class="avatar" alt="头像" />
              <span class="user-name">{{ item.user }} ：</span>
              <span class="user-message">{{ item.message }}</span>
              <span v-if="item.top" class="top-tag">置顶</span>
            </li>
          </ul>
        </div>
      </section>
      <!-- 机器展览区 -->
      <section class="overview-section">
        <div class="section-header">
          <h2>机器展览区</h2>
          <span
            v-if="machines.length > machinePreviewCountFinal"
            class="more-link"
            @click="goToDetail('machine')"
          >查看更多 ></span>
        </div>
        <div class="section-content preview-list" ref="machineContainer">
          <div v-for="item in machines.slice(0, machinePreviewCountFinal)" :key="item.id" class="preview-item" style="position:relative;">
            <img :src="item.img" :alt="item.title" class="preview-img" />
            <div class="preview-title">{{ item.title }}</div>
            <span v-if="item.top" class="top-tag">置顶</span>
          </div>
        </div>
      </section>
      <!-- 建筑展览区 -->
      <section class="overview-section">
        <div class="section-header">
          <h2>建筑展览区</h2>
          <span
            v-if="buildings.length > buildingPreviewCountFinal"
            class="more-link"
            @click="goToDetail('building')"
          >查看更多 ></span>
        </div>
        <div class="section-content preview-list" ref="buildingContainer">
          <div v-for="item in buildings.slice(0, buildingPreviewCountFinal)" :key="item.id" class="preview-item" style="position:relative;">
            <img :src="item.img" :alt="item.title" class="preview-img" />
            <div class="preview-title">{{ item.title }}</div>
            <span v-if="item.top" class="top-tag">置顶</span>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'

const router = useRouter()

const memberMessages = [
  { id: 1, user: 'Chengzi_er_', message: '大家好，这里是SCT！', avatar: 'https://mc-heads.net/avatar/Chengzi_er_', top: true },
  { id: 2, user: 'CNJ233', message: '欢迎来到留言区。', avatar: 'https://mc-heads.net/avatar/CNJ233', top: false },
  { id: 3, user: 'Xc_Star', message: '祝大家天天开心！', avatar: 'https://mc-heads.net/avatar/Xc_Star', top: false },
  { id: 4, user: 'late_maple', message: '新成员报到~', avatar: 'https://mc-heads.net/avatar/late_maple', top: true }
]

const machines = [
  { id: 1, title: '墨鱼塔', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: true },
  { id: 2, title: '1.21全物品', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false },
  { id: 3, title: '合成站', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false },
  { id: 4, title: '女巫塔', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false },
  { id: 5, title: '四连鱼塔', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: true }
]

const buildings = [
  { id: 1, title: 'SCT主世界大厅', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false },
  { id: 2, title: 'SCT地狱大厅', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: true },
  { id: 3, title: 'Xc_Star的狗窝', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false },
  { id: 4, title: '天空之橙', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false },
  { id: 5, title: 'SCT末地大厅', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false }
]

const machinePreviewCount = ref(3)
const buildingPreviewCount = ref(3)
const cardWidth = 150 + 32 // 卡片宽度+gap，和样式保持一致

const machineContainer = ref(null)
const buildingContainer = ref(null)

const isMobile = ref(false)

function checkIsMobile() {
  isMobile.value = window.innerWidth <= 800
}

function calcPreviewCount(containerRef, countRef) {
  if (!containerRef.value) return
  const width = containerRef.value.offsetWidth
  countRef.value = Math.max(1, Math.floor(width / cardWidth))
}

function handleResize() {
  calcPreviewCount(machineContainer, machinePreviewCount)
  calcPreviewCount(buildingContainer, buildingPreviewCount)
}

onMounted(() => {
  nextTick(() => {
    handleResize()
    checkIsMobile()
    window.addEventListener('resize', handleResize)
    window.addEventListener('resize', checkIsMobile)
  })
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('resize', checkIsMobile)
})

const machinePreviewCountFinal = computed(() => isMobile.value ? 3 : machinePreviewCount.value)
const buildingPreviewCountFinal = computed(() => isMobile.value ? 3 : buildingPreviewCount.value)

function goToDetail(type) {
  router.push(`/overview/${type}`)
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
  return 'SCT官网'
}

onMounted(async () => {
  document.title = getServerShortName() + '官网'
})
</script>

<style scoped>
.overview-container {
  max-width: 900px;
  margin: 40px auto;
  padding: 0 16px;
}
.overview-section {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  margin-bottom: 32px;
  padding: 32px 24px;
}
.overview-section h2 {
  margin-top: 0;
  margin-bottom: 22px;
  font-size: 1.7rem;
  color: #2d3a4b;
  font-weight: bold;
  letter-spacing: 1px;
  text-align: left;
}
.section-content {
  min-height: 60px;
  color: #666;
  font-size: 1.1rem;
}
.preview-list {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  margin-bottom: 10px;
}
.preview-item {
  width: 160px;
  text-align: center;
}
.preview-img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
  background: #f2f2f2;
  margin-bottom: 8px;
  display: block;
  margin-left: auto;
  margin-right: auto;
}
.preview-title {
  font-size: 1.08rem;
  color: #222;
  font-weight: 500;
}
.more-btn {
  margin-top: 16px;
  padding: 6px 18px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  transition: background 0.2s;
}
.more-btn:hover {
  background: #3076c9;
}
.message-list {
  padding-left: 0;
  margin-bottom: 0;
}
.message-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  list-style: none;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 12px;
  background: #f2f2f2;
}
.user-name {
  font-weight: 500;
  color: #222;
  margin-right: 8px;
}
.user-message {
  color: #555;
}
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}
.section-header h2 {
  margin: 0;
  font-size: 1.7rem;
  color: #2d3a4b;
  font-weight: bold;
  letter-spacing: 1px;
  text-align: left;
}
.more-btn {
  padding: 6px 18px;
  background: linear-gradient(90deg,#409eff 0%,#66b1ff 100%);
  color: #fff;
  border: none;
  border-radius: 18px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(64,158,255,0.10);
  transition: background 0.2s, box-shadow 0.2s, transform 0.15s;
  letter-spacing: 1px;
  margin-left: 18px;
}
.more-btn:hover {
  background: linear-gradient(90deg,#3076c9 0%,#409eff 100%);
  box-shadow: 0 6px 18px rgba(64,158,255,0.18);
  transform: translateY(-2px) scale(1.03);
}
.more-link {
  /* color: #409eff; */
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  margin-left: 18px;
  transition: text-decoration 0.2s;
}
.more-link:hover {
  text-decoration: underline;
}
.top-tag {
  position: absolute;
  top: 6px;
  right: 8px;
  background: #ff9800;
  color: #fff;
  font-size: 0.85rem;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: bold;
  z-index: 2;
}

@media (max-width: 800px) {
  .overview-container {
    padding: 0 4px;
  }
  .overview-section {
    padding: 18px 6px 16px 6px;
    border-radius: 10px;
    margin-bottom: 18px;
  }
  .section-header {
    margin-bottom: 10px;
  }
  .section-header h2 {
    font-size: 1.13rem;
  }
  .more-link {
    font-size: 0.98rem;
    margin-left: 8px;
  }
  .preview-list {
    display: flex;
    flex-wrap: nowrap;
    gap: 8px;
    justify-content: flex-start;
    overflow-x: auto;
    padding-bottom: 2px;
  }
  .preview-item {
    flex: 1 1 0;
    min-width: 0;
    max-width: none;
    width: 33.33vw;
    box-sizing: border-box;
    padding: 8px 2px 8px 2px;
    border-radius: 8px;
  }
  .preview-img {
    width: 80%;
    height: 80px;
    border-radius: 6px;
    margin-bottom: 6px;
    margin-left: auto;
    margin-right: auto;
    display: block;
  }
  .preview-title {
    font-size: 0.98rem;
    padding-top: 4px;
  }
  .message-list {
    gap: 10px;
  }
  .message-item {
    padding: 8px 6px;
    border-radius: 7px;
  }
  .avatar {
    width: 28px;
    height: 28px;
    margin-right: 7px;
  }
  .user-name {
    font-size: 0.98rem;
    margin-right: 4px;
  }
  .user-message {
    font-size: 0.95rem;
    padding: 4px 8px;
  }
}
</style> 