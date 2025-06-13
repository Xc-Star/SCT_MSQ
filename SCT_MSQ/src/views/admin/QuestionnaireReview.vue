<template>
    <el-config-provider :locale="zhCn">
        <div class="questionnaire-review">
            <h2>问卷审核</h2>
            <div class="content">
                <!-- 搜索表单 -->
                <el-form :model="searchForm" class="search-form">
                    <el-form-item label="游戏ID">
                        <el-input v-model="searchForm.respondent" placeholder="请输入游戏ID" clearable />
                    </el-form-item>
                    <el-form-item label="联系方式">
                        <el-input v-model="searchForm.respondentContact" placeholder="请输入联系方式" clearable />
                    </el-form-item>
                    <el-form-item label="问卷类型">
                        <el-select v-model="searchForm.type" placeholder="请选择问卷类型" clearable style="width: 100%">
                            <el-option label="红石问卷" :value="1" />
                            <el-option label="建筑问卷" :value="2" />
                            <el-option label="后勤问卷" :value="3" />
                            <el-option label="其他" :value="4" />
                        </el-select>
                    </el-form-item>
                    <el-form-item class="button-group">
                        <el-button type="primary" @click="handleSearch">搜索</el-button>
                        <el-button @click="handleReset">重置</el-button>
                    </el-form-item>
                </el-form>

                <!-- PC端表格 -->
                <div class="pc-table" v-loading="loading">
                    <el-table :data="tableData" style="width: 100%">
                        <el-table-column type="index" label="编号" width="80" align="center" />
                        <el-table-column prop="msqName" label="问卷名称" align="center" />
                        <el-table-column prop="type" label="类型" align="center">
                            <template #default="scope">
                                <el-tag :type="getTypeTagType(scope.row.type)">{{ getTypeText(scope.row.type) }}</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="respondent" label="游戏ID" align="center" />
                        <el-table-column prop="respondentContact" label="联系方式" align="center" />
                        <el-table-column prop="createTime" label="提交时间" align="center" />
                        <el-table-column prop="status" label="状态" align="center">
                            <template #default="scope">
                                <el-tag :type="getStatusType(scope.row.status)">
                                    {{ getStatusText(scope.row.status) }}
                                </el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="reviewer" label="审核人" align="center" />
                        <el-table-column prop="reviewTime" label="审核时间" align="center" />
                        <el-table-column label="操作" width="150" align="center">
                            <template #default="scope">
                                <el-button 
                                    v-if="scope.row.status === 1"
                                    type="primary" 
                                    size="small" 
                                    @click="handleReview(scope.row)"
                                >审核</el-button>
                                <el-button 
                                    v-else
                                    type="danger" 
                                    size="small" 
                                    @click="handleDelete(scope.row)"
                                >删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>

                <!-- 移动端卡片列表 -->
                <div class="mobile-list" v-loading="loading">
                    <el-card v-for="(item, index) in tableData" :key="item.id" class="list-item">
                        <div class="item-header">
                            <span class="item-title">{{ item.msqName }}</span>
                            <el-tag :type="getStatusType(item.status)" size="small">
                                {{ getStatusText(item.status) }}
                            </el-tag>
                        </div>
                        <div class="item-content">
                            <div class="info-row">
                                <span class="label">类型：</span>
                                <el-tag :type="getTypeTagType(item.type)" size="small">{{ getTypeText(item.type) }}</el-tag>
                            </div>
                            <div class="info-row">
                                <span class="label">游戏ID：</span>
                                <span>{{ item.respondent }}</span>
                            </div>
                            <div class="info-row">
                                <span class="label">联系方式：</span>
                                <span>{{ item.respondentContact }}</span>
                            </div>
                            <div class="info-row">
                                <span class="label">提交时间：</span>
                                <span>{{ item.createTime }}</span>
                            </div>
                            <div class="info-row">
                                <span class="label">审核人：</span>
                                <span>{{ item.reviewer }}</span>
                            </div>
                            <div class="info-row">
                                <span class="label">审核时间：</span>
                                <span>{{ item.reviewTime }}</span>
                            </div>
                        </div>
                        <div class="item-footer">
                            <el-button 
                                v-if="item.status === 1"
                                type="primary" 
                                size="small" 
                                @click="handleReview(item)"
                            >审核</el-button>
                            <el-button 
                                v-else
                                type="danger" 
                                size="small" 
                                @click="handleDelete(item)"
                            >删除</el-button>
                        </div>
                    </el-card>
                </div>

                <!-- 分页 -->
                <div class="pagination-container">
                    <el-pagination
                        v-model:current-page="pageNo"
                        v-model:page-size="pageSize"
                        :page-sizes="[10, 20, 50, 100]"
                        :total="total"
                        :layout="isMobile ? 'total, prev, pager, next' : 'total, sizes, prev, pager, next, jumper'"
                        @size-change="handleSizeChange"
                        @current-change="handleCurrentChange"
                    />
                </div>
            </div>
        </div>
    </el-config-provider>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { adminGetResultPage, deleteMsqResult } from '@/api/AdminMsq'
import { ElMessage, ElMessageBox } from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { useRouter } from 'vue-router'

interface TableItem {
    id: number
    msqName: string
    respondent: string
    respondentContact: string
    reviewerId: number | null
    reviewer: string | null
    reviewTime: string | null
    createTime: string
    status: number
    msqId: number
    type: number
    remark: string | null
    deleted: boolean
}

interface ApiResponse {
    code: number
    message: string | null
    data: {
        list: TableItem[]
        total: number
    }
}

