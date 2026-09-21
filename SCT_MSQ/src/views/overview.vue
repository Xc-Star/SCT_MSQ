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

      <!-- 机器展览区 / 建筑展览区 / 其他内容 -->
      <section
        v-for="(section, si) in sections"
        :key="section.key"
        class="overview-section"
      >
        <div class="section-header">
          <h2>{{ section.title }}</h2>
          <el-button
            v-if="section.items.length > visibleCount(section)"
            type="text"
            class="more-link"
            @click="goToDetail(section.route)"
          >查看更多</el-button>
        </div>

        <div class="gallery-grid" :ref="el => setContainerRef(el, si)">
          <article
            v-for="item in section.items.slice(0, visibleCount(section))"
            :key="item.id"
            class="gallery-card"
            @click="openDetail(item)"
          >
            <div class="card-media">
              <img :src="item.cover" :alt="item.title" class="card-img" loading="lazy" />
              <span v-if="item.top" class="top-tag floating">置顶</span>
              <span v-if="item.imageCount > 1" class="count-tag floating">
                <el-icon><Picture /></el-icon>{{ item.imageCount }}
              </span>
            </div>
            <div class="card-title">{{ item.title }}</div>
          </article>
        </div>

        <div v-if="!section.items.length" class="empty-tip">
          {{ section.failed ? '内容加载失败，请稍后再试' : '暂无内容' }}
        </div>
      </section>
    </div>

    <ExhibitionDetailDialog v-model="detailVisible" :exhibition-id="activeId" />
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'
import Navbar from '@/components/Navbar.vue'
import ExhibitionDetailDialog from '@/components/ExhibitionDetailDialog.vue'
import { getMemberMessageList } from '@/api/MemberMessage'
import { getExhibitionList } from '@/api/Exhibition'
import { getServerShortName } from '@/api/System'

const router = useRouter()

const memberMessages = ref([])
const messageLoadFailed = ref(false)
const serverShortName = ref('SCT')

/* ---------------- 三个展览区块 ---------------- */
// key 是后端 category，route 是「查看更多」跳转的路径
const sections = ref([
  { key: 'redstone', route: 'machine', title: '机器展览区', items: [], failed: false, previewCount: 3 },
  { key: 'building', route: 'building', title: '建筑展览区', items: [], failed: false, previewCount: 3 },
  { key: 'other', route: 'other', title: '其他内容', items: [], failed: false, previewCount: 3 }
])

const detailVisible = ref(false)
const activeId = ref(null)

function openDetail(item) {
  activeId.value = item.id
  detailVisible.value = true
}

async function fetchExhibitions() {
  await Promise.all(
    sections.value.map(async (section) => {
      try {
        const res = await getExhibitionList(section.key)
        section.items = Array.isArray(res?.data) ? res.data : []
        section.failed = false
      } catch (e) {
        section.items = []
        section.failed = true
      }
    })
  )
}

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

/* ---------------- 预览数量按容器宽度自适应 ---------------- */
const containerRefs = ref([])
const setContainerRef = (el, index) => {
  containerRefs.value[index] = el
}

// 与 .gallery-grid 的 minmax 最小宽度、gap 保持一致
const cardMinWidth = 200
const cardGap = 20

const isMobile = ref(false)

function checkIsMobile() {
  isMobile.value = window.innerWidth <= 800
}

function handleResize() {
  checkIsMobile()
  sections.value.forEach((section, index) => {
    const el = containerRefs.value[index]
    if (!el) return
    const width = el.offsetWidth
    section.previewCount = Math.max(1, Math.floor((width + cardGap) / (cardMinWidth + cardGap)))
  })
}

function visibleCount(section) {
  return isMobile.value ? 4 : section.previewCount
}

function goToDetail(type) {
  router.push(`/overview/${type}`)
}

onMounted(async () => {
  serverShortName.value = await getServerShortName()
  document.title = serverShortName.value + '官网'

  await fetchMemberMessages()
  await fetchExhibitions()

  nextTick(() => {
    handleResize()
    window.addEventListener('resize', handleResize)
  })
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.overview-page {
  min-height: 100vh;
  background: transparent;
  color: var(--ink-900);
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
  letter-spacing: 0;
  color: var(--aqua-600);
}

/* ---------- 区块 ---------- */
.overview-section {
  background: var(--shell);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-soft), var(--glass-spec);
  margin-bottom: 20px;
  padding-bottom: 20px;
  outline: 1px solid var(--shell-border);
  backdrop-filter: blur(2px) saturate(1.8);
  -webkit-backdrop-filter: blur(2px) saturate(1.8);
}
.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 20px;
  border-bottom: 1px solid var(--hair);
}
.section-header h2 {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
  font-size: 1.05rem;
  font-weight: 600;
  color: var(--ink-900);
  letter-spacing: 0;
}
.section-header h2::before {
  content: '';
  width: 3px;
  height: 16px;
  border-radius: 2px;
  background: var(--aqua-500);
}
.more-link {
  flex-shrink: 0;
  font-size: 0.9rem;
  color: var(--ink-700);
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
  border-bottom: 1px solid var(--hair);
}
.message-item:last-child {
  border-bottom: none;
}
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  background: var(--count-bg);
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
  color: var(--ink-900);
}
.user-message {
  margin: 0;
  color: var(--ink-500);
  font-size: 0.92rem;
  line-height: 1.6;
  word-break: break-word;
}

.empty-tip {
  padding: 32px 20px 12px;
  text-align: center;
  color: var(--ink-500);
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
  background: var(--shell);
  border: 1px solid var(--shell-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
  box-shadow: var(--shadow-soft), var(--glass-spec);
}
.gallery-card:hover {
  box-shadow: var(--shadow-lift), var(--glass-spec);
  transform: translateY(-6px) scale(1.015);
}
.card-media {
  position: relative;
  aspect-ratio: 4 / 3;
  background: var(--count-bg);
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
  color: var(--ink-900);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.top-tag {
  display: inline-block;
  padding: 0 7px;
  line-height: 20px;
  border-radius: var(--radius-pill);
  background: var(--tag-bg);
  border: 1px solid var(--hair);
  color: var(--aqua-600);
  font-size: 0.72rem;
}
.top-tag.floating {
  position: absolute;
  top: 8px;
  right: 8px;
  background: var(--tag-bg);
}
.count-tag {
  position: absolute;
  bottom: 8px;
  right: 8px;
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 0 7px;
  line-height: 20px;
  border-radius: var(--radius-pill);
  background: var(--overlay-bg);
  color: #fff;
  font-size: 0.72rem;
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
  .empty-tip {
    padding: 24px 14px 8px;
  }
}
</style>
