<template>
  <div class="msq-container" ref="msqContainer">
    <!-- 导航栏 -->
    <Navbar />

    <div class="container">
      <ContentSkeleton v-if="loading" variant="form" />
      <div v-else-if="error" class="error-message">
        <h3>加载失败</h3>
        <p>{{ error }}</p>
        <button class="button2" @click="retryFetch">重试</button>
      </div>
      <div v-else-if="showQuestionnaireList" class="questionnaire-list">
        <h2>请选择问卷</h2>
        <div class="questionnaire-items">
          <div v-for="item in questionnaireList" :key="item.id" class="questionnaire-item" @click="selectQuestionnaire(item)">
            <h3>{{ item.name }}</h3>
            <p>{{ item.description }}</p>
          </div>
        </div>
      </div>
      <form v-else class="msq-form">
        <div class="title">
          <h2>{{ topic.name }}</h2>
          <p class="questionnaire-description">
            {{ topic.description }}
          </p>
        </div>

        <div class="msq-topic" v-for="(topic, index) in topic.topics" :key="topic.id">

          <div v-if="topic.type === 'input'">
            <div class="input-container">
              <label class="label" :for="'input-' + topic.id">{{ index + 1 }}. {{ topic.topic }}</label>
              <input required :id="'input-' + topic.id" type="text" v-model="submitData[topic.id]" autocomplete="off" @blur="handleInputBlur($event, topic.id)" />
              <div class="underline"></div>
            </div>
            <div v-if="topic.id === -2" class="player-info">
              <template v-if="playerInfo">
                <span class="player-tip">已查询到正版账号信息：</span>
                <LoadingImage :src="playerInfo.avatar" alt="玩家头像" class="player-avatar" />
                <span class="player-username">{{ playerInfo.username }}</span>
                <span class="player-uuid">{{ playerInfo.id }}</span>
              </template>
              <template v-else-if="errorMessage">
                <span class="player-error">{{ errorMessage }}</span>
              </template>
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
            <el-upload
              class="upload-demo"
              action="/api/upload/file"
              :limit="3"
              :on-success="(res, file, fileList) => handleFileSuccess(res, file, fileList, topic.id)"
              :on-remove="(file, fileList) => handleFileRemove(file, fileList, topic.id)"
              :file-list="fileUploadList[topic.id]"
              :before-upload="(file) => beforeFileUpload(file)"
              :on-exceed="() => ElMessage.warning('最多上传3个文件')"
              :accept="acceptFileTypes"
              multiple>
              <el-button type="primary">点击上传</el-button>
              <template #tip>
                <div class="el-upload__tip">支持bmp, gif, jpg, jpeg, png, rar, zip, gz, bz2, litematic, schematic格式，最多3个文件，单文件不超过20MB</div>
              </template>
            </el-upload>
          </div>

          <div v-if="topic.type === 'radio'" role="group" :aria-labelledby="'question-' + topic.id">
            <p class="question-label" :id="'question-' + topic.id">{{ index + 1 }}. {{ topic.topic }}</p>
            <div class="radio-button-container">
              <div class="radio-button" v-for="(option, index) in topic.options" :key="index">
                <input type="radio" class="radio-button__input" :id="'radio-' + topic.id + '-' + index" :name="'radio-group-' + topic.id" :value="option" v-model="submitData[topic.id]">
                <label class="radio-button__label" :for="'radio-' + topic.id + '-' + index">
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
          </div>

          <div v-if="topic.type === 'checkbox'" role="group" :aria-labelledby="'question-' + topic.id">
            <p class="question-label" :id="'question-' + topic.id">{{ index + 1 }}. {{ topic.topic }}</p>
            <div class="checkbox-container">
              <label class="cyberpunk-checkbox-label" v-for="(option, index) in topic.options" :key="index">
                <input type="checkbox" class="cyberpunk-checkbox" :id="'checkbox-' + topic.id + '-' + index" :name="'checkbox-group-' + topic.id" :value="option" v-model="submitData[topic.id]" @change="handleCheckboxChange($event, topic.id)">
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
          </div>

        </div>

        <div class="form-actions">
          <button class="button2" :disabled="submitting || submitted" @click="submit">{{ submitting ? '提交中...' : '提交' }}</button>
        </div>
      </form>
    </div>
  </div>
  <div v-if="showImageViewer" class="image-viewer-overlay" @click="closeImageViewer">
    <img :src="currentImageUrl" class="image-viewer-img" @click.stop />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import Navbar from '@/components/Navbar.vue'
