<template>
    <div class="exhibition-manage">
        <h2>展览管理</h2>

        <el-card class="exhibition-card">
            <template #header>
                <div class="card-header">
                    <span>机器展览区 / 建筑展览区 / 其他内容</span>
                    <div class="header-actions">
                        <div class="search-bar">
                            <el-input
                                v-model="query.keyword"
                                placeholder="标题 / 正文"
                                clearable
                                class="search-input"
                                @keyup.enter="handleSearch"
                            />
                            <el-select v-model="query.category" placeholder="全部分类" clearable class="category-select">
                                <el-option label="全部分类" :value="null" />
                                <el-option
                                    v-for="opt in categoryOptions"
                                    :key="opt.value"
                                    :label="`${opt.label}（${opt.section}）`"
                                    :value="opt.value"
                                />
                            </el-select>
                            <el-select v-model="query.status" placeholder="全部状态" clearable class="status-select">
                                <el-option label="全部状态" :value="null" />
                                <el-option label="正常" :value="1" />
                                <el-option label="已隐藏" :value="0" />
                            </el-select>
                            <el-button type="primary" @click="handleSearch">查询</el-button>
                            <el-button @click="handleReset">重置</el-button>
                        </div>
                        <el-button type="primary" plain @click="handleOpenCreate">
                            <el-icon><Plus /></el-icon><span>新增展览</span>
                        </el-button>
                    </div>
                </div>
            </template>

            <!-- PC端表格 -->
            <el-table :data="list" v-loading="loading" class="pc-table">
                <el-table-column label="封面" width="100" align="center">
                    <template #default="scope">
                        <el-image
                            v-if="displayCover(scope.row)"
                            :src="displayCover(scope.row)"
                            class="cover-thumb"
                            fit="cover"
                            :preview-src-list="[displayCover(scope.row)]"
                            preview-teleported
                        />
                        <span v-else class="muted">—</span>
                    </template>
                </el-table-column>
                <el-table-column prop="title" label="标题" min-width="160" align="left">
                    <template #default="scope">
                        <span class="title-text" :title="scope.row.title">{{ scope.row.title }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="分类" width="130" align="center">
                    <template #default="scope">
                        <el-tag :type="categoryTagType(scope.row.category)" size="small" effect="light">
                            {{ categoryLabel(scope.row.category) }}
                        </el-tag>
                        <div class="sub-text">{{ categorySection(scope.row.category) }}</div>
                    </template>
                </el-table-column>
                <el-table-column label="图片" width="80" align="center">
                    <template #default="scope">
                        <span>{{ (scope.row.images || []).length }} 张</span>
                    </template>
                </el-table-column>
                <el-table-column label="置顶" width="80" align="center">
                    <template #default="scope">
                        <el-tag v-if="scope.row.top === 1" type="warning" size="small">已置顶</el-tag>
                        <span v-else class="muted">—</span>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="80" align="center">
                    <template #default="scope">
                        <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
                            {{ scope.row.status === 1 ? '正常' : '已隐藏' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="140" align="center" />
                <el-table-column label="操作" width="300" align="center">
                    <template #default="scope">
                        <el-button type="primary" size="small" plain @click="handleOpenEdit(scope.row)">编辑</el-button>
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
                <div v-for="item in list" :key="item.id" class="mobile-item">
                    <div class="item-header">
                        <img v-if="displayCover(item)" :src="displayCover(item)" class="mobile-cover" />
                        <div class="item-meta">
                            <span class="item-title">{{ item.title }}</span>
                            <span class="item-sub">{{ categoryLabel(item.category) }} · {{ (item.images || []).length }} 张图</span>
                        </div>
                        <div class="tags">
                            <el-tag v-if="item.top === 1" type="warning" size="small">置顶</el-tag>
                            <el-tag :type="item.status === 1 ? 'success' : 'info'" size="small">
                                {{ item.status === 1 ? '正常' : '已隐藏' }}
                            </el-tag>
                        </div>
                    </div>
                    <div class="item-footer">
                        <span class="time">{{ item.createTime }}</span>
                        <div class="actions">
                            <el-button size="small" @click="handleOpenEdit(item)">编辑</el-button>
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
                <el-empty v-if="!loading && !list.length" description="暂无展览内容" />
            </div>

            <div class="pagination-container" v-if="total > query.pageSize">
                <el-pagination
                    v-model:current-page="query.pageNo"
                    v-model:page-size="query.pageSize"
                    :page-sizes="[10, 20, 50, 100]"
                    :total="total"
                    :layout="isMobile ? 'total, prev, pager, next' : 'total, sizes, prev, pager, next, jumper'"
                    @size-change="handleSizeChange"
                    @current-change="getList"
                />
            </div>
        </el-card>

        <!-- 新增 / 编辑弹窗 -->
        <el-dialog
            v-model="dialogVisible"
            :title="isEdit ? '编辑展览' : '新增展览'"
            :width="isMobile ? '94%' : '720px'"
            top="6vh"
            destroy-on-close
            @closed="resetForm"
        >
            <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
                <el-form-item label="标题" prop="title">
                    <el-input
                        v-model="form.title"
                        placeholder="请输入标题"
                        maxlength="128"
                        show-word-limit
                    />
                </el-form-item>

                <el-form-item label="分类" prop="category">
                    <el-radio-group v-model="form.category">
                        <el-radio
                            v-for="opt in categoryOptions"
                            :key="opt.value"
                            :value="opt.value"
                        >{{ opt.label }}（{{ opt.section }}）</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="封面">
                    <el-upload
                        class="upload-demo"
                        action="/api/upload/image"
                        list-type="picture-card"
                        :limit="1"
                        :file-list="coverFileList"
                        :on-success="handleCoverSuccess"
                        :on-remove="handleCoverRemove"
                        :on-exceed="() => ElMessage.warning('只能上传一张封面')"
                        :before-upload="beforeImageUpload"
                    >
                        <el-icon><Plus /></el-icon>
                    </el-upload>
                    <div class="option-tips">
                        <el-text type="info" size="small">选填；不传时自动取第一张图片作为封面</el-text>
                    </div>
                </el-form-item>

                <el-form-item label="图片" prop="images">
                    <el-upload
                        class="upload-demo"
                        action="/api/upload/image"
                        list-type="picture-card"
                        :limit="imageMaxCount"
                        :file-list="imageFileList"
                        :on-success="handleImagesSuccess"
                        :on-remove="handleImagesRemove"
                        :on-exceed="() => ElMessage.warning(`最多上传${imageMaxCount}张图片`)"
                        :before-upload="beforeImageUpload"
                        multiple
                    >
                        <el-icon><Plus /></el-icon>
                    </el-upload>
                    <div class="option-tips">
                        <el-text type="info" size="small">
                            至少 1 张，最多 {{ imageMaxCount }} 张；按上传顺序展示，第一张默认作为封面
                        </el-text>
                    </div>
                </el-form-item>

                <el-form-item label="正文" prop="content">
                    <el-input
                        v-model="form.content"
                        type="textarea"
                        :rows="8"
                        maxlength="5000"
                        show-word-limit
                        placeholder="请输入正文，换行会被保留"
                    />
                </el-form-item>

                <el-form-item label="置顶">
                    <el-switch v-model="form.top" :active-value="1" :inactive-value="0" />
                </el-form-item>

                <el-form-item label="状态">
                    <el-radio-group v-model="form.status">
                        <el-radio :value="1">正常</el-radio>
                        <el-radio :value="0">隐藏</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" :loading="saving" @click="handleSubmit">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useLatestRequest } from '@/composables/useLatestRequest'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
    fetchExhibitionPage,
    saveExhibition,
    updateExhibition,
    removeExhibition
} from '@/api/AdminExhibition'
import {
    EXHIBITION_CATEGORY_OPTIONS,
    categoryLabel,
    categorySection,
    categoryTagType
} from '@/constants/exhibition'

const categoryOptions = EXHIBITION_CATEGORY_OPTIONS
const imageMaxCount = 30

/** 列表里展示用的封面：没单独设封面时取第一张图 */
const displayCover = (row) => row.cover || (row.images || [])[0] || ''

const loading = ref(false)
const listRequest = useLatestRequest()
const list = ref([])
const total = ref(0)

const query = ref({
    pageNo: 1,
    pageSize: 20,
    keyword: '',
    category: null,
    status: null
})

const isMobile = ref(window.innerWidth <= 768)
const handleResize = () => {
    isMobile.value = window.innerWidth <= 768
}

const getList = async () => {
    const request = listRequest.start()
    loading.value = true
    try {
        const res = await fetchExhibitionPage({
            pageNo: query.value.pageNo,
            pageSize: query.value.pageSize,
            keyword: query.value.keyword || undefined,
            category: query.value.category || undefined,
            status: query.value.status === null || query.value.status === '' ? undefined : query.value.status
        }, { signal: request.signal })
        if (!request.isCurrent()) return
        list.value = res.data.list || []
        total.value = res.data.total || 0
    } catch (error) {
        // 拦截器已提示
    } finally {
        if (request.isCurrent()) loading.value = false
    }
}

const handleSearch = () => {
    query.value.pageNo = 1
    getList()
}

const handleReset = () => {
    query.value.keyword = ''
    query.value.category = null
    query.value.status = null
    query.value.pageNo = 1
    getList()
}

const handleSizeChange = (val) => {
    query.value.pageSize = val
    query.value.pageNo = 1
    getList()
}

/* ---------------- 新增 / 编辑 ---------------- */
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const formRef = ref()
const editingId = ref(null)

const imageFileList = ref([])

const defaultForm = () => ({
    title: '',
    category: 'redstone',
    cover: '',
    content: '',
    top: 0,
    status: 1
})

const form = ref(defaultForm())

const coverFileList = computed(() =>
    form.value.cover ? [{ name: '封面', url: form.value.cover }] : []
)

const rules = {
    title: [
        { required: true, message: '请输入标题', trigger: 'blur' },
        { max: 128, message: '标题不能超过128个字符', trigger: 'blur' }
    ],
    category: [
        { required: true, message: '请选择分类', trigger: 'change' }
    ],
    images: [
        {
            validator: (rule, value, callback) => {
                if (!imageFileList.value.length) {
                    callback(new Error('请至少上传一张图片'))
                    return
                }
                callback()
            },
            trigger: 'change'
        }
    ],
    content: [
        { required: true, message: '请输入正文', trigger: 'blur' },
        { max: 5000, message: '正文不能超过5000个字', trigger: 'blur' }
    ]
}

const beforeImageUpload = (file) => {
    const allowed = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp', 'image/gif', 'image/bmp']
    const isImage = allowed.includes(file.type)
    const isLt20M = file.size / 1024 / 1024 < 20
    if (!isImage) {
        ElMessage.error('只能上传 JPG / PNG / WEBP / GIF / BMP 格式图片!')
    }
    if (!isLt20M) {
        ElMessage.error('图片大小不能超过 20MB!')
    }
    return isImage && isLt20M
}

/* 封面 */
const handleCoverSuccess = (response) => {
    form.value.cover = response?.data || ''
}
const handleCoverRemove = () => {
    form.value.cover = ''
}

/* 图片列表 */
const normalizeFileList = (fileList) =>
    fileList
        .map((f) => ({ name: f.name, url: f.url || (f.response && f.response.data) || '' }))
        .filter((f) => f.url)

const handleImagesSuccess = (response, file, fileList) => {
    file.url = response?.data || ''
    imageFileList.value = normalizeFileList(fileList)
    formRef.value?.validateField('images')
}

const handleImagesRemove = (file, fileList) => {
    imageFileList.value = normalizeFileList(fileList)
    formRef.value?.validateField('images')
}

const resetForm = () => {
    form.value = defaultForm()
    imageFileList.value = []
    editingId.value = null
    isEdit.value = false
    formRef.value?.clearValidate()
}

const handleOpenCreate = () => {
    resetForm()
    dialogVisible.value = true
}

const handleOpenEdit = (row) => {
    resetForm()
    isEdit.value = true
    editingId.value = row.id
    form.value = {
        title: row.title || '',
        category: row.category || 'redstone',
        cover: row.cover || '',
        content: row.content || '',
        top: row.top === 1 ? 1 : 0,
        status: row.status === 1 ? 1 : 0
    }
    imageFileList.value = (row.images || []).map((url, i) => ({ name: `图片${i + 1}`, url }))
    dialogVisible.value = true
}

const handleSubmit = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (!valid) return
        saving.value = true
        try {
            const payload = {
                title: form.value.title,
                category: form.value.category,
                cover: form.value.cover || null,
                images: imageFileList.value.map((f) => f.url),
                content: form.value.content,
                top: form.value.top,
                status: form.value.status
            }
            if (isEdit.value) {
                await updateExhibition({ ...payload, id: editingId.value })
                ElMessage.success('修改成功')
            } else {
                await saveExhibition(payload)
                ElMessage.success('新增成功')
                query.value.pageNo = 1
            }
            dialogVisible.value = false
            getList()
        } catch (error) {
            // 拦截器已提示
        } finally {
            saving.value = false
        }
    })
}

