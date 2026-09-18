<template>
  <div class="detail-page">
    <Navbar />

    <div class="detail-container">
      <header class="detail-hero">
        <el-button type="text" class="back-btn" @click="goBack">‹ 返回</el-button>
        <h1>建筑展览区</h1>
        <span class="count">共 {{ buildings.length }} 座</span>
      </header>

      <div class="detail-card">
        <div v-if="buildings.length" class="gallery-grid">
          <article v-for="item in buildings" :key="item.id" class="gallery-card">
            <div class="card-media">
              <img :src="item.img" :alt="item.title" class="card-img" loading="lazy" />
              <span v-if="item.top" class="top-tag floating">置顶</span>
            </div>
            <div class="card-title">{{ item.title }}</div>
          </article>
        </div>
        <div v-else class="empty-tip">暂无建筑展示</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'

const router = useRouter()

function goBack() {
  router.push('/overview')
}

const buildings = [
  { id: 1, title: '智慧大厦', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false },
  { id: 2, title: '绿色工厂', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: true },
  { id: 3, title: '创新中心', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false },
  { id: 4, title: '未来之家', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false }
]
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