import ContentSkeleton from '@/components/ContentSkeleton.vue'
import LoadingImage from '@/components/LoadingImage.vue'
import { useLatestRequest } from '@/composables/useLatestRequest'

interface TopicOption {
  id: number
  type: 'input' | 'radio' | 'checkbox' | 'file'
  topic: string
  options?: string[]
  images?: { id: number; imageUrl: string }[]
}

interface Topic {
  id: number
  name: string
  description: string
  type: number
  topics: TopicOption[]
}

interface Questionnaire {
  id: number
  name: string
  description: string
  type: number
  status: number
  createTime: string
  updateTime: string
  remark: string
  deleted: boolean
}

interface SubmitData {
  [key: number]: string | string[]
}

interface FormattedData {
  id: number
  topic: string
  value?: string
  values?: string[]
}

interface TopicResult {
  topicId: number
  topic: string
  topicResult?: string
  topicResults?: string[]
  avatar?: string
  uuid?: string
}

interface SubmitRequest {
  msqId: number
  name: string
  type: number
  topicResults: TopicResult[]
  avatar?: string
  uuid?: string
}

interface ApiResponse<T> {
  code: number
  message: string | null
  data: T
}

const topic = ref<Topic>({
  id: 0,
  name: '',
  description: '',
  type: 0,
  topics: [
    {
      id: 0,
      type: 'input',
      topic: '',
      options: []
    }
  ]
})

const submitData = ref<SubmitData>({})
const questionnaireList = ref<Questionnaire[]>([])
const showQuestionnaireList = ref(false)

import { getMsqVO, getOneMsqVO, submitMsq, getPlayer } from '@/api/MsqView.js'

const error = ref<string | null>(null)
const loading = ref(false)
const questionnaireRequest = useLatestRequest()
const submitting = ref(false)
const submitted = ref(false)
const router = useRouter()
const route = useRoute()

const playerInfo = ref<{
  avatar: string;
  username: string;
  id: string;
} | null>(null);

const errorMessage = ref<string | null>(null);

const showImageViewer = ref(false)
const currentImageUrl = ref('')

const msqContainer = ref<HTMLElement | null>(null)

const fileUploadList = ref<{ [key: number]: any[] }>({})
const acceptFileTypes = '.bmp,.gif,.jpg,.jpeg,.png,.rar,.zip,.gz,.bz2,.litematic,.schematic'

function getImageUrl(url: string) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return url
}

// 使用异步函数获取数据
const fetchData = async () => {
  const request = questionnaireRequest.start()
  loading.value = true
  error.value = null
  try {
    // 从路由查询参数中获取问卷类型
    const questionnaireTypeParam = (route.query.type as string) || 'redstone'
    
    // 将英文参数转换为对应的数字
    const typeMapping: { [key: string]: number } = {
      'redstone': 1,
      'architectural': 2,
      'logistics': 3,
      'other': 4
    }
    
    const questionnaireType = typeMapping[questionnaireTypeParam] || 1
    
    const response = await getMsqVO(questionnaireType, { signal: request.signal });
    if (!request.isCurrent()) return
    if (response && response.data) {
      // 判断响应数据类型
      if (Array.isArray(response.data)) {
        // 第二种响应数据：问卷列表
        questionnaireList.value = response.data
        showQuestionnaireList.value = true
      } else {
        // 第一种响应数据：问卷内容
        topic.value = response.data
        showQuestionnaireList.value = false
        // 在数据加载完成后初始化多选框数据
        if (!Array.isArray(response.data.topics)) return
        response.data.topics.forEach(item => {
          if (item.type === 'checkbox') {
            submitData.value[item.id] = []
          }
          if (item.type === 'file') {
            fileUploadList.value[item.id] = []
            submitData.value[item.id] = []
          }
        })
      }
    } else {
      throw new Error('获取数据失败：返回数据格式不正确')
    }
  } catch (err) {
    if (!request.isCurrent()) return
    console.error('获取数据失败：', err)
    error.value = err.message || '获取数据失败，请稍后重试'
  } finally {
    if (request.isCurrent()) loading.value = false
  }
}

