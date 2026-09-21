<template>
  <div class="msq-container" ref="msqContainer">
    <Navbar />
    
    <el-dialog v-model="showDialog" class="result-query-dialog" title="请输入你的ID" :width="dialogWidth" :close-on-click-modal="false" :show-close="true" @close="handleCancel" :class="{'mobile-dialog': isMobile}">
        <el-input v-model="inputId" aria-label="你的ID" placeholder="请输入你的ID" @keyup.enter="handleConfirm" spellcheck="false" autocapitalize="off" />
        <template #footer>
          <div class="query-dialog-actions">
            <el-button @click="handleCancel">返回</el-button>
            <el-button @click="handleConfirm" type="primary">确定</el-button>
          </div>
        </template>
      </el-dialog>
    <div v-if="loading" class="container">
      <ContentSkeleton variant="form" />
    </div>
    <div v-else-if="error" class="error-message">
      <h3>加载失败</h3>
      <p>{{ error }}</p>
      <button class="button2" @click="retryFetch">重试</button>
    </div>
    <div class="container" v-if="!loading && !error && topic.topicResults && topic.topicResults.length">
      <form class="msq-form">
        <div class="title" style="margin-bottom: 64px; position: relative;">
          <h2>{{ topic.msqName }}</h2>
          <template v-if="[1,2,3,4].includes(topic.status)">
            <div class="stamp" :class="getStampClass(topic.status)">
              {{ getStampText(topic.status) }}
            </div>
          </template>
        </div>
        <div class="msq-topic" v-for="(topic, index) in parsedTopics" :key="topic.topicId">
          <div v-if="topic.type === 'input'">
            <div class="input-container">
              <input required type="text" :value="topic.topicResult" disabled />
              <label class="label">{{ index + 1 }}. {{ topic.topic }}</label>
              <div class="underline"></div>
            </div>
            <div v-if="topic.images && topic.images.length" class="topic-images">
              <img
                v-for="img in topic.images"
                :key="img.id"
                :src="getImageUrl(img.imageUrl)"
                class="topic-image"
                alt="题目图片"
                @click="openImageViewer(getImageUrl(img.imageUrl))"
                style="cursor: pointer;"
              />
            </div>
            <div style="height: 50px;"></div>
          </div>
          <div v-if="topic.type === 'file'">
            <div class="input-container">
              <label>{{ index + 1 }}. {{ topic.topic }}</label>
            </div>
            <div v-if="topic.images && topic.images.length" class="topic-images">
              <img
                v-for="img in topic.images"
                :key="img.id"
                :src="getImageUrl(img.imageUrl)"
                class="topic-image"
                alt="题目图片"
                @click="openImageViewer(getImageUrl(img.imageUrl))"
                style="cursor: pointer;"
              />
            </div>
            <div v-if="topic.files && topic.files.length" class="topic-files" style="margin-top: 12px;">
              <div v-for="(file, fileIndex) in topic.files" :key="fileIndex" style="margin-bottom: 8px;">
                <a :href="getImageUrl(file)" target="_blank" style="color: var(--aqua-500); text-decoration: underline;">
                  {{ getFileName(file) }}
                </a>
              </div>
            </div>
            <div style="height: 50px;"></div>
          </div>
          <div v-if="topic.type === 'radio'">
            {{ index + 1 }}. {{ topic.topic }}
            <div class="radio-button-container">
              <div class="radio-button" v-for="(option, optionIndex) in topic.options" :key="optionIndex">
                <input type="radio" class="radio-button__input" :id="'radio-' + topic.topicId + '-' + optionIndex" :name="'radio-group-' + topic.topicId" :value="option" :checked="topic.topicResult === option" disabled>
                <label class="radio-button__label" :for="'radio-' + topic.topicId + '-' + optionIndex">
                  <span class="radio-button__custom"></span>
                  {{ option }}
                </label>
              </div>
            </div>
            <div v-if="topic.images && topic.images.length" class="topic-images">
              <img
                v-for="img in topic.images"
                :key="img.id"
                :src="getImageUrl(img.imageUrl)"
                class="topic-image"
                alt="题目图片"
                @click="openImageViewer(getImageUrl(img.imageUrl))"
                style="cursor: pointer;"
              />
            </div>
            <div style="height: 32px;"></div>
          </div>
          <div v-if="topic.type === 'checkbox'">
            {{ index + 1 }}. {{ topic.topic }}
            <div class="checkbox-container">
              <label class="cyberpunk-checkbox-label" v-for="(option, optionIndex) in topic.options" :key="optionIndex">
                <input type="checkbox" class="cyberpunk-checkbox" :id="'checkbox-' + topic.topicId + '-' + optionIndex" :name="'checkbox-group-' + topic.topicId" :value="option" :checked="topic.topicResults && topic.topicResults.includes(option)" disabled>
                {{ option }}
              </label>
            </div>
            <div v-if="topic.images && topic.images.length" class="topic-images">
              <img
                v-for="img in topic.images"
                :key="img.id"
                :src="getImageUrl(img.imageUrl)"
                class="topic-image"
                alt="题目图片"
                @click="openImageViewer(getImageUrl(img.imageUrl))"
                style="cursor: pointer;"
              />
            </div>
            <div style="height: 32px;"></div>
          </div>
        </div>
      </form>
    </div>
    <div v-if="showImageViewer" class="image-viewer-overlay" @click="closeImageViewer">
      <img :src="currentImageUrl" class="image-viewer-img" @click.stop />
    </div>
  </div>
