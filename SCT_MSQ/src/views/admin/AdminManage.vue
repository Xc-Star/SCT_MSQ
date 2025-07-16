<template>
    <el-config-provider :locale="zhCn">
        <div class="admin-manage">
            <h2>管理员账号管理</h2>
            <div class="content">
                <div class="operation-bar">
                    <el-button type="primary" @click="handleAdd">新增管理员</el-button>
                </div>
                
                <!-- PC端表格 -->
                <el-table
                    v-loading="loading"
                    :data="tableData"
                    style="width: 100%"
                    v-if="!isMobile"
                    border
                >
                    <el-table-column type="index" label="编号" width="60" align="center" :index="(index) => index + 1" />
                    <el-table-column prop="username" label="用户名" width="100" align="center" />
                    <el-table-column prop="avatar" label="头像" width="80" align="center">
                        <template #default="scope">
                            <el-avatar :size="36" :src="scope.row.avatar">
                                {{ scope.row.username ? scope.row.username.charAt(0) : 'U' }}
                            </el-avatar>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="创建时间" min-width="160" align="center" />
                    <el-table-column prop="lastLoginTime" label="最后登录时间" min-width="160" align="center" />
                    <el-table-column prop="updateTime" label="更新时间" min-width="160" align="center" />
                    <el-table-column label="操作" fixed="right" width="120" align="center">
                        <template #default="scope">
                            <el-button 
                                type="primary" 
                                link 
                                @click="handleEdit(scope.row)"
                                :disabled="scope.row.isSuperAdmin"
                            >编辑</el-button>
                            <el-button 
                                type="danger" 
                                link 
                                @click="handleDelete(scope.row)"
                                :disabled="scope.row.isSuperAdmin"
                            >删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <!-- 移动端列表 -->
                <div class="mobile-list" v-else>
                    <div v-loading="loading" class="mobile-item" v-for="(item, index) in tableData" :key="item.id">
                        <div class="mobile-item-header">
                            <el-avatar :size="40" :src="item.avatar">
                                {{ item.username ? item.username.charAt(0) : 'U' }}
                            </el-avatar>
                            <span class="username">{{ item.username }}</span>
                            <div class="mobile-actions">
                                <el-button 
                                    type="primary" 
                                    link 
                                    @click="handleEdit(item)"
                                    :disabled="item.isSuperAdmin"
                                >编辑</el-button>
                                <el-button 
                                    type="danger" 
                                    link 
                                    @click="handleDelete(item)"
                                    :disabled="item.isSuperAdmin"
                                >删除</el-button>
                            </div>
                        </div>
                        <div class="mobile-item-info">
                            <p>编号: {{ index + 1 }}</p>
                            <p>创建时间: {{ item.createTime }}</p>
                            <p>最后登录: {{ item.lastLoginTime }}</p>
                            <p>更新时间: {{ item.updateTime }}</p>
                        </div>
                    </div>
                </div>

                <!-- 分页 -->
                <div class="pagination-container">
                    <el-pagination
                        v-model:current-page="currentPage"
                        v-model:page-size="pageSize"
                        :page-sizes="[10, 20, 50, 100]"
                        :total="total"
                        layout="total, sizes, prev, pager, next, jumper"
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                    />
                </div>

                <!-- 新增/编辑对话框 -->
                <el-dialog
                    v-model="dialogVisible"
                    :title="dialogType === 'add' ? '新增管理员' : '编辑管理员'"
                    width="400px"
                    destroy-on-close
                    center
                >
                    <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
                        <el-form-item label="用户名" prop="username">
                            <el-input v-model="form.username" />
                        </el-form-item>
                        <el-form-item label="密码" prop="password">
                            <el-input 
                                v-model="form.password" 
                                :placeholder="dialogType === 'edit' ? '不输入则不修改密码' : ''"
                            />
                        </el-form-item>
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
    </el-config-provider>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { getAdminList, addAdmin, updateAdmin, deleteAdmin } from '@/api/AdminUser'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import useUserInfoStore from '@/stores/userInfo'

interface Admin {
    id: number
    username: string
    password?: string
    avatar?: string
    createTime: string
    lastLoginTime: string
    updateTime: string
    deleted: boolean
    remark?: string
    isSuperAdmin: boolean // Added isSuperAdmin to the interface
}