const selectQuestionnaire = async (questionnaire: Questionnaire) => {
  const request = questionnaireRequest.start()
  loading.value = true
  try {
    const response = await getOneMsqVO(questionnaire.id, { signal: request.signal })
    if (!request.isCurrent()) return
    if (response && response.data) {
      topic.value = response.data
      showQuestionnaireList.value = false
      // 在数据加载完成后初始化多选框数据
      if (!Array.isArray(response.data.topics)) return
      response.data.topics.forEach(item => {
        if (item.type === 'checkbox') {
          submitData.value[item.id] = []
        }
        if (item.type === 'file') {
          fileUploadList.value[item.id] = []
          submitData.value[item.id] = []
        }
      })
    } else {
      throw new Error('获取问卷内容失败：返回数据格式不正确')
    }
  } catch (err) {
    if (!request.isCurrent()) return
    console.error('获取问卷内容失败：', err)
    error.value = err.message || '获取问卷内容失败，请稍后重试'
  } finally {
    if (request.isCurrent()) loading.value = false
  }
}

const retryFetch = () => {
  fetchData()
}

// 在组件挂载时获取数据
onMounted(() => {
  document.title = '问卷填写'
  fetchData();
})

const submit = async (e: Event) => {
  e.preventDefault()
  if (submitting.value || submitted.value) return
  submitting.value = true
  try {
    await submitQuestionnaire()
  } catch {
  } finally {
    submitting.value = false
  }
}

const submitQuestionnaire = async () => {
  
  // 检查ID为负数的题目是否已填写
  const negativeIdTopics = topic.value.topics.filter(item => item.id < 0)
  for (const item of negativeIdTopics) {
    if (item.type === 'checkbox') {
      if (!submitData.value[item.id] || (submitData.value[item.id] as string[]).length === 0) {
        ElMessage.error(`请填写"${item.topic}"`)
        return
      }
    } else {
      if (!submitData.value[item.id]) {
        ElMessage.error(`请填写"${item.topic}"`)
        return
      }
    }
  }

  // 检查正版账号验证状态
  const genuineIdTopic = topic.value.topics.find(item => item.id === -2)
  if (genuineIdTopic && submitData.value[-2]) {
    const genuineId = String(submitData.value[-2]).trim()
    try {
      const data = await getPlayer(genuineId)
      if (genuineId !== String(submitData.value[-2]).trim()) {
        ElMessage.error('账号ID已修改，请重新提交')
        return
      }
      
      if (data?.uuid) {
        playerInfo.value = {
          avatar: `https://mc-heads.net/avatar/${encodeURIComponent(genuineId)}`,
          username: data.username,
          id: data.uuid
        };
        errorMessage.value = null;
      } else {
        playerInfo.value = null;
        errorMessage.value = '未查询到正版账号信息';
        ElMessage.error('请确保输入的是有效的正版账号ID')
        return
      }
    } catch (error) {
      console.error('验证玩家ID时出错：', error);
      playerInfo.value = null;
      errorMessage.value = '验证玩家ID时出错，请稍后重试';
      ElMessage.error('验证玩家ID时出错，请稍后重试')
      return
    }
  }
  
  const topicResults: TopicResult[] = topic.value.topics.map(item => {
    const result: any = {
      topicId: item.id,
    }
    if (item.type === 'checkbox') {
      result.topicResults = submitData.value[item.id] as string[] || []
      result.topicResult = ''
      result.files = []
    } else if (item.type === 'file') {
      const files = submitData.value[item.id]
      result.files = Array.isArray(files) ? files : []
      result.topicResult = ''
      result.topicResults = []
    } else {
      result.topicResult = submitData.value[item.id] as string || ''
      result.topicResults = []
      result.files = []
    }
    return result
  })
  
  const submitRequest: SubmitRequest = {
    msqId: topic.value.id,
    name: topic.value.name,
    type: topic.value.type,
    topicResults: topicResults
  }

  // 如果有玩家信息，添加到提交数据中
  if (playerInfo.value) {
    submitRequest.avatar = playerInfo.value.avatar
    submitRequest.uuid = playerInfo.value.id
  }
  
  const response = await submitMsq(submitRequest)
  submitted.value = true
  ElMessage.success('提交成功')
  // 延迟1秒后跳转，让用户看到成功提示
  setTimeout(() => {
    router.push('/msq/success')  // 跳转到问卷列表页
  }, 1000)
}

