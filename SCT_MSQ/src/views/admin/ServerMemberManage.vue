<template>
    <div class="server-member-manage">
        <h2>服务器成员管理</h2>

        <div class="card-container">
            <el-tabs v-model="activeTab" class="member-tabs">
                <el-tab-pane label="服务器成员" name="member">
                    <el-card class="member-card">
                        <template #header>
                            <div class="card-header">
                                <span>服务器成员</span>
                            </div>
                        </template>
                        <el-table :data="memberList" v-loading="loading">
                            <el-table-column prop="avatar" label="头像" align="center">
                                <template #default="scope">
                                    <img :src="scope.row.avatar" style="width:32px;height:32px;border-radius:50%;" />
                                </template>
                            </el-table-column>
                            <el-table-column prop="respondent" label="ID" align="center" />
                            <el-table-column prop="respondentContact" label="联系方式" align="center" />
                            <el-table-column prop="type" label="类型" align="center">
                                <template #default="scope">
                                    <el-tag :type="getTypeTagType(scope.row.type)">
                                        {{ typeMap[scope.row.type] || '未知' }}
                                    </el-tag>
                                </template>
                            </el-table-column>
                            <el-table-column prop="reviewer" label="审核人" align="center" />
                            <el-table-column prop="reviewTime" label="审核时间" align="center" />
                            <el-table-column label="操作" width="120" align="center">
                                <template #default="scope">
                                    <el-button 
                                        type="danger" 
                                        size="small" 
                                        @click="handleRemoveMember(scope.row)"
                                    >
                                        移出服务器
                                    </el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <div class="pagination-container" v-if="memberTotal > pageSize">
                            <el-pagination
                                v-model:current-page="memberPage"
                                v-model:page-size="pageSize"
                                :page-sizes="[10, 20, 50, 100]"
                                :total="memberTotal"
                                @size-change="handleSizeChange"
                                @current-change="getMemberList"
                                layout="total, sizes, prev, pager, next, jumper"
                            />
                        </div>
                    </el-card>
                </el-tab-pane>

                <el-tab-pane label="被移除成员" name="removed">
                    <el-card class="removed-card">
                        <template #header>
                            <div class="card-header">
                                <span>被移除成员</span>
                            </div>
                        </template>
                        <el-table :data="removedList" v-loading="loading">
                            <el-table-column prop="avatar" label="头像" align="center">
                                <template #default="scope">
                                    <img :src="scope.row.avatar" style="width:32px;height:32px;border-radius:50%;" />
                                </template>
                            </el-table-column>
                            <el-table-column prop="respondent" label="ID" align="center" />
                            <el-table-column prop="respondentContact" label="联系方式" align="center" />
                            <el-table-column prop="type" label="类型" align="center">
                                <template #default="scope">
                                    <el-tag :type="getTypeTagType(scope.row.type)">
                                        {{ typeMap[scope.row.type] || '未知' }}
                                    </el-tag>
                                </template>
                            </el-table-column>
                            <el-table-column prop="remover" label="移除人" align="center" />
                            <el-table-column prop="removeTime" label="移除时间" align="center" />
                            <el-table-column prop="remark" label="移除原因" align="center" />
                        </el-table>
                        <div class="pagination-container" v-if="removedTotal > pageSize">
                            <el-pagination
                                v-model:current-page="removedPage"
                                v-model:page-size="pageSize"
                                :page-sizes="[10, 20, 50, 100]"
                                :total="removedTotal"
                                @size-change="handleSizeChange"
                                @current-change="getRemovedList"
                                layout="total, sizes, prev, pager, next, jumper"
                            />
                        </div>
                    </el-card>
                </el-tab-pane>
            </el-tabs>
        </div>

        <!-- 移出服务器弹窗 -->
        <el-dialog
            v-model="removeDialogVisible"
            title="移出服务器"
            width="500px"
            destroy-on-close
        >
            <el-form :model="removeForm" label-width="80px" :rules="removeRules" ref="removeFormRef">
                <el-form-item label="移出原因" prop="remark">
                    <el-input
                        v-model="removeForm.remark"
                        type="textarea"
                        :rows="3"
                        placeholder="请输入移出原因"
                    />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="removeDialogVisible = false">取消</el-button>
                    <el-button type="danger" @click="confirmRemoveMember">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { fetchMemberList, removeMember } from '@/api/AdminMember';

interface ServerMember {
    id: number
    username: string
    role: number
    joinTime: string
    lastLoginTime: string
    status: number
}

// 角色选项
const roleOptions = [
    { value: 1, label: '管理员' },
    { value: 2, label: '普通成员' },
    { value: 3, label: '游客' }
]

