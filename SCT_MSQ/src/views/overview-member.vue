<template>
  <div class="detail-container">
    <h2>成员留言区 - 全部留言</h2>
    <ul class="message-list">
      <li v-for="item in memberMessages" :key="item.id" class="message-item" style="position:relative;">
        <img :src="item.avatar" class="avatar" alt="头像" />
        <span class="user-name">{{ item.user }}</span>
        <span class="user-message">{{ item.message }}</span>
        <span v-if="item.top" class="top-tag">置顶</span>
      </li>
    </ul>
  </div>
</template>

<script setup>
const memberMessages = [
  { id: 1, user: '小明', message: '大家好，这里是SCT！', avatar: 'https://mc-heads.net/avatar/Xc_Star', top: true },
  { id: 2, user: '小红', message: '欢迎来到留言区。', avatar: 'https://mc-heads.net/avatar/Xc_Star', top: false },
  { id: 3, user: '小刚', message: '祝大家天天开心！', avatar: 'https://mc-heads.net/avatar/Xc_Star', top: false },
  { id: 4, user: '小李', message: '新成员报到~', avatar: 'https://mc-heads.net/avatar/Xc_Star', top: true }
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
.detail-container ul {
  padding-left: 0;
}
.detail-container li {
  list-style: none;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  font-size: 1.1rem;
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