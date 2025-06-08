<template>
    <div class="questionnaire-manage">
        <h2>问卷管理</h2>
        <div class="content">
            <div class="operation-bar">
                <el-button type="primary" @click="handleAdd">新增问卷</el-button>
            </div>
            
            <el-table :data="tableData" style="width: 100%" v-loading="loading">
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
                <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
                <el-table-column prop="remark" label="备注" align="center" />
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

            <!-- 新增/编辑对话框 -->
            <el-dialog
                v-model="dialogVisible"
                :title="dialogType === 'add' ? '新增问卷' : '编辑问卷'"
                width="500px"
            >
                <el-form :model="form" label-width="80px">
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
                    <!-- <el-form-item label="状态">
                        <el-switch
                            v-model="form.status"
                            :active-value="1"
                            :inactive-value="0"
                            active-text="启用"
                            inactive-text="禁用"
                        />
                    </el-form-item>
                    <el-form-item label="备注">
                        <el-input v-model="form.remark" type="textarea" />
                    </el-form-item> -->
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="dialogVisible = false">取消</el-button>
                        <el-button type="primary" @click="handleSubmit">确定</el-button>
                    </span>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<script setup lang="ts">
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
    type: number
    description: string
    updateTime: string
    remark: string
    status: number
}

// 问卷类型选项
const typeOptions = [
    { value: 1, label: '红石问卷' },
    { value: 2, label: '建筑问卷' },
    { value: 3, label: '其他' }
]

// 获取类型标签
const getTypeLabel = (type: number) => {
    const option = typeOptions.find(item => item.value === type)
    return option ? option.label : '未知类型'
}

// 获取类型标签样式
const getTypeTagType = (type: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
    const typeMap: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
        1: 'primary',    // 红石问卷 - 蓝色
        2: 'success',    // 建筑问卷 - 绿色
        3: 'info'        // 其他 - 灰色
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
    type: 1,
    description: '',
    updateTime: '',
    remark: '',
    status: 1
})

// 获取问卷列表
const getList = async () => {
    loading.value = true
    const response = await getMsqList()
    tableData.value = response.data
    // 这里使用模拟数据
    // tableData.value = [
    //     {
    //         id: 1,
    //         name: '示例问卷1',
    //         type: 1,
    //         description: '这是一个示例问卷',
    //         updateTime: '2024-03-20 10:00:00',
    //         remark: '测试备注',
    //         status: 1
    //     }
    // ]
}

// 新增问卷
const handleAdd = () => {
    dialogType.value = 'add'
    form.value = {
        id: undefined,
        name: '',
        type: 1,
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
    form.value = { ...row }
    dialogVisible.value = true
}

// 删除问卷
const handleDelete = (row: Questionnaire) => {
    ElMessageBox.confirm('确认删除该问卷吗？', '提示', {
        type: 'warning'
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
    } else {
        await updateMsq(form.value)
    }
    
    ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
    dialogVisible.value = false
    getList()
}

// 状态变更处理
const handleStatusChange = async (row: Questionnaire) => {
    // 调用后端API更新状态
    await updateStatus({id: row.id, status: row.status})
    ElMessage.success('状态更新成功: ' + row.status)
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
</style> 