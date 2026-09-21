<template>
  <div class="detail-page">
    <Navbar />

    <div class="detail-container">
      <header class="detail-hero">
        <el-button type="text" class="back-btn" @click="goBack">‹ 返回</el-button>
        <h1>成员留言区</h1>
        <span class="count">共 {{ memberMessages.length }} 条</span>
      </header>

      <div class="detail-card">
        <div v-if="loading" class="skeleton-list">
          <div v-for="n in 4" :key="n" class="skeleton-item"></div>
        </div>
        <ul v-else-if="memberMessages.length" class="message-list">
          <li v-for="item in memberMessages" :key="item.id" class="message-item">
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
        <div v-else-if="loadFailed" class="empty-tip">留言加载失败，请稍后再试</div>
        <div v-else class="empty-tip">还没有留言，快去留言吧</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Navbar from '@/components/Navbar.vue'
import { getMemberMessageList } from '@/api/MemberMessage'

const router = useRouter()
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

function goBack() {
  router.push('/overview')
}

onMounted(fetchMemberMessages)
</script>

<style scoped>
.detail-page {
  min-height: 100vh;
  background: transparent;
  color: var(--ink-900);
}

.detail-container {
  max-width: 820px;
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
  color: var(--aqua-500);
}
.detail-hero h1 {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 600;
  letter-spacing: 0;
}
.count {
  font-size: 0.85rem;
  color: var(--ink-500);
}

.detail-card {
  background: var(--shell);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-soft), var(--glass-spec);
  padding: 4px 20px;
  outline: 1px solid var(--shell-border);
  backdrop-filter: blur(2px) saturate(1.8);
  -webkit-backdrop-filter: blur(2px) saturate(1.8);
}

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

.empty-tip {
  padding: 40px 0;
  text-align: center;
  color: var(--ink-500);
  font-size: 0.92rem;
}

.skeleton-list {
  padding: 6px 0;
}
.skeleton-item {
  height: 56px;
  margin: 14px 0;
  border-radius: var(--radius-md);
  background: var(--count-bg);
}

@media (max-width: 800px) {
  .detail-container {
    padding: 78px 12px 36px;
  }
  .detail-hero h1 {
    font-size: 1.1rem;
  }
  .detail-card {
    padding: 4px 14px;
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