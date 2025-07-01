<template>
    <div class="system-manage">
        <h2>系统配置管理</h2>
        <el-tabs v-model="activeTab" class="config-tabs">
            <el-tab-pane label="文本配置" name="text">
                <el-card class="config-card">
                    <el-table :data="textConfigs" style="width: 100%">
                        <el-table-column prop="configKey" label="标识" align="center" />
                        <el-table-column prop="configName" label="名称" align="center" />
                        <el-table-column label="值" align="center">
                            <template #default="scope">
                                <span>{{ scope.row.configValue ?? '未设置' }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" align="center" width="100">
                            <template #default="scope">
                                <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-tab-pane>
            <el-tab-pane label="图片配置" name="image">
                <el-card class="config-card">
                    <el-table :data="imageConfigs" style="width: 100%">
                        <el-table-column prop="configKey" label="标识" align="center" />
                        <el-table-column prop="configName" label="名称" align="center" />
                        <el-table-column label="图片" align="center">
                            <template #default="scope">
                                <el-image v-if="scope.row.configValue" :src="scope.row.configValue" style="width: 80px; height: 80px; border-radius: 4px" fit="cover" />
                                <span v-else style="color: #aaa">暂无图片</span>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" align="center" width="100">
                            <template #default="scope">
                                <el-button size="small" @click="openEditDialog(scope.row)">编辑</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-tab-pane>
            <el-tab-pane label="开关配置" name="switch">
                <el-card class="config-card">
                    <el-table :data="switchConfigs" style="width: 100%">
                        <el-table-column prop="configKey" label="标识" align="center" />
                        <el-table-column prop="configName" label="名称" align="center" />
                        <el-table-column label="状态" align="center">
                            <template #default="scope">
                                <el-switch v-model="scope.row.configValue" :active-value="'1'" :inactive-value="'0'" @change="handleSwitchChange(scope.row)" />
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-tab-pane>
        </el-tabs>
        <!-- 编辑弹窗 -->
        <el-dialog v-model="editDialogVisible" title="编辑配置" width="400px" destroy-on-close>
            <template v-if="editItem">
                <div class="edit-form">
                    <div class="edit-label">{{ editItem.configName }}</div>
                    <el-input v-if="editItem.type === 1" v-model="editValue" placeholder="请输入内容" />
                    <div v-else-if="editItem.type === 2">
                        <el-upload
                            class="upload-demo"
                            action="/api/upload/image"
                            list-type="picture-card"
                            :limit="1"
                            :file-list="imageFileList"
                            :on-success="handleImageUploadSuccess"
                            :on-remove="handleImageRemove"
                            :on-exceed="() => ElMessage.warning('只能上传一张图片')"
                            :before-upload="beforeImageUpload"
                        >
                            <el-icon><Plus /></el-icon>
                        </el-upload>
                    </div>
                </div>
            </template>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="editDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSave" :disabled="editItem == null">保存</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getConfigList, updateConfig } from '@/api/System'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const textConfigs = ref<any[]>([])
const imageConfigs = ref<any[]>([])
const switchConfigs = ref<any[]>([])
const activeTab = ref('text')

const editDialogVisible = ref(false)
const editItem = ref<any>(null)
const editValue = ref<any>(null)

const imageFileList = computed(() =>
  editValue.value ? [{ name: '图片', url: editValue.value }] : []
)

const fetchConfigs = async () => {
    try {
        const res = await getConfigList()
        const list = res.data || []
        textConfigs.value = list.filter((item: any) => item.type === 1)
        imageConfigs.value = list.filter((item: any) => item.type === 2)
        switchConfigs.value = list
            .filter((item: any) => item.type === 3)
            .map((item: any) => ({
                ...item,
                configValue: item.configValue === '1' ? '1' : '0'
            }))
    } catch (e) {
        ElMessage.error('获取配置失败')
    }
}

function openEditDialog(item: any) {
    editItem.value = item
    editValue.value = item.configValue
    editDialogVisible.value = true
}

function handleSave() {
    if (!editItem.value) return
    updateConfig({ id: editItem.value.id, configValue: editValue.value })
        .then(() => {
            editItem.value.configValue = editValue.value
            editDialogVisible.value = false
            ElMessage.success('修改成功')
        })
        .catch(() => {
            ElMessage.error('修改失败')
        })
}

// 图片上传相关
function handleImageUploadSuccess(response, file, fileList) {
  // 假设后端返回图片url在response.url 或 response.data
  const url = response.url || (response.data && response.data.url) || response.data
  editValue.value = url
  // 不自动保存，只做本地回显
}
function handleImageRemove(file, fileList) {
  editValue.value = ''
  // 不自动保存，只做本地回显
}
function beforeImageUpload(file: File) {
    const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
    const isLt2M = file.size / 1024 / 1024 < 20
    if (!isJPG) {
        ElMessage.error('只能上传 JPG/PNG 格式图片!')
    }
    if (!isLt2M) {
        ElMessage.error('图片大小不能超过 20MB!')
    }
    return isJPG && isLt2M
}

// 开关配置直接切换
function handleSwitchChange(row: any) {
    // value为字符串'1'或'0'
    const newValue = row.configValue
    updateConfig({ id: row.id, configValue: newValue })
        .then(() => {
            row.configValue = newValue
            ElMessage.success('修改成功')
        })
        .catch(() => {
            ElMessage.error('修改失败')
        })
}

onMounted(() => {
    fetchConfigs()
})
</script>

<style scoped>
.system-manage {
    padding: 20px;
}

h2 {
    margin-bottom: 20px;
    color: #333;
}

.config-tabs {
    width: 100%;
}

.config-card {
    margin-top: 20px;
}

.edit-form {
    display: flex;
    flex-direction: column;
    gap: 16px;
    margin: 20px 0;
}

.edit-label {
    font-weight: bold;
    margin-bottom: 8px;
}
</style> 