// 获取角色标签
const getRoleLabel = (role: number) => {
    const option = roleOptions.find(item => item.value === role)
    return option ? option.label : '未知角色'
}

// 获取角色标签样式
const getRoleTagType = (role: number): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
    const typeMap: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
        1: 'danger',    // 管理员 - 红色
        2: 'primary',   // 普通成员 - 蓝色
        3: 'info'       // 游客 - 灰色
    }
    return typeMap[role] || 'info'
}

const loading = ref(false)
const tableData = ref<ServerMember[]>([])
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()

const form = ref<ServerMember>({
    id: 0,
    username: '',
    role: 2,
    joinTime: '',
    lastLoginTime: '',
    status: 1
})

const rules = {
    username: [
        { required: true, message: '请输入游戏ID', trigger: 'blur' },
        { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    role: [
        { required: true, message: '请选择角色', trigger: 'change' }
    ]
}

const typeMap = {
    1: '红石',
    2: '建筑',
    3: '后勤',
    4: '其他'
}

// 获取类型标签样式
const getTypeTagType = (type: number): 'success' | 'warning' | 'info' | 'danger' | 'primary' => {
    const typeMap: Record<number, 'success' | 'warning' | 'info' | 'danger' | 'primary'> = {
        1: 'danger',    // 红石 - 红色
        2: 'primary',   // 建筑 - 蓝色
        3: 'warning',   // 后勤 - 黄色
        4: 'info'       // 其他 - 灰色
    }
    return typeMap[type] || 'info'
}

const activeTab = ref('member')
const memberList = ref([])
const removedList = ref([])
const memberTotal = ref(0)
const removedTotal = ref(0)
const memberPage = ref(1)
const removedPage = ref(1)
const pageSize = ref(20)

// 处理每页显示数量变化
const handleSizeChange = (val: number) => {
    pageSize.value = val
    if (activeTab.value === 'member') {
        memberPage.value = 1
        getMemberList()
    } else {
        removedPage.value = 1
        getRemovedList()
    }
}

// 获取成员列表
const getMemberList = async () => {
    loading.value = true
    try {
        const res = await fetchMemberList({ 
            isRemoved: false, 
            pageNo: memberPage.value, 
            pageSize: pageSize.value 
        })
        memberList.value = res.data.list
        memberTotal.value = res.data.total
    } catch (error) {
        ElMessage.error('获取成员列表失败')
    } finally {
        loading.value = false
    }
}

// 获取被移除成员列表
const getRemovedList = async () => {
    loading.value = true
    try {
        const res = await fetchMemberList({ 
            isRemoved: true, 
            pageNo: removedPage.value, 
            pageSize: pageSize.value 
        })
        removedList.value = res.data.list
        removedTotal.value = res.data.total
    } catch (error) {
        ElMessage.error('获取被移除成员列表失败')
    } finally {
        loading.value = false
    }
}

// 移出服务器相关
const removeDialogVisible = ref(false)
const removeFormRef = ref<FormInstance>()
const removeForm = ref({
    id: 0,
    remark: ''
})

const removeRules = {
    remark: [
        { required: true, message: '请输入移出原因', trigger: 'blur' },
        { min: 2, max: 200, message: '长度在 2 到 200 个字符', trigger: 'blur' }
    ]
}

const handleRemoveMember = (row: any) => {
    removeForm.value = {
        id: row.id,
        remark: ''
    }
    removeDialogVisible.value = true
}

const confirmRemoveMember = async () => {
    if (!removeFormRef.value) return
    
    await removeFormRef.value.validate(async (valid) => {
        if (valid) {
            try {
                // 调用后端API移出成员
                await removeMember(removeForm.value)
                ElMessage.success('成员已移出服务器')
                removeDialogVisible.value = false
                getMemberList()
                getRemovedList()
            } catch (error) {
                ElMessage.error('移出失败')
            }
        }
    })
}

onMounted(() => {
    getMemberList()
    getRemovedList()
})
</script>

<style scoped>
.server-member-manage {
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
    .server-member-manage {
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

    .join-time, .last-login-time {
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
}

/* PC端样式 */
@media screen and (min-width: 769px) {
    .mobile-list {
        display: none;
    }
}

.card-container {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.member-card,
.removed-card {
    width: 100%;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

@media screen and (max-width: 768px) {
    .card-container {
        margin-top: 15px;
        gap: 15px;
    }
}

.member-tabs {
    width: 100%;
}

.member-tabs :deep(.el-tabs__content) {
    padding: 20px 0;
}

@media screen and (max-width: 768px) {
    .member-tabs :deep(.el-tabs__content) {
        padding: 15px 0;
    }
}
</style> 