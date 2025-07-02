<template>
  <div class="msq-container">
      <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
      </div>
      <div v-else-if="error" class="error-message">
      <el-alert
        :title="error"
        type="error"
        :closable="false"
        show-icon
      />
      <el-button type="primary" @click="retryFetch" style="margin-top: 20px">重试</el-button>
      </div>
    <el-form v-else :model="topic" label-width="80px" class="msq-form">
      <el-form-item label="问卷名称" required>
        <el-input v-model="topic.name" placeholder="请输入问卷名称" />
      </el-form-item>
      <el-form-item label="问卷类型" required>
        <el-select v-model="topic.type" placeholder="请选择问卷类型" style="width: 200px">
          <el-option label="红石问卷" :value="1" />
          <el-option label="建筑问卷" :value="2" />
          <el-option label="后勤问卷" :value="3" />
          <el-option label="其他" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="问卷描述" required>
        <el-input v-model="topic.description" type="textarea" :rows="3" placeholder="请输入问卷描述" />
      </el-form-item>

      <el-divider content-position="left">问题列表</el-divider>

      <div class="table-operations">
        <el-button type="primary" @click="handleAddQuestion">新增问题</el-button>
        </div>

      <el-table 
        :data="topic.topics" 
        style="width: 100%" 
        border
        row-key="id"
        :row-class-name="getRowClassName"
      >
        <el-table-column type="index" label="编号" width="80" align="center" />
        <el-table-column label="问题类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getQuestionTypeTag(scope.row.type)">
              {{ getQuestionTypeLabel(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="topic" label="问题内容" min-width="200" />
        <el-table-column label="选项" min-width="200">
          <template #default="scope">
            <template v-if="scope.row.options && scope.row.options.length">
              <el-tag 
                v-for="option in scope.row.options" 
                :key="option"
                class="option-tag"
                size="small"
              >
                {{ option }}
              </el-tag>
            </template>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="图片" min-width="120">
          <template #default="scope">
            <div v-if="scope.row.images && scope.row.images.length">
              <el-image
                v-for="(img, idx) in scope.row.images"
                :key="img"
                :src="img"
                style="width: 40px; height: 40px; margin-right: 4px; border-radius: 4px; cursor: pointer"
                fit="cover"
                @click="openImagePreview(scope.row.images, idx)"
              />
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <div class="operation-buttons">
              <div class="left-buttons">
                <el-button 
                  size="small" 
                  @click="handleEditQuestion(scope.row)"
                  :disabled="scope.row.id < 0"
                >编辑</el-button>
                <el-button 
                  size="small" 
                  type="danger" 
                  @click="handleDeleteQuestion(scope.row)"
                  :disabled="scope.row.id < 0"
                >删除</el-button>
            </div>
              <div class="right-buttons">
                <el-button 
                  size="small" 
                  @click="handleMoveUp(scope.$index)"
                  :disabled="scope.$index === 0 || scope.row.id < 0"
                >
                  <el-icon><ArrowUp /></el-icon>
                </el-button>
                <el-button 
                  size="small" 
                  @click="handleMoveDown(scope.$index)"
                  :disabled="scope.$index === topic.topics.length - 1 || scope.row.id < 0"
                >
                  <el-icon><ArrowDown /></el-icon>
                </el-button>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <el-form-item class="form-buttons-container">
        <div class="form-buttons">
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" @click="submit">保存问卷</el-button>
        </div>
      </el-form-item>
    </el-form>

    <!-- 问题编辑对话框 -->
    <el-dialog
      v-model="questionDialogVisible"
      :title="questionDialogType === 'add' ? '新增问题' : '编辑问题'"
      width="800px"
      class="question-dialog"
    >
      <el-form :model="questionForm" label-width="100px" class="question-form">
        <el-form-item label="问题类型" required>
          <el-select v-model="questionForm.type" placeholder="请选择问题类型" style="width: 200px">
            <el-option label="填空题" value="input" />
            <el-option label="单选题" value="radio" />
            <el-option label="多选题" value="checkbox" />
            <el-option label="文件上传" value="file" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题内容" required>
          <el-input v-model="questionForm.topic" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item 
          label="选项" 
          v-if="questionForm.type === 'radio' || questionForm.type === 'checkbox'"
          required
        >
          <el-input-tag
            v-model="questionForm.options"
            :suggestions="[]"
            placeholder="请输入选项内容，按回车确认"
            :max-collapse-tags="3"
            :max-tags="10"
            :allow-duplicate="false"
            :allow-empty="false"
            @change="handleOptionsChange"
          />
          <div class="option-tips">
            <el-text type="info" size="small">
              提示：输入选项内容后按回车键添加，点击标签上的 × 删除选项
            </el-text>
          </div>
        </el-form-item>
        <el-form-item label="图片上传">
          <el-upload
            class="upload-demo"
            action="/api/upload/image"
            list-type="picture-card"
            :limit="3"
            :on-success="handleImageSuccess"
            :on-remove="handleImageRemove"
            :file-list="imageFileList"
            :on-exceed="() => ElMessage.warning('最多上传三张图片')"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="option-tips">
            <el-text type="info" size="small">
              最多上传3张图片，支持拖拽排序
            </el-text>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="questionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleQuestionSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="previewVisible" :width="previewDialogWidth" :show-close="true" center class="image-preview-dialog">
      <div style="text-align:center">
        <el-image
          :src="previewImages[previewIndex]"
          style="max-width: 100%; max-height: 70vh"
          fit="contain"
        />
        <div style="margin-top: 10px;">
          <el-button
            v-if="previewIndex > 0"
            @click="previewIndex--"
            size="small"
            style="margin-right: 10px"
          >上一张</el-button>
          <el-button
            v-if="previewIndex < previewImages.length - 1"
            @click="previewIndex++"
            size="small"
          >下一张</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { adminGetMsqVO } from '@/api/AdminMsq.js'
import { adminUpdateMsq } from '@/api/AdminMsq.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowUp, ArrowDown, Plus } from '@element-plus/icons-vue'
import { useWindowSize } from '@vueuse/core'

// 定义props接收id
const props = defineProps<{
  id: number
}>()

// 定义emit
const emit = defineEmits<{
  (e: 'save-success'): void
}>()

interface TopicOption {
  id: number
  type: 'input' | 'radio' | 'checkbox' | 'file'
  topic: string
  options?: string[]
  images?: string[]
}

interface Topic {
  id: number
  name: string
  description: string
  type: number
  topics: TopicOption[]
}

interface SubmitData {
  [key: number]: string | string[]
}

const topic = ref<Topic>({
  id: 0,
  name: '',
  description: '',
  type: 0,
  topics: []
})

const submitData = ref<SubmitData>({})
const error = ref<string | null>(null)
const loading = ref(false)

const questionDialogVisible = ref(false)
const questionDialogType = ref<'add' | 'edit'>('add')
const questionForm = ref({
  id: 0,
  type: 'input' as 'input' | 'radio' | 'checkbox' | 'file',
  topic: '',
  options: [],
  images: [] as string[]
})

// 图片预览相关
const previewVisible = ref(false)
const previewImages = ref<string[]>([])
const previewIndex = ref(0)
const openImagePreview = (images: string[], index: number) => {
  previewImages.value = images
  previewIndex.value = index
  previewVisible.value = true
}

// 获取问题类型标签
const getQuestionTypeLabel = (type: string) => {
  const typeMap: Record<string, string> = {
    'input': '填空题',
    'radio': '单选题',
    'checkbox': '多选题',
    'file': '文件上传'
  }
  return typeMap[type] || '未知类型'
}

// 获取问题类型标签样式
const getQuestionTypeTag = (type: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  const typeMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    'input': 'primary',
    'radio': 'success',
    'checkbox': 'warning',
    'file': 'danger'
  }
  return typeMap[type] || 'info'
}

// 使用异步函数获取数据
const fetchData = async () => {
  if (!props.id) {
    console.warn('No id provided')
    return
  }
  
  loading.value = true
  error.value = null
  try {
    const response = await adminGetMsqVO(props.id);
    if (response && response.data) {
      topic.value = response.data
      // 确保每个题目的 images 字段为图片地址数组，options为数组
      topic.value.topics.forEach(item => {
        // 1. 兼容 images 为对象数组的情况
        if (Array.isArray(item.images)) {
          if (item.images.length > 0 && typeof item.images[0] === 'object' && item.images[0] !== null) {
            // 转成图片地址数组
            item.images = item.images.map((imgObj: any) => imgObj && typeof imgObj === 'object' && 'imageUrl' in imgObj ? imgObj.imageUrl : '').filter(Boolean)
          } else if (item.images.length > 0 && typeof item.images[0] === 'string') {
            // 已经是字符串数组，直接用
            item.images = item.images.filter(Boolean)
          }
        } else {
          item.images = []
        }
        // 2. 其它字段兼容
        if (!Array.isArray(item.images)) {
          item.images = []
        }
        if (item.type === 'checkbox') {
          submitData.value[item.id] = []
        }
        // 3. 兼容 options 字段为 null
        if (!Array.isArray(item.options)) {
          item.options = []
        }
      })
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

// 监听id的变化
watch(() => props.id, (newId) => {
  if (newId && typeof newId === 'number') {
    fetchData()
  } else {
    error.value = '无效的问卷ID'
  }
}, { immediate: true })

const retryFetch = () => {
  fetchData()
}

// 提交问卷
const submit = async () => {
  // 验证基本信息
  if (!topic.value.name) {
    ElMessage.error('请输入问卷名称')
    return
  }
  if (!topic.value.type) {
    ElMessage.error('请选择问卷类型')
    return
  }
  if (!topic.value.description) {
    ElMessage.error('请输入问卷描述')
    return
  }

  // 验证问题列表
  if (topic.value.topics.length === 0) {
    ElMessage.error('请至少添加一个问题')
    return
  }

  // 验证每个问题
  const invalidQuestions = topic.value.topics.filter(item => {
    if (!item.topic) return true
    if ((item.type === 'radio' || item.type === 'checkbox') && 
        (!item.options || item.options.length === 0)) return true
    return false
  })

  if (invalidQuestions.length > 0) {
    ElMessage.error('存在无效的问题，请检查')
    return
  }

  // 构建提交数据
  const submitData = {
    id: topic.value.id,
    name: topic.value.name,
    type: topic.value.type,
    description: topic.value.description,
    topics: topic.value.topics.map((item, index) => ({
      id: item.id,
      no: index + 1,
      type: item.type,
      topic: item.topic,
      options: item.type === 'input' ? [] : item.options,
      images: item.images
    }))
  }

  try {
    await adminUpdateMsq(submitData)
    ElMessage.success('保存成功')
    emit('save-success') // 触发保存成功事件
  } catch (error) {
    ElMessage.error('保存失败：' + (error.message || '未知错误'))
  }
}

const resetForm = () => {
  ElMessageBox.confirm(
    '确认重置所有内容吗？这将清空所有已编辑的内容。',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    fetchData()
    ElMessage.success('已重置')
  })
}

// 新增问题
const handleAddQuestion = () => {
  questionDialogType.value = 'add'
  questionForm.value = {
    id: undefined, // 使用负数作为临时ID
    type: 'input' as 'input' | 'radio' | 'checkbox' | 'file',
    topic: '',
    options: [],
    images: []
  }
  questionDialogVisible.value = true
}

// 编辑问题
const handleEditQuestion = (row: TopicOption) => {
  questionDialogType.value = 'edit'
  questionForm.value = {
    id: row.id,
    type: row.type,
    topic: row.topic,
    options: row.options ? [...row.options] : [],
    images: Array.isArray(row.images) ? [...row.images] : []
  }
  questionDialogVisible.value = true
}

// 删除问题
const handleDeleteQuestion = (row: TopicOption) => {
  ElMessageBox.confirm(
    '确认删除该问题吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = topic.value.topics.findIndex(item => item.id === row.id)
    if (index !== -1) {
      topic.value.topics.splice(index, 1)
      ElMessage.success('删除成功')
    }
  })
}

// 处理选项变化
const handleOptionsChange = (value: string[]) => {
  questionForm.value.options = value
}

// 提交问题表单
const handleQuestionSubmit = () => {
  if (!questionForm.value.topic) {
    ElMessage.error('请输入问题内容')
    return
  }

  if ((questionForm.value.type === 'radio' || questionForm.value.type === 'checkbox') && 
      (!questionForm.value.options || questionForm.value.options.length === 0)) {
    ElMessage.error('请至少添加一个选项')
    return
  }

  if (questionDialogType.value === 'add') {
    topic.value.topics.push({
      id: questionForm.value.id,
      type: questionForm.value.type,
      topic: questionForm.value.topic,
      options: (questionForm.value.type === 'input') ? undefined : (questionForm.value.type === 'file' ? [] : questionForm.value.options),
      images: Array.isArray(questionForm.value.images) ? questionForm.value.images : []
    })
  } else {
    const index = topic.value.topics.findIndex(item => item.id === questionForm.value.id)
    if (index !== -1) {
      topic.value.topics[index] = {
        id: questionForm.value.id,
        type: questionForm.value.type,
        topic: questionForm.value.topic,
        options: (questionForm.value.type === 'input') ? undefined : (questionForm.value.type === 'file' ? [] : questionForm.value.options),
        images: Array.isArray(questionForm.value.images) ? questionForm.value.images : []
      }
    }
  }

  questionDialogVisible.value = false
  ElMessage.success(questionDialogType.value === 'add' ? '新增成功' : '编辑成功')
}

// 获取行类名
const getRowClassName = ({ row }: { row: TopicOption }) => {
  return row.id < 0 ? 'disabled-row' : ''
}

// 上移问题
const handleMoveUp = (index: number) => {
  if (index > 0) {
    const temp = topic.value.topics[index]
    topic.value.topics[index] = topic.value.topics[index - 1]
    topic.value.topics[index - 1] = temp
  }
}

// 下移问题
const handleMoveDown = (index: number) => {
  if (index < topic.value.topics.length - 1) {
    const temp = topic.value.topics[index]
    topic.value.topics[index] = topic.value.topics[index + 1]
    topic.value.topics[index + 1] = temp
  }
}

// 图片上传 file-list 适配
const imageFileList = computed(() =>
  questionForm.value.images.map((url, idx) => ({ name: `图片${idx + 1}`, url }))
)

// 图片上传成功
const handleImageSuccess = (response, file, fileList) => {
  // 上传成功后，手动把 file.url 设置为后端返回的地址
  file.url = response.data
  questionForm.value.images = fileList.map(f => f.url || (f.response && f.response.data)).filter(Boolean)
}

// 删除图片
const handleImageRemove = (file, fileList) => {
  questionForm.value.images = fileList.map(f => f.url || (f.response && f.response.data)).filter(Boolean)
}

const { width: windowWidth } = useWindowSize()
const previewDialogWidth = computed(() => {
  // 90vw, 最小 300px，最大 900px
  const w = Math.min(windowWidth.value * 0.9, 900)
  return w < 300 ? '300px' : w + 'px'
})
</script>

<style scoped>
.msq-container {
  padding: 20px;
}

.msq-form {
  max-width: 1200px;
  margin: 0 auto;
}

.table-operations {
  margin-bottom: 20px;
}

.option-tag {
  margin-right: 8px;
  margin-bottom: 4px;
}

.option-item {
  margin-bottom: 15px;
}

.option-item :deep(.el-input) {
  width: 100%;
}

.option-item :deep(.el-input-group__append) {
  padding: 0;
}

.option-item :deep(.el-input-group__append .el-button) {
  margin: 0;
  border: none;
  padding: 0 15px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

.loading-container {
  padding: 20px;
}

.error-message {
  text-align: center;
  padding: 20px;
}

.question-dialog :deep(.el-dialog__body) {
  padding: 20px 40px;
}

.question-form {
  max-width: 700px;
  margin: 0 auto;
}

.option-tips {
  margin-top: 8px;
}

:deep(.el-input-tag) {
  width: 100%;
}

:deep(.el-input-tag__inner) {
  min-height: 32px;
}

:deep(.el-input-tag__tags) {
  margin: 0;
  padding: 0;
}

:deep(.el-tag) {
  margin: 4px;
}

.disabled-row {
  background-color: #f5f7fa;
  cursor: not-allowed;
}

:deep(.disabled-row) {
  background-color: #f5f7fa;
}

:deep(.disabled-row .el-button) {
  cursor: not-allowed;
}

:deep(.el-button .el-icon) {
  margin-right: 0;
}

.operation-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 4px;
}

.left-buttons {
  display: flex;
  gap: 4px;
}

.right-buttons {
  display: flex;
  flex-direction: column;
  gap: 2px;
  margin: 0;
}

:deep(.right-buttons .el-button) {
  width: 32px;
  height: 24px;
  padding: 0;
  border-radius: 4px;
  margin: 0;
}

:deep(.right-buttons .el-icon) {
  margin: 0;
  font-size: 14px;
}

:deep(.left-buttons .el-button) {
  padding: 0 8px;
}

.form-buttons-container {
  margin-top: 20px;
}

.form-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.form-buttons-container .el-form-item__content) {
  justify-content: flex-end;
}

@media screen and (max-width: 768px) {
  .msq-container {
    padding: 10px;
  }
}

.image-preview-dialog >>> .el-dialog {
  max-width: 90vw;
  min-width: 300px;
}
</style> 