const handleCheckboxChange = (event, topicId) => {
  if (!Array.isArray(submitData.value[topicId])) {
    submitData.value[topicId] = []
  }
}

const handleInputBlur = async (event: Event, topicId: number) => {
  // 只处理第一个输入框（通常是正版ID输入框）
  if (topicId === -2) {
    const input = event.target as HTMLInputElement;
    const genuineId = input.value.trim();
    
    if (genuineId) {
      try {
        const data = await getPlayer(genuineId)
        if (genuineId !== String(submitData.value[-2] || '').trim()) return
        
        if (data?.uuid) {
          playerInfo.value = {
            avatar: `https://mc-heads.net/avatar/${encodeURIComponent(genuineId)}`,
            username: data.username,
            id: data.uuid
          };
          errorMessage.value = null;
        } else {
          playerInfo.value = null;
          errorMessage.value = '未查询到正版账号信息';
        }
      } catch (error) {
        if (genuineId !== String(submitData.value[-2] || '').trim()) return
        console.error('验证玩家ID时出错：', error);
        playerInfo.value = null;
        errorMessage.value = '验证玩家ID时出错，请稍后重试';
      }
    } else {
      playerInfo.value = null;
      errorMessage.value = null;
    }
  }
}

function openImageViewer(url: string) {
  currentImageUrl.value = url
  showImageViewer.value = true
}

function closeImageViewer() {
  showImageViewer.value = false
  currentImageUrl.value = ''
}

function beforeFileUpload(file: File) {
  const isAllowed = [
    'image/bmp', 'image/gif', 'image/jpeg', 'image/png',
    'application/x-rar-compressed', 'application/zip', 'application/gzip', 'application/x-bzip2',
    '', // litematic, schematic等特殊格式可能无MIME
  ]
  const extAllowed = [
    'bmp', 'gif', 'jpg', 'jpeg', 'png', 'rar', 'zip', 'gz', 'bz2', '7z', 'litematic', 'schematic'
  ]
  const ext = file.name.split('.').pop()?.toLowerCase()
  const isExtAllowed = ext && extAllowed.includes(ext)
  const isLt20M = file.size / 1024 / 1024 < 20
  if (!isExtAllowed) {
    ElMessage.error('不支持的文件格式')
    return false
  }
  if (!isLt20M) {
    ElMessage.error('单文件不能超过20MB')
    return false
  }
  return true
}

function handleFileSuccess(res, file, fileList, topicId) {
  if (!fileUploadList.value[topicId]) fileUploadList.value[topicId] = []
  fileUploadList.value[topicId] = fileList
  submitData.value[topicId] = fileList
    .map(f => f.response?.data || f.url)
    .filter(url => !!url)
}

function handleFileRemove(file, fileList, topicId) {
  fileUploadList.value[topicId] = fileList
  submitData.value[topicId] = fileList
    .map(f => f.response?.data || f.url)
    .filter(url => !!url)
}
</script>

<style scoped>
.msq-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  /* 背景图片由js动态设置 */
}

/* 全局文字阴影 */
* {
  text-shadow: none;
}

.container {
  width: calc(100% - 48px);
  max-width: 880px;
  margin: 84px auto 0;
  padding: 32px 40px;
  background-color: var(--shell);
  backdrop-filter: blur(2px) saturate(1.8);
  -webkit-backdrop-filter: blur(2px) saturate(1.8);
  box-shadow: var(--shadow-soft), var(--glass-spec);
  border-radius: var(--radius-lg);
  box-sizing: border-box;
  margin-bottom: 40px;
  border: 1px solid var(--shell-border);
}

