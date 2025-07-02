<template>
  <div class="msq-container" ref="msqContainer">
    <Navbar />
    
    <el-dialog v-model="showDialog" title="请输入你的ID" :width="dialogWidth" :close-on-click-modal="false" :show-close="false" :class="{'mobile-dialog': isMobile}">
        <el-input v-model="inputId" placeholder="请输入你的ID" @keyup.enter.native="handleConfirm" spellcheck="false" autocapitalize="off" />
        <template #footer>
          <el-button @click="handleConfirm" type="primary">确定</el-button>
        </template>
      </el-dialog>
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>
    <div v-else-if="error" class="error-message">
      <h3>加载失败</h3>
      <p>{{ error }}</p>
      <button class="button2" @click="retryFetch">重试</button>
    </div>
    <div class="container" v-if="topic.topicResults && topic.topicResults.length">
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
                <a :href="getImageUrl(file)" target="_blank" style="color: #4c8bf5; text-decoration: underline;">
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
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMsqResult } from '@/api/MsqView.js'
import Navbar from '@/components/Navbar.vue'
import { getConfig } from '@/api/System.js'

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
const error = ref<string | null>(null)
const showDialog = ref(true)
const inputId = ref('')
const router = useRouter()
const route = useRoute()
const showImageViewer = ref(false)
const currentImageUrl = ref('')
const configMap = ref({})

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
  loading.value = true
  error.value = null
  try {
    const res: any = await getMsqResult(id)
    if (res.code === 0 && res.data) {
      topic.value = res.data
    } else {
      throw new Error(res.message || '获取数据失败')
    }
  } catch (err: any) {
    error.value = err.message || '获取数据失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const handleConfirm = () => {
  if (!inputId.value) {
    ElMessage.error('请输入ID')
    return
  }
  showDialog.value = false
  router.replace({ query: { id: inputId.value } })
  fetchData(inputId.value)
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
const backgroundImageUrl = ref('')
const isMobile = ref(false)

// 新增：弹窗宽度自适应
const dialogWidth = computed(() => isMobile.value ? '95vw' : '30%')

function checkDeviceType() {
  isMobile.value = window.innerWidth <= 768 || /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

function setBackgroundImage() {
  if (msqContainer.value) {
    msqContainer.value.style.backgroundImage = `url('${backgroundImageUrl.value}')`
    msqContainer.value.style.backgroundSize = 'cover'
    msqContainer.value.style.backgroundPosition = 'center'
    msqContainer.value.style.backgroundRepeat = 'no-repeat'
    msqContainer.value.style.backgroundAttachment = 'fixed'
  }
}

function getBackgroundImage() {
  if (isMobile.value) {
    backgroundImageUrl.value = configMap.value['phone_msq_background'] || ''
  } else {
    backgroundImageUrl.value = configMap.value['msq_background'] || ''
  }
  setBackgroundImage()
}

onMounted(async () => {
  document.title = '结果查询'
  checkDeviceType()
  // 获取配置
  try {
    const res = await getConfig()
    if (res && res.data) {
      const map = {}
      res.data.forEach(item => {
        map[item.configKey] = item.configValue
      })
      configMap.value = map
    }
  } catch (e) {
    // 可选：错误处理
  }
  getBackgroundImage()
  window.addEventListener('resize', () => {
    const wasMobile = isMobile.value
    checkDeviceType()
    if (wasMobile !== isMobile.value) {
      getBackgroundImage()
    }
  })
})

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
  background-color: #fff;
  /* 背景图片由js动态设置 */
}
.container {
  width: 100%;
  max-width: 1200px;
  margin: 50px auto;
  padding: 60px 5%;
  background: rgba(255, 255, 255, 0.35);
  box-shadow: 2px 2px 8px rgba(0, 0, 0, .3);
  border-radius: 5px;
  box-sizing: border-box;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}
.msq-form {
  width: 100%;
}
.title h2 {
  text-align: center;
  font-weight: 700;
  font-size: clamp(40px, 8vw, 80px);
  color: black;
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
  border-bottom: 2px solid #555;
  padding: 5px 0;
  background-color: transparent;
  outline: none;
}
.input-container .label {
  position: absolute;
  top: 0;
  left: 0;
  color: black;
  transition: all 0.3s ease;
  pointer-events: none;
}
.input-container input[type="text"]:focus ~ .label,
.input-container input[type="text"]:valid ~ .label {
  top: -20px;
  font-size: 16px;
  color: #4c8bf5;
}
.input-container .underline {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 2px;
  width: 100%;
  background-color: #4c8bf5;
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
  color: #666;
  border-bottom: 2px solid #999;
}
.input-container input[type="text"]:disabled ~ .label {
  top: -20px;
  font-size: 16px;
  color: #666;
  cursor: default;
}
.input-container input[type="text"]:disabled ~ .underline {
  transform: scaleX(1);
  background-color: #999;
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
  color: #000;
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
  border: 2px solid #555;
  transition: all 0.3s ease;
}
.radio-button__input:checked + .radio-button__label .radio-button__custom {
  background-color: #4c8bf5;
  border-color: transparent;
  transform: scale(0.8);
  box-shadow: 0 0 20px #4c8bf580;
}
.radio-button__input:checked + .radio-button__label {
  color: #4c8bf5;
}
.radio-button__label:hover .radio-button__custom {
  transform: scale(1.2);
  border-color: #4c8bf5;
  box-shadow: 0 0 20px #4c8bf580;
}
.radio-button__input:disabled + .radio-button__label {
  cursor: default;
  opacity: 0.7;
}
.radio-button__input:disabled + .radio-button__label .radio-button__custom {
  border-color: #999;
}
.radio-button__input:disabled + .radio-button__label:hover .radio-button__custom {
  transform: none;
  border-color: #999;
  box-shadow: none;
}
.cyberpunk-checkbox {
  appearance: none;
  width: 20px;
  height: 20px;
  border: 2px solid #555;
  border-radius: 5px;
  background-color: transparent;
  display: inline-block;
  position: relative;
  margin-right: 10px;
  cursor: pointer;
}
.cyberpunk-checkbox:before {
  content: "";
  background-color: #4c8bf5;
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
  border-color: #4c8bf5;
}
.cyberpunk-checkbox:checked + .cyberpunk-checkbox-label {
  color: #4c8bf5;
}
.cyberpunk-checkbox:hover {
  border-color: #4c8bf5;
  box-shadow: 0 0 20px #4c8bf580;
}
.cyberpunk-checkbox:disabled {
  cursor: default;
  opacity: 0.7;
  border-color: #999;
}
.cyberpunk-checkbox:disabled:hover {
  border-color: #999;
  box-shadow: none;
}
.cyberpunk-checkbox-label {
  font-size: 18px;
  color: #000;
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
  color: #090909;
  padding: 0.3em 1.8em;
  cursor: pointer;
  font-size: 18px;
  border-radius: 0.5em;
  background: #e8e8e8;
  border: 1px solid #eee;
  box-shadow: 3px 3px 3px rgba(0, 0, 0, .2);
}
.button2:active {
  color: #666;
  box-shadow: inset 4px 4px 12px #c5c5c5, inset -4px -4px 12px #ffffff;
}
.button2:before {
  content: "";
  position: absolute;
  left: 50%;
  transform: translateX(-50%) scaleY(1) scaleX(1.25);
  top: 100%;
  width: 140%;
  height: 180%;
  background-color: rgba(76, 139, 245, 0.1);
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
  background-color: #4c8bf5;
  border-radius: 50%;
  display: block;
  transition: all 0.5s 0.1s cubic-bezier(0.55, 0, 0.1, 1);
  z-index: -1;
}
.button2:hover {
  color: #ffffff;
  border: 1px solid #4c8bf5;
}
.button2:hover:before {
  top: -35%;
  background-color: #4c8bf5;
  transform: translateX(-50%) scaleY(1.3) scaleX(0.8);
}
.button2:hover:after {
  top: -45%;
  background-color: #4c8bf5;
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
  color: #ff4d4f;
  margin-bottom: 16px;
}
.error-message p {
  color: #666;
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
  border-top: 5px solid #4c8bf5;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
.loading-container p {
  color: #666;
  font-size: 16px;
}
.questionnaire-list {
  padding: 20px;
}
.questionnaire-list h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
.questionnaire-items {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}
.questionnaire-item {
  background-color: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}
.questionnaire-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}
.questionnaire-item h3 {
  margin: 0 0 10px 0;
  color: #333;
}
.questionnaire-item p {
  margin: 0;
  color: #666;
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
  background-color: #67c23a;
  color: white;
}
.review-buttons .approve:hover {
  background-color: #85ce61;
  border-color: #85ce61;
}
.review-buttons .reject {
  background-color: #f56c6c;
  color: white;
}
.review-buttons .reject:hover {
  background-color: #f78989;
  border-color: #f78989;
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
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  object-fit: contain;
}
.image-viewer-overlay {
  position: fixed;
  z-index: 9999;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  align-items: center;
  justify-content: center;
}
.image-viewer-img {
  max-width: 90vw;
  max-height: 90vh;
  border-radius: 8px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.4);
  background: #fff;
}
.stamp {
  position: absolute;
  top: 0;
  right: 0;
  padding: 12px 28px;
  font-size: 2rem;
  font-weight: bold;
  color: #fff;
  border-radius: 8px;
  transform: rotate(12deg);
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  opacity: 0.92;
  z-index: 10;
  letter-spacing: 6px;
  user-select: none;
}
.stamp-success {
  background: #67c23a;
}
.stamp-danger {
  background: #f56c6c;
}
.stamp-removed {
  background: #909399;
}
.stamp-info {
  background: #409eff;
}
.mobile-dialog .el-dialog {
  width: 90vw !important;
  max-width: 95vw;
  min-width: unset;
  padding: 0 8px;
}
.mobile-dialog .el-dialog__body {
  padding: 16px 8px;
}
.mobile-dialog .el-input__inner {
  font-size: 18px;
}
.mobile-dialog .el-button {
  font-size: 18px;
  padding: 8px 0;
  width: 100%;
}
@media (max-width: 768px) {
  .el-dialog {
    width: 90vw !important;
    max-width: 95vw;
    min-width: unset;
    padding: 0 8px;
  }
  .el-dialog__body {
    padding: 16px 8px;
  }
}
</style> 