/* ---------------- 快捷操作 ---------------- */
const handleToggleTop = async (row) => {
    const nextTop = row.top === 1 ? 0 : 1
    try {
        await updateExhibition({ id: row.id, top: nextTop })
        ElMessage.success(nextTop === 1 ? '已置顶' : '已取消置顶')
        getList()
    } catch (error) {}
}

const handleToggleStatus = async (row) => {
    const nextStatus = row.status === 1 ? 0 : 1
    try {
        await updateExhibition({ id: row.id, status: nextStatus })
        ElMessage.success(nextStatus === 1 ? '已显示' : '已隐藏')
        getList()
    } catch (error) {}
}

const handleRemove = (row) => {
    ElMessageBox.confirm(
        `确定删除「${row.title}」吗？`,
        '提示',
        { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    ).then(async () => {
        try {
            await removeExhibition(row.id)
            ElMessage.success('删除成功')
            // 删掉当前页最后一条时往前翻一页
            if (list.value.length === 1 && query.value.pageNo > 1) {
                query.value.pageNo -= 1
            }
            getList()
        } catch (error) {}
    }).catch(() => {})
}

onMounted(() => {
    getList()
    window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.exhibition-manage {
    padding: 20px;
}

h2 {
    margin-bottom: 20px;
    color: var(--ink-900);
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
    width: 200px;
}

.category-select {
    width: 170px;
}

.status-select {
    width: 130px;
}

.cover-thumb {
    width: 64px;
    height: 48px;
    border-radius: var(--radius-md);
    background: var(--count-bg);
    cursor: zoom-in;
}

/* 操作列按钮挨紧一点，避免换行 */
:deep(.pc-table .el-table__cell .cell > .el-button + .el-button) {
    margin-left: 6px;
}

.title-text {
    display: inline-block;
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    vertical-align: middle;
}

.sub-text {
    margin-top: 2px;
    font-size: 0.7rem;
    color: var(--ink-500);
}

.muted {
    color: var(--ink-300);
}

.option-tips {
    width: 100%;
    margin-top: 4px;
    line-height: 1.4;
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
    .exhibition-manage {
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
        background: var(--shell-strong);
    }

    .mobile-item {
        padding: 10px 8px;
        border-bottom: 1px solid var(--hair);
    }

    .mobile-item:last-child {
        border-bottom: none;
    }

    .item-header {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;
    }

    .mobile-cover {
        width: 52px;
        height: 40px;
        border-radius: var(--radius-md);
        object-fit: cover;
        flex-shrink: 0;
        background: var(--count-bg);
    }

    .item-meta {
        display: flex;
        flex-direction: column;
        gap: 2px;
        min-width: 0;
        flex: 1;
    }

    .item-title {
        font-size: 0.85rem;
        color: var(--ink-900);
        font-weight: 500;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .item-sub {
        font-size: 0.72rem;
        color: var(--ink-500);
    }

    .tags {
        display: flex;
        gap: 4px;
        flex-shrink: 0;
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
        color: var(--ink-500);
    }

    .actions {
        display: flex;
        gap: 6px;
        flex-wrap: wrap;
    }

    .actions .el-button--small {
        padding: 3px 8px;
        font-size: 0.72rem;
        height: 24px;
        line-height: 1;
        margin-left: 0;
    }

    .search-input,
    .category-select,
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
