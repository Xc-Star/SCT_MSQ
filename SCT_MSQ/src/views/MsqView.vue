<template>
  <div class="msq-container">
    <div class="container">
      <form class="msq-form">
        <div class="title" style="margin-bottom: 64px;">
          <h2 style="text-align: center; font-weight: 700; font-size: clamp(40px, 8vw, 80px); color: black;">{{ topic.name }}</h2>
          <span style="text-align: center; color: #666; display: block;">
            {{ topic.description }}
          </span>
        </div>

        <div class="msq-topic" v-for="(topic, index) in topic.topics" :key="topic.id">

          <div v-if="topic.type === 'input'">
            <!-- {{ topic.topic }} -->
            <div class="input-container">
              <input required id="input" type="text" v-model="submitData[topic.id]" autocomplete="off" />
              <label class="label" for="input">{{ index + 1 }}. {{ topic.topic }}</label>
              <div class="underline"></div>
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
            <div style="height: 32px;"></div>
          </div>

        </div>

        <button class="button2" @click="submit">提交</button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface TopicOption {
  id: number
  type: 'input' | 'radio' | 'checkbox'
  topic: string
  options?: string[]
}

interface Topic {
  id: number
  name: string
  description: string
  topics: TopicOption[]
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

const topic = ref<Topic>({
  id: 0,
  name: '',
  description: '',
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

import { getMsqVO } from '../api/MsqView.js'

// 使用异步函数获取数据
const fetchData = async () => {
  console.log('使用异步函数获取数据')
  const response = await getMsqVO(1);
  console.log('res: ', response);
  topic.value = response.data
  console.log('res: ', response.data);
  
  // 在数据加载完成后初始化多选框数据
  topic.value.topics.forEach(item => {
    if (item.type === 'checkbox') {
      submitData.value[item.id] = []
    }
  })
}

// const fetchData =  () => {
//   console.log('使用异步函数获取数据')
//   getMsqVO(1).then(response =>{
//     console.log('[p]res: ', response);
//     topic.value = response.data
//     console.log('[1]res: ', response.data);
    
//     // 在数据加载完成后初始化多选框数据
//     topic.value.topics.forEach(item => {
//       if (item.type === 'checkbox') {
//         submitData.value[item.id] = []
//       }
//     })
//   })
// }

// 在组件挂载时获取数据
onMounted(() => {
  fetchData();
})

const submit = (e: Event) => {
  e.preventDefault()
  
  const formattedData: FormattedData[] = topic.value.topics.map(item => {
    const data: FormattedData = {
      id: item.id,
      topic: item.topic,
    }
    
    if (item.type === 'checkbox') {
      data.values = submitData.value[item.id] as string[] || []
    } else {
      data.value = submitData.value[item.id] as string || ''
    }
    
    return data
  })
  
  console.log('提交的数据：', formattedData)
}

const handleCheckboxChange = (event, topicId) => {
  if (!Array.isArray(submitData.value[topicId])) {
    submitData.value[topicId] = []
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
  background-color: skyblue;
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 50px auto;
  padding: 60px 5%;
  background-color: rgba(255, 255, 255, .3);
  box-shadow: 5px 5px 5px rgba(0, 0, 0, .3);
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
</style> 