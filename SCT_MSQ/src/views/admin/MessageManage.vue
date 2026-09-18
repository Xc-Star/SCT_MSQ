<template>
    <div class="message-manage">
        <h2>留言管理</h2>

        <el-card class="message-card">
            <template #header>
                <div class="card-header">
                    <span>成员留言</span>
                    <div class="header-actions">
                        <div class="search-bar">
                            <el-input
                                v-model="query.keyword"
                                placeholder="玩家ID / 留言内容 / QQ号"
                                clearable
                                class="search-input"
                                @keyup.enter="handleSearch"
                            />
                            <el-select v-model="query.status" placeholder="全部状态" clearable class="status-select">
                                <el-option label="全部状态" :value="null" />
                                <el-option label="正常" :value="1" />
                                <el-option label="已隐藏" :value="0" />
                            </el-select>
                            <el-button type="primary" @click="handleSearch">查询</el-button>
                            <el-button @click="handleReset">重置</el-button>
                        </div>
                        <el-button type="primary" plain @click="handleOpenCreate">
                            <el-icon><Plus /></el-icon><span>新增留言</span>
                        </el-button>
                    </div>
                </div>
            </template>

            <!-- PC端表格 -->
            <el-table :data="messageList" v-loading="loading" class="pc-table">
                <el-table-column prop="avatar" label="头像" width="70" align="center">
                    <template #default="scope">
                        <img :src="scope.row.avatar" class="avatar" />
                    </template>
                </el-table-column>
                <el-table-column prop="playerId" label="玩家ID" width="140" align="center" />
                <el-table-column prop="qq" label="QQ号" width="130" align="center" />
                <el-table-column prop="content" label="留言内容" align="left" min-width="200">
                    <template #default="scope">
                        <span class="content-text" :title="scope.row.content">{{ scope.row.content }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="置顶" width="90" align="center">
                    <template #default="scope">
                        <el-tag v-if="scope.row.top === 1" type="warning" size="small">已置顶</el-tag>
                        <span v-else class="muted">—</span>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="90" align="center">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
                            {{ scope.row.status === 1 ? '正常' : '已隐藏' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="150" align="center" />
                <el-table-column label="操作" width="230" align="center">
                    <template #default="scope">
                        <el-button
                            :type="scope.row.top === 1 ? 'info' : 'warning'"
                            size="small"
                            @click="handleToggleTop(scope.row)"
                        >
                            {{ scope.row.top === 1 ? '取消置顶' : '置顶' }}
                        </el-button>
                        <el-button
                            :type="scope.row.status === 1 ? 'info' : 'success'"
                            size="small"
                            @click="handleToggleStatus(scope.row)"
                        >
                            {{ scope.row.status === 1 ? '隐藏' : '显示' }}
                        </el-button>
                        <el-button type="danger" size="small" @click="handleRemove(scope.row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 移动端列表 -->
            <div class="mobile-list" v-loading="loading">
                <div v-for="item in messageList" :key="item.id" class="mobile-item">
                    <div class="item-header">
                        <div class="user-info">
                            <img :src="item.avatar" class="avatar" />
                            <div class="user-meta">
                                <span class="user-id">{{ item.playerId }}</span>
                                <span class="user-qq">QQ：{{ item.qq || '—' }}</span>
                            </div>
                        </div>
                        <div class="tags">
                            <el-tag v-if="item.top === 1" type="warning" size="small">置顶</el-tag>
                            <el-tag :type="item.status === 1 ? 'success' : 'info'" size="small">
                                {{ item.status === 1 ? '正常' : '已隐藏' }}
                            </el-tag>
                        </div>
                    </div>
                    <div class="item-content">{{ item.content }}</div>
                    <div class="item-footer">
                        <span class="time">{{ item.createTime }}</span>
                        <div class="actions">
                            <el-button size="small" @click="handleToggleTop(item)">
                                {{ item.top === 1 ? '取消置顶' : '置顶' }}
                            </el-button>
                            <el-button size="small" @click="handleToggleStatus(item)">
                                {{ item.status === 1 ? '隐藏' : '显示' }}
                            </el-button>
                            <el-button type="danger" size="small" @click="handleRemove(item)">删除</el-button>
                        </div>
                    </div>
                </div>
                <el-empty v-if="!loading && !messageList.length" description="暂无留言" />
            </div>

            <div class="pagination-container" v-if="total > pageSize">
                <el-pagination
                    v-model:current-page="query.pageNo"
                    v-model:page-size="query.pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="total"
                    :layout="isMobile ? 'total, prev, pager, next' : 'total, sizes, prev, pager, next, jumper'"
                    @size-change="handleSizeChange"
                    @current-change="getMessageList"
                />
            </div>
        </el-card>

        <!-- 新增留言弹窗 -->
        <el-dialog
            v-model="createVisible"
            title="新增留言"
            :width="isMobile ? '92%' : '520px'"
            destroy-on-close
            @closed="resetCreateForm"
        >
            <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="80px">
                <el-form-item label="玩家ID" prop="playerId">
                    <el-input
                        v-model="createForm.playerId"
                        placeholder="游戏内ID，用于展示名字和头像"
                        maxlength="64"
                        show-word-limit
                    />
                </el-form-item>
                <el-form-item label="QQ号" prop="qq">
                    <el-input v-model="createForm.qq" placeholder="选填，用于对接机器人插件" maxlength="32" />
                </el-form-item>
                <el-form-item label="留言内容" prop="content">
                    <el-input
                        v-model="createForm.content"
                        type="textarea"
                        :rows="4"
                        maxlength="500"
                        show-word-limit
                        placeholder="请输入留言内容"
                    />
                </el-form-item>
                <el-form-item label="置顶">
                    <el-switch v-model="createForm.top" :active-value="1" :inactive-value="0" />
                </el-form-item>
                <el-form-item label="状态">
                    <el-radio-group v-model="createForm.status">
                        <el-radio :value="1">正常</el-radio>
                        <el-radio :value="0">隐藏</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="createVisible = false">取消</el-button>
                    <el-button type="primary" :loading="saving" @click="handleCreateSubmit">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { fetchMessagePage, saveMessage, updateMessage, removeMessage } from '@/api/AdminMemberMessage'

const loading = ref(false)
const messageList = ref([])
const total = ref(0)

const query = ref({
    pageNo: 1,
    pageSize: 20,
    keyword: '',
    status: null
})

const isMobile = ref(window.innerWidth <= 768)
const handleResize = () => {
    isMobile.value = window.innerWidth <= 768
}

const getMessageList = async () => {
    loading.value = true
    try {
        const res = await fetchMessagePage({
            pageNo: query.value.pageNo,
            pageSize: query.value.pageSize,
            keyword: query.value.keyword || undefined,
            status: query.value.status === null || query.value.status === '' ? undefined : query.value.status
        })
        messageList.value = res.data.list || []
        total.value = res.data.total || 0
    } catch (error) {
        // 拦截器已提示
    } finally {
        loading.value = false
    }
}

const handleSearch = () => {
    query.value.pageNo = 1
    getMessageList()
}

const handleReset = () => {
    query.value.keyword = ''
    query.value.status = null
    query.value.pageNo = 1
    getMessageList()
}

/* ---------------- 新增留言 ---------------- */
const createVisible = ref(false)
const saving = ref(false)
const createFormRef = ref()

const createForm = ref({
    playerId: '',
    qq: '',
    content: '',
    top: 0,
    status: 1
})

const createRules = {
    playerId: [
        { required: true, message: '请输入玩家ID', trigger: 'blur' },
        { max: 64, message: '玩家ID不能超过64个字符', trigger: 'blur' }
    ],
    qq: [
        { max: 32, message: 'QQ号不能超过32个字符', trigger: 'blur' }
    ],
    content: [
        { required: true, message: '请输入留言内容', trigger: 'blur' },
        { max: 500, message: '留言内容不能超过500个字', trigger: 'blur' }
    ]
}

const resetCreateForm = () => {
    createForm.value = {
        playerId: '',
        qq: '',
        content: '',
        top: 0,
        status: 1
    }
    createFormRef.value?.clearValidate()
}

const handleOpenCreate = () => {
    resetCreateForm()
    createVisible.value = true
}

const handleCreateSubmit = async () => {
    if (!createFormRef.value) return
    await createFormRef.value.validate(async (valid) => {
        if (!valid) return
        saving.value = true
        try {
            await saveMessage({
                playerId: createForm.value.playerId,
                qq: createForm.value.qq || null,
                content: createForm.value.content,
                top: createForm.value.top,
                status: createForm.value.status
            })
            ElMessage.success('新增成功')
            createVisible.value = false
            query.value.pageNo = 1
            getMessageList()
        } catch (error) {
            // 拦截器已提示
        } finally {
            saving.value = false
        }
    })
}

const handleSizeChange = (val) => {
    query.value.pageSize = val
    query.value.pageNo = 1
    getMessageList()
}

const handleToggleTop = async (row) => {
    const nextTop = row.top === 1 ? 0 : 1
    try {
        await updateMessage({ id: row.id, top: nextTop })
        ElMessage.success(nextTop === 1 ? '已置顶' : '已取消置顶')
        getMessageList()
    } catch (error) {}
}

const handleToggleStatus = async (row) => {
    const nextStatus = row.status === 1 ? 0 : 1
    try {
        await updateMessage({ id: row.id, status: nextStatus })
        ElMessage.success(nextStatus === 1 ? '已显示' : '已隐藏')
        getMessageList()
    } catch (error) {}
}

const handleRemove = (row) => {
    ElMessageBox.confirm(
        `确定删除「${row.playerId}」的这条留言吗？`,
        '提示',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    ).then(async () => {
        try {
            await removeMessage(row.id)
            ElMessage.success('删除成功')
            // 删掉当前页最后一条时往前翻一页
            if (messageList.value.length === 1 && query.value.pageNo > 1) {
                query.value.pageNo -= 1
            }
            getMessageList()
        } catch (error) {}
    }).catch(() => {})
}

onMounted(() => {
    getMessageList()
    window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.message-manage {
    padding: 20px;
}

h2 {
    margin-bottom: 20px;
    color: #333;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
}

.search-bar {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
}

.header-actions {
    display: flex;
    align-items: center;
    gap: 12px;
    flex-wrap: wrap;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.search-input {
    width: 240px;
}

.status-select {
    width: 130px;
}

.avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: #f2f2f2;
}

.content-text {
    display: inline-block;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}

.muted {
    color: #c0c4cc;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

/* PC 端隐藏移动列表 */
@media screen and (min-width: 769px) {
    .mobile-list {
        display: none;
    }
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
    .message-manage {
        padding: 0;
    }

    h2 {
        font-size: 1.1rem;
        margin: 8px;
    }

    .pc-table {
        display: none;
    }

    .mobile-list {
        display: block;
        background: #fff;
    }

    .mobile-item {
        padding: 10px 8px;
        border-bottom: 1px solid #eee;
    }

    .mobile-item:last-child {
        border-bottom: none;
    }

    .item-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 6px;
    }

    .user-info {
        display: flex;
        align-items: center;
        gap: 8px;
    }

    .user-meta {
        display: flex;
        flex-direction: column;
        line-height: 1.3;
    }

    .user-id {
        font-size: 0.85rem;
        color: #333;
        font-weight: 500;
    }

    .user-qq {
        font-size: 0.72rem;
        color: #909399;
    }

    .tags {
        display: flex;
        gap: 4px;
    }

    .item-content {
        font-size: 0.85rem;
        color: #555;
        line-height: 1.5;
        margin-bottom: 8px;
        word-break: break-all;
    }

    .item-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 8px;
        flex-wrap: wrap;
    }

    .time {
        font-size: 0.72rem;
        color: #909399;
    }

    .actions {
        display: flex;
        gap: 6px;
    }

    .actions .el-button--small {
        padding: 3px 8px;
        font-size: 0.72rem;
        height: 24px;
        line-height: 1;
        margin-left: 0;
    }

    .search-input,
    .status-select {
        width: 100%;
    }

    .header-actions {
        width: 100%;
        flex-direction: column;
        align-items: stretch;
    }

    .pagination-container {
        margin: 8px;
    }
}
</style>