</template>

<script setup lang="ts">
import ContentSkeleton from '@/components/ContentSkeleton.vue'
import { useLatestRequest } from '@/composables/useLatestRequest'
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMsqResult } from '@/api/MsqView.js'
import Navbar from '@/components/Navbar.vue'

interface TopicResult {
  topicId: number
  topic: string
  type: string
  options: string
  topicResult: string | null
  topicResults: string | null
  images?: { id: number; imageUrl: string }[]
  files?: string[]
}

interface Topic {
  id: number
  msqName: string
  status: number
  topicResults: TopicResult[]
}

const topic = ref<Topic>({
  id: 0,
  msqName: '',
  status: 1,
  topicResults: []
})
const loading = ref(false)
const resultRequest = useLatestRequest()
const error = ref<string | null>(null)
const showDialog = ref(true)
const inputId = ref('')
const isConfirming = ref(false)
const isCancelling = ref(false)
const router = useRouter()
const route = useRoute()
const showImageViewer = ref(false)
const currentImageUrl = ref('')

const parsedTopics = computed(() => {
  return topic.value.topicResults.map(item => {
    let options: string[] = []
    try {
      options = JSON.parse(item.options || '[]')
    } catch {
      options = []
    }
    let topicResults: string[] = []
    try {
      topicResults = item.topicResults ? JSON.parse(item.topicResults) : []
    } catch {
      topicResults = []
    }
    let images = Array.isArray(item.images) ? item.images : []
    let files: string[] = []
    if (Array.isArray((item as any).files)) {
      files = (item as any).files
    }
    return {
      ...item,
      options,
      topicResults,
      images,
      files
    }
  })
})

const fetchData = async (id: string) => {
  const request = resultRequest.start()
  loading.value = true
  error.value = null
  try {
    const res: any = await getMsqResult(id, { signal: request.signal })
    if (!request.isCurrent()) return
    if (res.code === 0 && res.data) {
      topic.value = res.data
    } else {
      throw new Error(res.message || '获取数据失败')
    }
  } catch (err: any) {
    if (!request.isCurrent()) return
    error.value = err.message || '获取数据失败，请稍后重试'
  } finally {
    if (request.isCurrent()) loading.value = false
  }
}

const handleConfirm = () => {
  if (!inputId.value) {
    ElMessage.error('请输入ID')
    return
  }
  isConfirming.value = true
  showDialog.value = false
  router.replace({ query: { id: inputId.value } })
  fetchData(inputId.value)
}

const handleCancel = () => {
  if (isConfirming.value || isCancelling.value) return
  isCancelling.value = true
  showDialog.value = false
  router.back()
}

const retryFetch = () => {
  if (route.query.id) {
    fetchData(route.query.id as string)
  } else {
    showDialog.value = true
  }
}

function getImageUrl(url: string) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return url
}