interface DeleteResponse {
    code: number
    message: string | null
    data: any
}

interface SearchForm {
    respondent?: string
    respondentContact?: string
    type?: number
}

const tableData = ref<TableItem[]>([])
const loading = ref(false)
const searchForm = ref<SearchForm>({
    respondent: undefined,
    respondentContact: undefined,
    type: undefined
})
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const isMobile = ref(window.innerWidth < 768)

const router = useRouter()

const handleResize = () => {
    isMobile.value = window.innerWidth < 768
}

const loadData = async () => {
    loading.value = true
    try {
        const params = {
            pageNo: pageNo.value,
            pageSize: pageSize.value,
            ...Object.fromEntries(
                Object.entries(searchForm.value).map(([key, value]) => [
                    key,
                    value === '' ? undefined : value
                ])
            )
        }
        console.log(params)
        const res = await adminGetResultPage(params) as unknown as ApiResponse
        if (res.code === 0 && res.data) {
            tableData.value = res.data.list
            total.value = res.data.total
            // 保持滚动位置
            nextTick(() => {
                const scrollPosition = window.scrollY
                window.scrollTo(0, scrollPosition)
            })
        } else {
            ElMessage.error('获取数据失败')
        }
    } catch (error) {
        ElMessage.error('获取数据失败')
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    pageNo.value = 1
    loadData()
}

const handleReset = () => {
    searchForm.value = {
        respondent: undefined,
        respondentContact: undefined,
        type: undefined
    }
    pageNo.value = 1
    loadData()
}

const handleSizeChange = (val: number) => {
    pageSize.value = val
    loadData()
}

const handleCurrentChange = (val: number) => {
    pageNo.value = val
    loadData()
}

const handleReview = async (row: TableItem) => {
    router.push({
        name: 'msq-review',
        params: {
            id: row.id
        }
    })
}

const handleDelete = async (row: TableItem) => {
    try {
        // 第一次确认
        await ElMessageBox.confirm('删除仅用于恶意使用正版ID导致本人无法提交问卷时使用，你确定你要删除吗！', '警告', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'error'
        })
        
        // 第二次确认
        await ElMessageBox.confirm('你确定你已经阅读了删除警告信息吗！', '再次确认', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
        
        // 调用删除API
        const res = await deleteMsqResult(row.id) as unknown as DeleteResponse
        if (res.code === 0) {
            ElMessage.success('删除成功')
            // 重新获取数据
            await loadData()
        } else {
            ElMessage.error(res.message || '删除失败')
        }
    } catch (error) {
        if (error !== 'cancel') {
            ElMessage.error('删除失败')
        }
    }
}

const getStatusType = (status: number) => {
    switch (status) {
        case 1:
            return 'info'
        case 2:
            return 'success'
        case 3:
            return 'danger'
        default:
            return 'info'
    }
}

const getStatusText = (status: number) => {
    switch (status) {
        case 1:
            return '未审核'
        case 2:
            return '已通过'
        case 3:
            return '未通过'
        default:
            return '未知状态'
    }
}

const getTypeTagType = (type: number) => {
    switch (type) {
        case 1:
            return 'danger'  // 红色
        case 2:
            return 'primary'  // 蓝色
        case 3:
            return 'warning'  // 黄色
        case 4:
            return 'info'  // 灰色
        default:
            return 'info'  // 灰色
    }
}

const getTypeText = (type: number) => {
    switch (type) {
        case 1:
            return '红石问卷'
        case 2:
            return '建筑问卷'
        case 3:
            return '后勤问卷'
        case 4:
            return '其他'
        default:
            return '未知类型'
    }
}

onMounted(() => {
    loadData()
    window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
    window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.questionnaire-review {
    padding: 10px;
}

h2 {
    margin-bottom: 15px;
    color: #333;
    font-size: 1.2rem;
}

.content {
    background-color: #fff;
    padding: 15px;
    border-radius: 4px;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.search-form {
    margin-bottom: 15px;
}

.search-form :deep(.el-form-item) {
    margin-bottom: 12px;
}

.search-form :deep(.el-form-item__label) {
    width: 80px;
    text-align: left;
}

.search-form :deep(.el-input) {
    width: 100%;
}

.button-group {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-top: 20px;
}

.mobile-list {
    margin-top: 15px;
}

.list-item {
    margin-bottom: 15px;
}

.item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.item-title {
    font-weight: bold;
    font-size: 1rem;
}

.item-content {
    margin: 10px 0;
}

.info-row {
    display: flex;
    margin-bottom: 8px;
    font-size: 0.9rem;
    line-height: 1.5;
}

.label {
    color: #666;
    width: 80px;
    flex-shrink: 0;
}

.item-footer {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid #eee;
}

.pagination-container {
    margin-top: 15px;
    display: flex;
    justify-content: center;
}

.pc-table {
    display: none;
}

@media screen and (min-width: 768px) {
    .questionnaire-review {
        padding: 20px;
    }

    h2 {
        font-size: 1.5rem;
        margin-bottom: 20px;
    }

    .content {
        padding: 20px;
    }

    .search-form {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
    }

    .search-form :deep(.el-form-item) {
        margin-bottom: 0;
    }

    .search-form :deep(.el-input) {
        width: 200px;
    }

    .search-form :deep(.el-select) {
        width: 200px !important;
    }

    .button-group {
        margin-top: 0;
    }

    .mobile-list {
        display: none;
    }

    .pc-table {
        display: block;
    }

    .pagination-container {
        justify-content: flex-end;
    }
}
</style> 