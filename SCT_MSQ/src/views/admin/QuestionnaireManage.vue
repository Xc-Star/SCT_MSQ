<template>
    <div class="questionnaire-manage">
        <h2>问卷管理</h2>
        <div class="content">
            <div class="operation-bar">
                <el-button type="primary" @click="handleAdd">新增问卷</el-button>
            </div>
            
            <!-- PC端表格 -->
            <el-table :data="tableData" style="width: 100%" v-loading="loading" class="pc-table">
                <el-table-column prop="id" label="ID" width="80" align="center" />
                <el-table-column prop="name" label="问卷名称" align="center" />
                <el-table-column prop="type" label="类型" width="120" align="center">
                    <template #default="scope">
                        <el-tag :type="getTypeTagType(scope.row.type)">
                            {{ getTypeLabel(scope.row.type) }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="description" label="描述" align="center" />
                <el-table-column prop="updateTime" label="修改时间" width="180" align="center" />
                <el-table-column prop="remark" label="修改人" align="center" />
                <el-table-column prop="status" label="状态" width="100" align="center">
                    <template #default="scope">
                        <el-switch
                            v-model="scope.row.status"
                            :active-value="1"
                            :inactive-value="0"
                            @change="handleStatusChange(scope.row)"
                        />
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200" align="center">
                    <template #default="scope">
                        <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                        <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 移动端列表 -->
            <div class="mobile-list">
                <div v-for="item in tableData" :key="item.id" class="mobile-item" v-loading="loading">
                    <div class="item-header">
                        <span class="item-name">{{ item.name }}</span>
                        <el-tag :type="getTypeTagType(item.type)" size="small">
                            {{ getTypeLabel(item.type) }}
                        </el-tag>
                    </div>
                    <div class="item-content">
                        <p class="description">{{ item.description }}</p>
                        <p class="update-time">修改时间：{{ item.updateTime }}</p>
                        <p class="remark" v-if="item.remark">修改人：{{ item.remark }}</p>
                    </div>
                    <div class="item-footer">
                        <el-switch
                            v-model="item.status"
                            :active-value="1"
                            :inactive-value="0"
                            @change="handleStatusChange(item)"
                        />
                        <div class="operations">
                            <el-button size="small" @click="handleEdit(item)">编辑</el-button>
                            <el-button size="small" type="danger" @click="handleDelete(item)">删除</el-button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 新增/编辑对话框 -->
            <el-dialog
                v-model="dialogVisible"
                :title="dialogType === 'add' ? '新增问卷' : '编辑问卷'"
                :width="dialogType === 'add' ? (isMobile ? '90%' : '500px') : (isMobile ? '90%' : '1200px')"
                class="mobile-dialog"
                destroy-on-close
            >
                <el-form :model="form" label-width="80px" v-if="dialogType === 'add'">
                    <el-form-item label="问卷名称">
                        <el-input v-model="form.name" />
                    </el-form-item>
                    <el-form-item label="问卷类型">
                        <el-select v-model="form.type" placeholder="请选择问卷类型">
                            <el-option
                                v-for="item in typeOptions"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="描述">
                        <el-input v-model="form.description" type="textarea" />
                    </el-form-item>
                </el-form>
                <template #footer v-if="dialogType === 'add'">
                    <span class="dialog-footer">
                        <el-button @click="dialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="handleSubmit">确定</el-button>
                    </span>
                </template>
                <div v-if="dialogType === 'edit'" class="edit-container">
                    <MsqEditView :id="form.id" @save-success="handleSaveSuccess" />
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script setup lang="ts">
import MsqEditView from './components/MsqEditView.vue'
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import 'element-plus/dist/index.css'
import {
    ElButton,
    ElTable,
    ElTableColumn,
    ElDialog,
    ElForm,
    ElFormItem,
    ElInput,
    ElSwitch,
    ElSelect,
    ElOption,
    ElTag
} from 'element-plus'

import { getMsqList, addMsq, getMsq, updateMsq, updateStatus, deleteMsq } from '@/api/AdminMsq'

interface Questionnaire {
    id: number
    name: string
    type: number | string
    description: string
    updateTime: string
    remark: string
    status: number
}

// 问卷类型选项
const typeOptions = [
    { value: '', label: '请选择问卷类型' },
    { value: 1, label: '红石问卷' },
    { value: 2, label: '建筑问卷' },
    { value: 3, label: '后勤问卷' },
    { value: 4, label: '其他' }
]

// 获取类型标签
const getTypeLabel = (type: number | string) => {
    const option = typeOptions.find(item => item.value === type)
    return option ? option.label : '未知类型'
}

// 获取类型标签样式
const getTypeTagType = (type: number | string): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
    const typeMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
        '1': 'danger',    // 红石问卷 - 红色
        '2': 'primary',    // 建筑问卷 - 蓝色
        '3': 'warning',    // 后勤问卷 - 黄色
        '4': 'info'        // 其他 - 灰色
    }
    return typeMap[type] || 'info'
}

