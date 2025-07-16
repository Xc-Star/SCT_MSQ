<template>
  <div class="detail-container">
    <h2>建筑展览区 - 全部建筑</h2>
    <div class="detail-list">
      <div v-for="item in buildings" :key="item.id" class="detail-item" style="position:relative;">
        <img :src="item.img" :alt="item.title" class="detail-img" />
        <div class="detail-title">{{ item.title }}</div>
        <span v-if="item.top" class="top-tag">置顶</span>
      </div>
    </div>
  </div>
</template>

<script setup>
const buildings = [
  { id: 1, title: '智慧大厦', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false },
  { id: 2, title: '绿色工厂', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: true },
  { id: 3, title: '创新中心', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false },
  { id: 4, title: '未来之家', img: '/profile/upload/2025/07/02/123054495_p2_20250702014641A001.jpg', top: false }
]
</script>

<style scoped>
.detail-container {
  max-width: 700px;
  margin: 40px auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  padding: 32px 24px;
}
.detail-container h2 {
  margin-bottom: 18px;
  font-size: 1.5rem;
  color: #333;
  font-weight: bold;
}
.detail-list {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}
.detail-item {
  width: 160px;
  text-align: center;
}
.detail-img {
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
.detail-title {
  font-size: 1.08rem;
  color: #222;
  font-weight: 500;
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
</style> 