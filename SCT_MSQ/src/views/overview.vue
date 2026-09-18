<template>
  <div class="overview-page">
    <Navbar />

    <div class="overview-container">
      <header class="page-hero">
        <h1 class="hero-title">浏览 {{ serverShortName }}</h1>
      </header>

      <!-- 成员留言区 -->
      <section class="overview-section">
        <div class="section-header">
          <h2>成员留言区</h2>
          <el-button type="text" class="more-link" @click="goToDetail('member')">查看更多</el-button>
        </div>

        <div class="section-content">
          <ul v-if="memberMessages.length" class="message-list">
            <li v-for="item in memberMessages.slice(0, 3)" :key="item.id" class="message-item">
              <img :src="item.avatar" class="avatar" alt="头像" loading="lazy" @error="handleAvatarError" />
              <div class="message-body">
                <div class="message-meta">
                  <span class="user-name">{{ item.playerId }}</span>
                  <span v-if="item.top" class="top-tag">置顶</span>
                </div>
                <p class="user-message">{{ item.content }}</p>
              </div>
            </li>
          </ul>
          <div v-else-if="messageLoadFailed" class="empty-tip">留言加载失败，请稍后再试</div>
          <div v-else class="empty-tip">还没有留言，快去留言吧</div>
        </div>
      </section>

      <!-- 机器展览区 -->
      <section class="overview-section">
        <div class="section-header">
          <h2>机器展览区</h2>
          <el-button
            v-if="machines.length > machinePreviewCountFinal"
            type="text"
            class="more-link"
            @click="goToDetail('machine')"
          >查看更多</el-button>
        </div>
        <div class="gallery-grid" ref="machineContainer">
          <article
            v-for="item in machines.slice(0, machinePreviewCountFinal)"
            :key="item.id"
            class="gallery-card"
          >
            <div class="card-media">
              <img :src="item.img" :alt="item.title" class="card-img" loading="lazy" />
              <span v-if="item.top" class="top-tag floating">置顶</span>
            </div>
            <div class="card-title">{{ item.title }}</div>
          </article>
        </div>
      </section>

      <!-- 建筑展览区 -->
      <section class="overview-section">
        <div class="section-header">
          <h2>建筑展览区</h2>
          <el-button
            v-if="buildings.length > buildingPreviewCountFinal"
            type="text"
            class="more-link"
            @click="goToDetail('building')"
          >查看更多</el-button>
        </div>
        <div class="gallery-grid" ref="buildingContainer">
          <article
            v-for="item in buildings.slice(0, buildingPreviewCountFinal)"
            :key="item.id"
            class="gallery-card"
          >
            <div class="card-media">
              <img :src="item.img" :alt="item.title" class="card-img" loading="lazy" />
              <span v-if="item.top" class="top-tag floating">置顶</span>
            </div>
            <div class="card-title">{{ item.title }}</div>
          </article>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, computed } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'
import { getMemberMessageList } from '@/api/MemberMessage'

const router = useRouter()

const memberMessages = ref([])
const messageLoadFailed = ref(false)
const serverShortName = ref('SCT')

async function fetchMemberMessages() {
  try {
    const res = await getMemberMessageList()
    memberMessages.value = Array.isArray(res?.data) ? res.data : []
    messageLoadFailed.value = false
  } catch (e) {
    memberMessages.value = []
    messageLoadFailed.value = true
  }
}

function handleAvatarError(e) {
  e.target.style.visibility = 'hidden'
}

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
// 与 .gallery-grid 的 minmax 最小宽度、gap 保持一致
const cardMinWidth = 200
const cardGap = 20

const machineContainer = ref(null)
const buildingContainer = ref(null)

const isMobile = ref(false)

function checkIsMobile() {
  isMobile.value = window.innerWidth <= 800
}