.msq-form {
  width: 100%;
  overflow-wrap: anywhere;
}
.title {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--hair);
}
.questionnaire-description {
  margin: 12px 0 0;
  color: var(--ink-700);
  line-height: 1.75;
  white-space: pre-line;
}
.msq-topic + .msq-topic {
  margin-top: 28px;
}
.question-label, .input-container label {
  display: block;
  margin: 0 0 12px;
  color: var(--ink-900);
  font-size: 16px;
  font-weight: 600;
  line-height: 1.65;
}
.form-actions {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid var(--hair);
}
.form-actions .button2 {
  min-width: 160px;
  min-height: 44px;
  border-radius: var(--radius-md);
}

.title h2 {
  text-align: left;
  font-weight: 700;
  font-size: 28px;
  line-height: 1.4;
  color: var(--ink-900);
  margin: 0;
}

/* From Uiverse.io by opMorn */ 
.input-container {
  position: relative;
  width: 100%;
  max-width: 1100px;
}

.input-container input[type="text"] {
  font-size: 16px;
  min-height: 44px;
  box-sizing: border-box;
  width: 100%;
  border: none;
  border-bottom: 2px solid var(--ink-700);
  padding: 5px 0;
  background-color: transparent;
  outline: none;
}

.input-container .label {
  position: static;
  color: var(--ink-900);
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

/* From Uiverse.io by gharsh11032000 */ 
.radio-button-container {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  margin: 0;
  gap: 8px 24px;
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
  display: block;
  padding-left: 30px;
  padding-top: 10px;
  padding-bottom: 10px;
  min-height: 44px;
  box-sizing: border-box;
  margin-bottom: 0;
  line-height: 1.5;
  position: relative;
  font-size: 15px;
  color: var(--ink-900);
  font-weight: 400;
  cursor: pointer;
  text-transform: none;
  transition: all 0.3s ease;
}

.radio-button__custom {
  position: absolute;
  top: 12px;
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

/* From Uiverse.io by adamgiebl */ 
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

.cyberpunk-checkbox-label {
  font-size: 15px;
  line-height: 1.5;
  min-height: 44px;
  color: var(--ink-900);
  cursor: pointer;
  user-select: none;
  display: flex;
  align-items: center;
}

.checkbox-container {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 24px;
  margin: 0;
}

/* From Uiverse.io by shah1345 */ 
.button2 {
  /* width: 300px;
  height: 100px; */
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
  /* box-shadow: 6px 6px 12px #c5c5c5, -6px -6px 12px #ffffff; */
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
  .container {
    width: calc(100% - 24px);
    margin-top: 76px;
    padding: 24px 18px;
  }
  .title h2 { font-size: 24px; }
  .radio-button-container, .checkbox-container {
    grid-template-columns: minmax(0, 1fr);
    gap: 4px;
  }
  .form-actions .button2 { width: 100%; }
  .questionnaire-list { padding: 0; }
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
  grid-template-columns: repeat(auto-fit, minmax(min(100%, 280px), 1fr));
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

.player-info {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
  font-size: 14px;
}

.player-tip {
  color: var(--ink-500);
}

.player-avatar {
  width: 24px;
  height: 24px;
  border-radius: var(--radius-md);
  vertical-align: middle;
}

.player-username {
  color: var(--ink-900);
  font-weight: 500;
}

.player-uuid {
  color: var(--ink-500);
  font-family: monospace;
}

.player-error {
  color: var(--sct-danger);
  font-size: 14px;
}

@media screen and (max-width: 768px) {
  .player-tip {
    display: none;
  }
  
  .player-info {
    flex-wrap: wrap;
  }
  
  .player-uuid {
    width: 100%;
    margin-top: 4px;
  }
}

.topic-images {
  margin: 16px 0;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.topic-image {
  max-width: min(100%, 300px);
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
</style>
<style scoped src="../styles/msq-controls.css"></style>