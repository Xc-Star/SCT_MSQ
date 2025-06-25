<template>
  <div class="msq-container">
    <div class="container">
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
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
        <div class="title" style="margin-bottom: 64px;">
          <h2 style="text-align: center; font-weight: 700; font-size: clamp(40px, 8vw, 80px); color: black;">{{ topic.name }}</h2>
          <span style="text-align: center; color: #666; display: block;">
            {{ topic.description }}
          </span>
        </div>

        <div class="msq-topic" v-for="(topic, index) in topic.topics" :key="topic.id">

          <div v-if="topic.type === 'input'">
            <div class="input-container">
              <input required id="input" type="text" v-model="submitData[topic.id]" autocomplete="off" @blur="handleInputBlur($event, topic.id)" />
              <label class="label" for="input">{{ index + 1 }}. {{ topic.topic }}</label>
              <div class="underline"></div>
            </div>
            <div v-if="topic.id === -2" class="player-info">
              <template v-if="playerInfo">
                <span class="player-tip">已查询到正版账号信息：</span>
                <img :src="playerInfo.avatar" alt="玩家头像" class="player-avatar" />
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
            <div style="height: 50px;"></div>
          </div>

          <div v-if="topic.type === 'radio'">
            {{ index + 1 }}. {{ topic.topic }}
            <div class="radio-button-container">
              <div class="radio-button" v-for="(option, index) in topic.options">
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
            <div style="height: 32px;"></div>
          </div>

          <div v-if="topic.type === 'checkbox'">
            {{ index + 1 }}. {{ topic.topic }}
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
            <div style="height: 32px;"></div>
          </div>

        </div>

        <button class="button2" @click="submit">提交</button>
      </form>
    </div>
  </div>
  <div v-if="showImageViewer" class="image-viewer-overlay" @click="closeImageViewer">
    <img :src="currentImageUrl" class="image-viewer-img" @click.stop />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

interface TopicOption {
  id: number
  type: 'input' | 'radio' | 'checkbox'
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

import { getMsqVO, getOneMsqVO, submitMsq } from '@/api/MsqView.js'

const error = ref<string | null>(null)
const loading = ref(false)
const router = useRouter()

const playerInfo = ref<{
  avatar: string;
  username: string;
  id: string;
} | null>(null);

const errorMessage = ref<string | null>(null);

const showImageViewer = ref(false)
const currentImageUrl = ref('')

function getImageUrl(url: string) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return url
}

// 使用异步函数获取数据
const fetchData = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await getMsqVO(1);
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
        topic.value.topics.forEach(item => {
          if (item.type === 'checkbox') {
            submitData.value[item.id] = []
          }
        })
      }
    } else {
      throw new Error('获取数据失败：返回数据格式不正确')
    }
  } catch (err) {
    console.error('获取数据失败：', err)
    error.value = err.message || '获取数据失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const selectQuestionnaire = async (questionnaire: Questionnaire) => {
  loading.value = true
  try {
    const response = await getOneMsqVO(questionnaire.id)
    if (response && response.data) {
      topic.value = response.data
      showQuestionnaireList.value = false
      // 在数据加载完成后初始化多选框数据
      topic.value.topics.forEach(item => {
        if (item.type === 'checkbox') {
          submitData.value[item.id] = []
        }
      })
    } else {
      throw new Error('获取问卷内容失败：返回数据格式不正确')
    }
  } catch (err) {
    console.error('获取问卷内容失败：', err)
    error.value = err.message || '获取问卷内容失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const retryFetch = () => {
  fetchData()
}

// 在组件挂载时获取数据
onMounted(() => {
  fetchData();
})

const submit = async (e: Event) => {
  e.preventDefault()
  
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
    try {
      const response = await fetch(`https://api.ashcon.app/mojang/v2/user/${submitData.value[-2]}`);
      const data = await response.json();
      
      if (data.uuid) {
        playerInfo.value = {
          avatar: `https://mc-heads.net/avatar/${submitData.value[-2]}`,
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
    const result: TopicResult = {
      topicId: item.id,
      topic: item.topic
    }
    
    if (item.type === 'checkbox') {
      result.topicResults = submitData.value[item.id] as string[] || []
    } else {
      result.topicResult = submitData.value[item.id] as string || ''
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
        const response = await fetch(`https://api.ashcon.app/mojang/v2/user/${genuineId}`);
        const data = await response.json();
        
        if (data.uuid) {
          playerInfo.value = {
            avatar: `https://mc-heads.net/avatar/${genuineId}`,
            username: data.username,
            id: data.uuid
          };
          errorMessage.value = null;
        } else {
          playerInfo.value = null;
          errorMessage.value = '未查询到正版账号信息';
        }
      } catch (error) {
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
</script>

<style scoped>
.msq-container {
  display: flex;
  flex-direction: column;
  /* align-items: center; */
  /* justify-content: center; */
  min-height: 100vh;
  background-color: skyblue;
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 50px auto;
  padding: 60px 5%;
  background-color: rgba(255, 255, 255, .3);
  box-shadow: 2px 2px 8px rgba(0, 0, 0, .3);
  border-radius: 5px;
  box-sizing: border-box;
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

/* From Uiverse.io by opMorn */ 
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

/* From Uiverse.io by gharsh11032000 */ 
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

/* From Uiverse.io by adamgiebl */ 
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
  color: #090909;
  padding: 0.3em 1.8em;
  cursor: pointer;
  font-size: 18px;
  border-radius: 0.5em;
  background: #e8e8e8;
  border: 1px solid #eee;
  /* box-shadow: 6px 6px 12px #c5c5c5, -6px -6px 12px #ffffff; */
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

.player-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
  font-size: 14px;
}

.player-tip {
  color: #666;
}

.player-avatar {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  vertical-align: middle;
}

.player-username {
  color: #333;
  font-weight: 500;
}

.player-uuid {
  color: #666;
  font-family: monospace;
}

.player-error {
  color: #ff4d4f;
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
</style> 