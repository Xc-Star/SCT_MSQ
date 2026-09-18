<template>
  <div class="detail-page">
    <Navbar />

    <div class="detail-container">
      <header class="detail-hero">
        <el-button type="text" class="back-btn" @click="goBack">‹ 返回</el-button>
        <h1>{{ title }}</h1>
        <span class="count">共 {{ list.length }} {{ unit }}</span>
      </header>

      <div class="detail-card">
        <div v-if="loading" class="gallery-grid">
          <div v-for="n in 6" :key="n" class="skeleton-card"></div>
        </div>

        <div v-else-if="list.length" class="gallery-grid">
          <article
            v-for="item in list"
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

        <div v-else-if="loadFailed" class="empty-tip">加载失败，请稍后再试</div>
        <div v-else class="empty-tip">暂无{{ title }}内容</div>
      </div>
    </div>

    <ExhibitionDetailDialog v-model="detailVisible" :exhibition-id="activeId" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Picture } from '@element-plus/icons-vue'
import Navbar from '@/components/Navbar.vue'
import ExhibitionDetailDialog from '@/components/ExhibitionDetailDialog.vue'
import { getExhibitionList } from '@/api/Exhibition'

const props = defineProps({
  /** 分类 redstone / building / other */
  category: { type: String, required: true },
  /** 页面标题 */
  title: { type: String, required: true },
  /** 计数单位，例如 台 / 座 / 个 */
  unit: { type: String, default: '个' }
})

const router = useRouter()

const list = ref([])
const loading = ref(true)
const loadFailed = ref(false)

const detailVisible = ref(false)
const activeId = ref(null)

function goBack() {
  router.push('/overview')
}

function openDetail(item) {
  activeId.value = item.id
  detailVisible.value = true
}

async function fetchList() {
  loading.value = true
  try {
    const res = await getExhibitionList(props.category)
    list.value = Array.isArray(res?.data) ? res.data : []
    loadFailed.value = false
  } catch (e) {
    list.value = []
    loadFailed.value = true
  } finally {
    loading.value = false
  }
}

onMounted(fetchList)
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  color: #303133;
}

.detail-container {
  max-width: 1040px;
  margin: 0 auto;
  padding: 92px 20px 60px;
}
.detail-hero {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.back-btn {
  padding: 0;
  font-size: 0.92rem;
  color: #409EFF;
}
.detail-hero h1 {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 600;
  letter-spacing: 1px;
}
.count {
  font-size: 0.85rem;
  color: #909399;
}

.detail-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}
.gallery-card {
  position: relative;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
}
.gallery-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
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
.count-tag {
  position: absolute;
  bottom: 8px;
  right: 8px;
  display: inline-flex;
  align-items: center;
  gap: 3px;
  padding: 0 7px;
  line-height: 20px;
  border-radius: 4px;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 0.72rem;
}

.skeleton-card {
  height: 176px;
  border-radius: 8px;
  background: #f5f7fa;
}

.empty-tip {
  padding: 40px 0;
  text-align: center;
  color: #909399;
  font-size: 0.92rem;
}

@media (max-width: 800px) {
  .detail-container {
    padding: 78px 12px 36px;
  }
  .detail-hero h1 {
    font-size: 1.1rem;
  }
  .detail-card {
    padding: 14px;
  }
  .gallery-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }
  .card-title {
    padding: 8px 10px;
    font-size: 0.88rem;
  }
}
</style>
