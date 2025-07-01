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
        <form v-else class="msq-form">
          <div class="title" style="margin-bottom: 64px; position: relative;">
            <h2 style="text-align: center; font-weight: 700; font-size: clamp(40px, 8vw, 80px); color: black;">{{ topic.msqName }}</h2>
            <template v-if="[2,3,4].includes(topic.status)">
              <div class="stamp" :class="getStampClass(topic.status)">
                {{ getStampText(topic.status) }}
              </div>
            </template>
          </div>
  
          <div class="msq-topic" v-for="(topic, index) in parsedTopics" :key="topic.topic">
  
            <div v-if="topic.type === 'input'">
              <div class="input-container">
                <input required id="input" type="text" v-model="submitData[topic.topic]" autocomplete="off" disabled />
                <label class="label" for="input">{{ index + 1 }}. {{ topic.topic }}</label>
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
  
            <div v-if="topic.type === 'radio'">
              {{ index + 1 }}. {{ topic.topic }}
              <div class="radio-button-container">
                <div class="radio-button" v-for="(option, optionIndex) in topic.options" :key="optionIndex">
                  <input type="radio" class="radio-button__input" :id="'radio-' + topic.topic + '-' + optionIndex" :name="'radio-group-' + topic.topic" :value="option" v-model="submitData[topic.topic]" disabled>
                  <label class="radio-button__label" :for="'radio-' + topic.topic + '-' + optionIndex">
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
                  <input type="checkbox" class="cyberpunk-checkbox" :id="'checkbox-' + topic.topic + '-' + optionIndex" :name="'checkbox-group-' + topic.topic" :value="option" v-model="submitData[topic.topic]" disabled>
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

          <div class="review-buttons" v-if="!isViewMode">
              <button type="button" class="button2 reject" @click="handleReview(false)">不通过</button>
              <button type="button" class="button2 approve" @click="handleReview(true)">通过</button>
          </div>
        </form>
      </div>
    </div>
    <div v-if="showImageViewer" class="image-viewer-overlay" @click="closeImageViewer">
      <img :src="currentImageUrl" class="image-viewer-img" @click.stop />
    </div>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted, computed } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import { ElMessage } from 'element-plus'
  
  interface TopicOption {
    topic: string
    type: 'input' | 'radio' | 'checkbox'
    options: string
    topicResult: string | null
    topicResults: string | null
  }
  
  interface ParsedTopicOption extends Omit<TopicOption, 'options'> {
    options: string[]
    images?: { id: number; imageUrl: string }[]
  }
  
  interface Topic {
    id: number
    msqName: string
    topicResults: TopicOption[]
    status: number
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
    [key: string]: string | string[]
  }
  
  interface FormattedData {
    id: number
    topic: string
    value?: string
    values?: string[]
  }
  
  interface TopicResult {
    topicId: string
    topic: string
    topicResult?: string
    topicResults?: string[]
  }
  
  interface SubmitRequest {
    msqId: number
    name: string
    type: number
    topicResults: TopicResult[]
  }
  
  interface ApiResponse<T> {
    code: number
    message: string | null
    data: T
  }
  
  const topic = ref<Topic>({
    id: 0,
    msqName: '',
    topicResults: [],
    status: 1
  })
  
  const submitData = ref<SubmitData>({})
  const questionnaireList = ref<Questionnaire[]>([])
  const showQuestionnaireList = ref(false)
  
  import { getReviewInfo, updateResultStatus } from '@/api/AdminMsq.js'
  
  const error = ref<string | null>(null)
  const loading = ref(false)
  const router = useRouter()
  const route = useRoute()
  
  const parsedTopics = computed(() => {
    return topic.value.topicResults.map(item => {
      let images: { id: number; imageUrl: string }[] = []
      if (Array.isArray((item as any).images)) {
        images = (item as any).images
      }
      return {
        ...item,
        options: JSON.parse(item.options || '[]'),
        images
      }
    })
  })
  
  // 新增：判断是否为查看模式
  const isViewMode = computed(() => route.query.mode === 'view')
  
  // 使用异步函数获取数据
  const fetchData = async () => {
    loading.value = true
    error.value = null
    try {
      const id = Number(route.params.id)
      if (!id) {
        throw new Error('问卷ID不存在')
      }
      const response = await getReviewInfo(id);
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
          topic.value.topicResults.forEach(item => {
            if (item.type === 'checkbox') {
              submitData.value[item.topic] = JSON.parse(item.topicResults || '[]')
            } else {
              submitData.value[item.topic] = item.topicResult || ''
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
  
  const retryFetch = () => {
    fetchData()
  }
  
  // 在组件挂载时获取数据
  onMounted(() => {
    fetchData();
  })
  
  const submit = async (e: Event) => {
    e.preventDefault()
    
    const topicResults: TopicResult[] = topic.value.topicResults.map(item => {
      const result: TopicResult = {
        topicId: item.topic,
        topic: item.topic
      }
      
      if (item.type === 'checkbox') {
        result.topicResults = submitData.value[item.topic] as string[] || []
      } else {
        result.topicResult = submitData.value[item.topic] as string || ''
      }
      
      return result
    })
    
    const submitRequest: SubmitRequest = {
      msqId: topic.value.id,
      name: topic.value.msqName,
      type: 0,
      topicResults: topicResults
    }
    
    // 提交审核结果
    ElMessage.success('审核成功')
    // 延迟1秒后跳转，让用户看到成功提示
    setTimeout(() => {
      router.push({
        name: 'questionnaire-review'
      })  // 跳转到问卷列表页
    }, 1000)
  }
  
  const handleCheckboxChange = (event, topicId) => {
    if (!Array.isArray(submitData.value[topicId])) {
      submitData.value[topicId] = []
    }
  }
  
  const handleReview = async (approved: boolean) => {
    try {
      const params = {
        id: topic.value.id,
        status: approved ? 2 : 3
      }
      await updateResultStatus(params)
      if (approved) {
        ElMessage.success('审核通过')
      } else {
        ElMessage.error('审核不通过')
      }
      // 延迟1秒后跳转
      setTimeout(() => {
        router.push({
            name: 'questionnaireReview'
        })
      }, 1000)
    } catch (error) {
      ElMessage.error('操作失败，请重试')
    }
  }
  
  const showImageViewer = ref(false)
  const currentImageUrl = ref('')

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
  
  // 盖章文字
  function getStampText(status: number) {
    switch (status) {
      case 2:
        return '已通过'
      case 3:
        return '未通过'
      case 4:
        return '已移出'
      default:
        return ''
    }
  }
  // 盖章样式
  function getStampClass(status: number) {
    switch (status) {
      case 2:
        return 'stamp-success'
      case 3:
        return 'stamp-danger'
      case 4:
        return 'stamp-removed'
      default:
        return ''
    }
  }
  
  // topic.value = {
  //   id: 1,
  //   name: 'SCT问卷',
  //   description: 'SCT的一些碎碎念，可以要也可以不要的，比如可以介绍一下服务器的特色什么的，也可以介绍一下问卷啥的',
  //   topics: [
  //     {
  //       id: 1,
  //       type: 'input',
  //       topic: '请输入你的正版ID',
  //     },
  //     {
  //       id: 2,
  //       type: 'radio',
  //       topic: '你的性别',
  //       options: ['男', '女', '草履虫', '沃尔玛购物袋', '其他']
  //     },
  //     {
  //       id: 3,
  //       type: 'checkbox',
  //       topic: '你的爱好',
  //       options: ['唱', '跳', 'rap', '篮球']
  //     },
  //   ]
  // }
  </script>
  
  <style scoped>
  .msq-container {
    display: flex;
    flex-direction: column;
    /* align-items: center; */
    /* justify-content: center; */
    min-height: 100vh;
    background-color: #fff;
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
  </style> 