function calcPreviewCount(containerRef, countRef) {
  if (!containerRef.value) return
  const width = containerRef.value.offsetWidth
  countRef.value = Math.max(1, Math.floor((width + cardGap) / (cardMinWidth + cardGap)))
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

const machinePreviewCountFinal = computed(() => isMobile.value ? 4 : machinePreviewCount.value)
const buildingPreviewCountFinal = computed(() => isMobile.value ? 4 : buildingPreviewCount.value)

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
  return 'SCT'
}

onMounted(async () => {
  serverShortName.value = getServerShortName()
  document.title = serverShortName.value + '官网'
  await fetchMemberMessages()
})
</script>

<style scoped>
.overview-page {
  min-height: 100vh;
  background: #f5f7fa;
  color: #303133;
}

.overview-container {
  max-width: 1040px;
  margin: 0 auto;
  padding: 92px 20px 60px;
}

.page-hero {
  margin-bottom: 20px;
}
.hero-title {
  margin: 0;
  font-size: 1.75rem;
  font-weight: bold;
  letter-spacing: 2px;
  background: linear-gradient(90deg, #409EFF 30%, #66b1ff 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* ---------- 区块 ---------- */
.overview-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
  padding-bottom: 20px;
}
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 20px;
  border-bottom: 1px solid #ebeef5;
}
.section-header h2 {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
  font-size: 1.05rem;
  font-weight: 600;
  color: #303133;
  letter-spacing: 1px;
}
.section-header h2::before {
  content: '';
  width: 3px;
  height: 16px;
  border-radius: 2px;
  background: #409EFF;
}
.more-link {
  flex-shrink: 0;
  font-size: 0.9rem;
  color: #409EFF;
  padding: 0;
}

.section-content {
  padding: 8px 20px 0;
}

/* ---------- 留言 ---------- */
.message-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.message-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid #ebeef5;
}
.message-item:last-child {
  border-bottom: none;
}
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  background: #f5f7fa;
  flex-shrink: 0;
}
.message-body {
  min-width: 0;
  flex: 1;
}
.message-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.user-name {
  font-size: 0.92rem;
  font-weight: 600;
  color: #303133;
}
.user-message {
  margin: 0;
  color: #606266;
  font-size: 0.92rem;
  line-height: 1.6;
  word-break: break-word;
}

.empty-tip {
  padding: 32px 0;
  text-align: center;
  color: #909399;
  font-size: 0.92rem;
}

/* ---------- 图集 ---------- */
.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  padding: 16px 20px 0;
}
.gallery-card {
  position: relative;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}
.gallery-card:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.card-media {
  position: relative;
  aspect-ratio: 4 / 3;
  background: #f5f7fa;
  overflow: hidden;
}
.card-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.card-title {
  padding: 10px 12px;
  font-size: 0.92rem;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.top-tag {
  display: inline-block;
  padding: 0 7px;
  line-height: 20px;
  border-radius: 4px;
  background: #fdf6ec;
  border: 1px solid #f5dab1;
  color: #e6a23c;
  font-size: 0.72rem;
}
.top-tag.floating {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(253, 246, 236, 0.95);
}

@media (max-width: 800px) {
  .overview-container {
    padding: 78px 12px 36px;
  }
  .hero-title {
    font-size: 1.3rem;
  }
  .overview-section {
    margin-bottom: 14px;
    padding-bottom: 14px;
  }
  .section-header {
    padding: 12px 14px;
  }
  .section-header h2 {
    font-size: 0.98rem;
  }
  .more-link {
    font-size: 0.85rem;
  }
  .section-content {
    padding: 4px 14px 0;
  }
  .gallery-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
    padding: 12px 14px 0;
  }
  .card-title {
    padding: 8px 10px;
    font-size: 0.88rem;
  }
  .message-item {
    padding: 12px 0;
    gap: 10px;
  }
  .avatar {
    width: 34px;
    height: 34px;
  }
  .user-message {
    font-size: 0.88rem;
  }
}
</style> 