const loading = ref(false)
const tableData = ref<Admin[]>([])
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const isMobile = ref(window.innerWidth <= 768)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const form = ref<Admin>({
    id: 0,
    username: '',
    password: '',
    createTime: '',
    lastLoginTime: '',
    updateTime: '',
    deleted: false,
    isSuperAdmin: false // Added isSuperAdmin to the form
})

const rules = ref({
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
    ]
})

const userInfoStore = useUserInfoStore()
const userInfo = userInfoStore.info

// 获取管理员列表
const fetchAdminList = async () => {
    loading.value = true
    try {
        const response = await getAdminList({
            pageNo: currentPage.value,
            pageSize: pageSize.value
        })
        tableData.value = response.data.list
        total.value = response.data.total
    } catch (error) {
        ElMessage.error('获取管理员列表失败')
    } finally {
        loading.value = false
    }
}

// 处理新增
const handleAdd = () => {
    dialogType.value = 'add'
    // 新增时设置必填规则
    rules.value = {
        username: [
            { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
            { required: true, message: '请输入密码', trigger: 'blur' },
            { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ]
    }
    form.value = {
        id: 0,
        username: '',
        password: '',
        createTime: '',
        lastLoginTime: '',
        updateTime: '',
        deleted: false,
        isSuperAdmin: false // Added isSuperAdmin to the form
    }
    dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row: Admin) => {
    dialogType.value = 'edit'
    // 编辑时移除必填规则
    rules.value = {
        username: [],
        password: [
            { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ]
    }
    form.value = {
        ...row,
        password: '' // 编辑时清空密码
    }
    dialogVisible.value = true
}

// 处理删除
const handleDelete = (row: Admin) => {
    ElMessageBox.confirm(
        '确定要删除该管理员吗？',
        '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            await deleteAdmin(row.id)
            ElMessage.success('删除成功')
            fetchAdminList()
        } catch (error) {
            ElMessage.error('删除失败')
        }
    }).catch(() => {})
}

// 处理提交
const handleSubmit = async () => {
    if (!formRef.value) return
    
    await formRef.value.validate(async (valid) => {
        if (valid) {
            try {
                const submitData = {
                    ...form.value,
                    password: form.value.password?.trim() || undefined,
                    updateTime: undefined
                }
                
                if (dialogType.value === 'add') {
                    await addAdmin(submitData)
                } else {
                    // 编辑时移除必填校验
                    formRef.value.clearValidate(['username', 'password'])
                    await updateAdmin(submitData)
                }
                ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
                dialogVisible.value = false
                fetchAdminList()
            } catch (error) {
                ElMessage.error(dialogType.value === 'add' ? '新增失败' : '编辑失败')
            }
        }
    })
}

// 处理分页大小变化
const handleSizeChange = (val: number) => {
    pageSize.value = val
    fetchAdminList()
}

// 处理页码变化
const handleCurrentChange = (val: number) => {
    currentPage.value = val
    fetchAdminList()
}

// 监听窗口大小变化
window.addEventListener('resize', () => {
    isMobile.value = window.innerWidth <= 768
})

onMounted(() => {
    fetchAdminList()
})
</script>

<style scoped>
.admin-manage {
    padding: 20px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h2 {
    margin-bottom: 20px;
    color: #333;
}

.content {
    padding: 20px;
}

.operation-bar {
    margin-bottom: 20px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
}

/* 移动端样式 */
.mobile-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.mobile-item {
    background: #fff;
    border-radius: 8px;
    padding: 16px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.mobile-item-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
    padding-bottom: 12px;
    border-bottom: 1px solid #eee;
}

.mobile-item-header .username {
    font-size: 16px;
    font-weight: 500;
    flex: 1;
}

.mobile-actions {
    display: flex;
    gap: 8px;
}

.mobile-item-info {
    color: #666;
    font-size: 14px;
}

.mobile-item-info p {
    margin: 6px 0;
    line-height: 1.5;
}

:deep(.el-table) {
    --el-table-border-color: #ebeef5;
    --el-table-header-bg-color: #f5f7fa;
}

:deep(.el-table th) {
    font-weight: 600;
    color: #606266;
}

:deep(.el-table td) {
    padding: 8px 0;
}

:deep(.el-pagination) {
    justify-content: flex-end;
    margin-top: 20px;
}

:deep(.el-dialog__body) {
    padding: 20px 30px;
}

:deep(.el-form-item__label) {
    font-weight: 500;
}
</style> 