function openImageViewer(url: string) {
  currentImageUrl.value = url
  showImageViewer.value = true
}
function closeImageViewer() {
  showImageViewer.value = false
  currentImageUrl.value = ''
}

function getStampText(status: number) {
  switch (status) {
    case 2:
      return '已通过'
    case 3:
      return '未通过'
    case 4:
      return '已移出'
    case 1:
      return '待审核'
    default:
      return ''
  }
}
function getStampClass(status: number) {
  switch (status) {
    case 2:
      return 'stamp-success'
    case 3:
      return 'stamp-danger'
    case 4:
      return 'stamp-removed'
    case 1:
      return 'stamp-info'
    default:
      return ''
  }
}

// 背景图片自适应逻辑
const msqContainer = ref<HTMLElement | null>(null)
const isMobile = ref(false)

// 新增：弹窗宽度自适应
const dialogWidth = computed(() => isMobile.value ? 'calc(100vw - 32px)' : '30%')

function checkDeviceType() {
  isMobile.value = window.innerWidth <= 768 || /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

onMounted(() => {
  document.title = '结果查询'
  checkDeviceType()
  window.addEventListener('resize', checkDeviceType)
})

onBeforeUnmount(() => window.removeEventListener('resize', checkDeviceType))

if (route.query.id) {
  showDialog.value = false
  fetchData(route.query.id as string)
}

function getFileName(filePath: string) {
  if (!filePath) return ''
  return filePath.split('/').pop() || filePath
}
</script>

<style scoped>
.msq-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: transparent;
  /* 背景图片由js动态设置 */
}
.container {
  width: 100%;
  max-width: 1200px;
  margin: 50px auto;
  padding: 60px 5%;
  background: var(--shell);
  box-shadow: var(--shadow-soft), var(--glass-spec);
  border-radius: var(--radius-lg);
  box-sizing: border-box;
  backdrop-filter: blur(2px) saturate(1.8);
  -webkit-backdrop-filter: blur(2px) saturate(1.8);
  outline: 1px solid var(--shell-border);
}
.msq-form {
  width: 100%;
}
.title h2 {
  text-align: center;
  font-weight: 700;
  font-size: clamp(40px, 8vw, 80px);
  color: var(--ink-900);
  margin: 0;
}
.input-container {
  position: relative;
  width: 100%;
  max-width: 1100px;
}
.input-container input[type="text"] {
  font-size: 20px;
  width: 100%;
  border: none;
  border-bottom: 2px solid var(--ink-700);
  padding: 5px 0;
  background-color: transparent;
  outline: none;
}
.input-container .label {
  position: absolute;
  top: 0;
  left: 0;
  color: var(--ink-900);
  transition: all 0.3s ease;
  pointer-events: none;
}
.input-container input[type="text"]:focus ~ .label,
.input-container input[type="text"]:valid ~ .label {
  top: -20px;
  font-size: 16px;
  color: var(--aqua-500);
}
.input-container .underline {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 2px;
  width: 100%;
  background-color: var(--aqua-500);
  transform: scaleX(0);
  transition: all 0.3s ease;
}
.input-container input[type="text"]:focus ~ .underline,
.input-container input[type="text"]:valid ~ .underline {
  transform: scaleX(1);
}
.input-container input[type="text"]:disabled {
  background-color: transparent;
  cursor: default;
  color: var(--ink-500);
  border-bottom: 2px solid var(--ink-500);
}
.input-container input[type="text"]:disabled ~ .label {
  top: -20px;
  font-size: 16px;
  color: var(--ink-500);
  cursor: default;
}
.input-container input[type="text"]:disabled ~ .underline {
  transform: scaleX(1);
  background-color: var(--ink-500);
}
.radio-button-container {
  display: flex;
  align-items: center;
  margin: 10px 0;
  gap: 24px;
}
.radio-button {
  display: inline-block;
  position: relative;
  cursor: pointer;
}
.radio-button__input {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}
.radio-button__label {
  display: inline-block;
  padding-left: 30px;
  margin-bottom: 10px;
  position: relative;
  font-size: 15px;
  color: var(--ink-900);
  font-weight: 400;
  cursor: pointer;
  text-transform: uppercase;
  transition: all 0.3s ease;
}
.radio-button__custom {
  position: absolute;
  top: 0;
  left: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  border: 2px solid var(--ink-700);
  transition: all 0.3s ease;
}
.radio-button__input:checked + .radio-button__label .radio-button__custom {
  background-color: var(--aqua-500);
  border-color: transparent;
  transform: scale(0.8);
  box-shadow: 0 0 0 3px var(--focus-ring);
}
.radio-button__input:checked + .radio-button__label {
  color: var(--aqua-500);
}
.radio-button__label:hover .radio-button__custom {
  transform: scale(1.2);
  border-color: var(--aqua-500);
  box-shadow: 0 0 0 3px var(--focus-ring);
}
.radio-button__input:disabled + .radio-button__label {
  cursor: default;
  opacity: 0.7;
}
.radio-button__input:disabled + .radio-button__label .radio-button__custom {
  border-color: var(--ink-500);
}
.radio-button__input:disabled + .radio-button__label:hover .radio-button__custom {
  transform: none;
  border-color: var(--ink-500);
  box-shadow: none;
}
.cyberpunk-checkbox {
  appearance: none;
  width: 20px;
  height: 20px;
  border: 2px solid var(--ink-700);
  border-radius: 6px;
  background-color: transparent;
  display: inline-block;
  position: relative;
  margin-right: 10px;
  cursor: pointer;
}
.cyberpunk-checkbox:before {
  content: "";
  background-color: var(--aqua-500);
  display: block;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(0);
  width: 10px;
  height: 10px;
  border-radius: 3px;
  transition: all 0.3s ease-in-out;
}
.cyberpunk-checkbox:checked:before {
  transform: translate(-50%, -50%) scale(1);
}
.cyberpunk-checkbox:checked {
  border-color: var(--aqua-500);
}
.cyberpunk-checkbox:checked + .cyberpunk-checkbox-label {
  color: var(--aqua-500);
}
.cyberpunk-checkbox:hover {
  border-color: var(--aqua-500);
  box-shadow: 0 0 0 3px var(--focus-ring);
}
.cyberpunk-checkbox:disabled {
  cursor: default;
  opacity: 0.7;
  border-color: var(--ink-500);
}
.cyberpunk-checkbox:disabled:hover {
  border-color: var(--ink-500);
  box-shadow: none;
}
.cyberpunk-checkbox-label {
  font-size: 18px;
  color: var(--ink-900);
  cursor: pointer;
  user-select: none;
  display: flex;
  align-items: center;
}
.checkbox-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin: 10px 0;
}
.button2 {
  display: block;
  margin: 0 auto;
  transition: all 0.2s ease-in;
  position: relative;
  overflow: hidden;
  z-index: 1;
  color: var(--ink-900);
  padding: 0.3em 1.8em;
  cursor: pointer;
  font-size: 18px;
  border-radius: var(--radius-pill);
  background: var(--count-bg);
  border: 1px solid var(--hair);
  box-shadow: var(--shadow-soft), var(--glass-spec);
}
.button2:active {
  color: var(--ink-500);
  box-shadow: var(--shadow-soft), var(--glass-spec);
}
.button2:before {
  content: "";
  position: absolute;
  left: 50%;
  transform: translateX(-50%) scaleY(1) scaleX(1.25);
  top: 100%;
  width: 140%;
  height: 180%;
  background-color: var(--accent-glow);
  border-radius: 50%;
  display: block;
  transition: all 0.5s 0.1s cubic-bezier(0.55, 0, 0.1, 1);
  z-index: -1;
}
.button2:after {
  content: "";
  position: absolute;
  left: 55%;
  transform: translateX(-50%) scaleY(1) scaleX(1.45);
  top: 180%;
  width: 160%;
  height: 190%;
  background-color: var(--aqua-500);
  border-radius: 50%;
  display: block;
  transition: all 0.5s 0.1s cubic-bezier(0.55, 0, 0.1, 1);
  z-index: -1;
}
.button2:hover {
  color: #ffffff;
  border: 1px solid var(--aqua-500);
}
.button2:hover:before {
  top: -35%;
  background-color: var(--aqua-500);
  transform: translateX(-50%) scaleY(1.3) scaleX(0.8);
}
.button2:hover:after {
  top: -45%;
  background-color: var(--aqua-500);
  transform: translateX(-50%) scaleY(1.3) scaleX(0.8);
}
@media screen and (max-width: 768px) {
  .radio-button-container {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  .checkbox-container {
    flex-direction: column;
    gap: 4px;
  }
}
.error-message {
  text-align: center;
  padding: 40px 20px;
}
.error-message h3 {
  color: var(--sct-danger);
  margin-bottom: 16px;
}
.error-message p {
  color: var(--ink-500);
  margin-bottom: 24px;
}
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}
.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid var(--aqua-500);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
.loading-container p {
  color: var(--ink-500);
  font-size: 16px;
}
.questionnaire-list {
  padding: 20px;
}
.questionnaire-list h2 {
  text-align: center;
  margin-bottom: 30px;
  color: var(--ink-900);
}
.questionnaire-items {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}
.questionnaire-item {
  background-color: var(--shell);
  padding: 20px;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-soft), var(--glass-spec);
  cursor: pointer;
  transition: all 0.3s ease;
  outline: 1px solid var(--shell-border);
}
.questionnaire-item:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-lift), var(--glass-spec);
}
.questionnaire-item h3 {
  margin: 0 0 10px 0;
  color: var(--ink-900);
}
.questionnaire-item p {
  margin: 0;
  color: var(--ink-500);
  font-size: 14px;
}
.cyberpunk-checkbox-label {
  cursor: default;
}
.review-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 40px;
}
.review-buttons .button2 {
  min-width: 120px;
}
.review-buttons .approve {
  background-color: var(--sct-success);
  color: white;
}
.review-buttons .approve:hover {
  background-color: var(--sct-success);
  border-color: var(--sct-success);
}
.review-buttons .reject {
  background-color: var(--sct-danger);
  color: white;
}
.review-buttons .reject:hover {
  background-color: var(--sct-danger);
  border-color: var(--sct-danger);
}
.review-buttons .button2:hover {
  color: white;
}
.review-buttons .button2:hover:before,
.review-buttons .button2:hover:after {
  background-color: transparent;
}
.topic-images {
  margin: 16px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.topic-image {
  max-width: 300px;
  max-height: 200px;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-soft), var(--glass-spec);
  object-fit: contain;
}
.image-viewer-overlay {
  position: fixed;
  z-index: 9999;
  left: 0; top: 0; right: 0; bottom: 0;
  background: var(--overlay-bg);
  display: flex;
  align-items: center;
  justify-content: center;
}
.image-viewer-img {
  max-width: 90vw;
  max-height: 90vh;
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-soft), var(--glass-spec);
  background: var(--shell-strong);
}
.stamp {
  position: absolute;
  top: 0;
  right: 0;
  padding: 12px 28px;
  font-size: 2rem;
  font-weight: bold;
  color: #fff;
  border-radius: var(--radius-pill);
  transform: rotate(12deg);
  box-shadow: var(--shadow-soft), var(--glass-spec);
  opacity: 0.92;
  z-index: 10;
  letter-spacing: 0;
  user-select: none;
}
.stamp-success {
  background: var(--sct-success);
}
.stamp-danger {
  background: var(--sct-danger);
}
.stamp-removed {
  background: var(--ink-500);
}
.stamp-info {
  background: var(--aqua-500);
}
@media (max-width: 768px) {
  :global(.result-query-dialog) {
    padding: 20px;
    max-width: 440px;
  }
  :global(.result-query-dialog .el-dialog__header) {
    padding-bottom: 20px;
    padding-right: 32px;
  }
  :global(.result-query-dialog .el-dialog__body) {
    padding: 0;
  }
  :global(.result-query-dialog .el-dialog__footer) {
    padding-top: 20px;
  }
  :global(.result-query-dialog .el-input__wrapper) {
    min-height: 44px;
    box-sizing: border-box;
    padding: 0 14px;
  }
  :global(.result-query-dialog .el-input__inner) {
    font-size: 16px;
  }
  .query-dialog-actions {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }
  .query-dialog-actions .el-button {
    width: 100%;
    min-width: 0;
    height: 44px;
    margin: 0;
    padding: 0 12px;
    font-size: 16px;
    border-radius: var(--radius-md);
  }
}
</style>
<style scoped src="../styles/msq-controls.css"></style>