const loading = ref(false)
const tableData = ref<Questionnaire[]>([])
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const form = ref<Questionnaire>({
    id: 0,
    name: '',
    type: '',
    description: '',
    updateTime: '',
    remark: '',
    status: 1
})

// 添加移动端检测
const isMobile = ref(window.innerWidth <= 768)

// 监听窗口大小变化
window.addEventListener('resize', () => {
    isMobile.value = window.innerWidth <= 768
})

// 获取问卷列表
const getList = async () => {
    loading.value = true
    const response = await getMsqList()
    tableData.value = response.data
    loading.value = false
}

// 新增问卷
const handleAdd = () => {
    dialogType.value = 'add'
    form.value = {
        id: undefined,
        name: '',
        type: '',
        description: '',
        updateTime: '',
        remark: undefined,
        status: 1
    }
    dialogVisible.value = true
}

// 编辑问卷
const handleEdit = (row: Questionnaire) => {
    dialogType.value = 'edit'
    form.value = { 
        id: Number(row.id),
        name: row.name,
        type: row.type,
        description: row.description,
        updateTime: row.updateTime,
        remark: row.remark,
        status: row.status
    }
    dialogVisible.value = true
}

// 删除问卷
const handleDelete = (row: Questionnaire) => {
    ElMessageBox.confirm(
        '确认删除该问卷吗？', 
        '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }).then(async () => {
        // 调用后端API删除数据
        await deleteMsq(row.id)
        ElMessage.success('删除成功')
        getList()
    })
}

// 提交表单
const handleSubmit = async () => {
    form.value.updateTime = undefined
    // 调用后端API保存数据
    if (dialogType.value === 'add') {
        await addMsq(form.value)
        ElMessage.success('新增成功')
        dialogVisible.value = false
        getList()
    }
}

// 状态变更处理
const handleStatusChange = async (row: Questionnaire) => {
    // 调用后端API更新状态
    await updateStatus({id: row.id, status: row.status})
    ElMessage.success('状态更新成功: ' + row.status)
}

// 处理保存成功
const handleSaveSuccess = () => {
    dialogVisible.value = false
    getList()
}

onMounted(() => {
    getList()
})
</script>

<style scoped>
.questionnaire-manage {
    padding: 20px;
}

h2 {
    margin-bottom: 20px;
    color: #333;
}

.content {
    background-color: #fff;
    padding: 20px;
    border-radius: 4px;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.operation-bar {
    margin-bottom: 20px;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

/* 移动端适配样式 */
@media screen and (max-width: 768px) {
    .questionnaire-manage {
        padding: 10px;
    }

    .content {
        padding: 10px;
    }

    .pc-table {
        display: none;
    }

    .mobile-list {
        display: block;
    }

    .mobile-item {
        background: #fff;
        border-radius: 8px;
        padding: 15px;
        margin-bottom: 15px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    }

    .item-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;
    }

    .item-name {
        font-size: 16px;
        font-weight: bold;
    }

    .item-content {
        margin: 10px 0;
    }

    .description {
        color: #666;
        margin: 5px 0;
    }

    .update-time, .remark {
        font-size: 12px;
        color: #999;
        margin: 5px 0;
    }

    .item-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 10px;
        padding-top: 10px;
        border-top: 1px solid #eee;
    }

    .operations {
        display: flex;
        gap: 8px;
    }

    .mobile-dialog :deep(.el-dialog) {
        width: 90% !important;
        margin: 0 auto;
    }
}

/* PC端样式 */
@media screen and (min-width: 769px) {
    .mobile-list {
        display: none;
    }
}

.edit-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}

.mobile-dialog :deep(.el-dialog__body) {
  padding: 20px;
}

@media screen and (max-width: 768px) {
  .edit-container {
    padding: 10px;
  }
}
</style> 