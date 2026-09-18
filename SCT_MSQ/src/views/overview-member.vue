<template>
  <div class="detail-container">
    <h2>成员留言区</h2>
    <ul v-if="memberMessages.length" class="message-list">
      <li v-for="item in memberMessages" :key="item.id" class="message-item" style="position:relative;">
        <img :src="item.avatar" class="avatar" alt="头像" @error="handleAvatarError" />
        <span class="user-name">{{ item.playerId }}</span>
        <span class="user-message">{{ item.content }}</span>
        <span v-if="item.top" class="top-tag">置顶</span>
      </li>
    </ul>
    <div v-else-if="loadFailed" class="empty-tip">留言加载失败，请稍后再试</div>
    <div v-else-if="!loading" class="empty-tip">还没有留言，快去留言吧~</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMemberMessageList } from '@/api/MemberMessage'

const memberMessages = ref([])
const loading = ref(true)
const loadFailed = ref(false)

async function fetchMemberMessages() {
  try {
    const res = await getMemberMessageList()
    memberMessages.value = Array.isArray(res?.data) ? res.data : []
    loadFailed.value = false
  } catch (e) {
    memberMessages.value = []
    loadFailed.value = true
  } finally {
    loading.value = false
  }
}

function handleAvatarError(e) {
  e.target.style.visibility = 'hidden'
}

onMounted(fetchMemberMessages)
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
.empty-tip {
  color: #999;
  font-size: 1rem;
  padding: 8px 0;
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