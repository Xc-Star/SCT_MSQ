<template>
    <div class="submit-success">
        <div class="success-content">
            <el-result
                icon="success"
                title="提交成功"
                sub-title="您已成功提交问卷，请耐心等待管理员审核。"
            >
                <template #extra>
                    <el-button type="primary" @click="goToHome">返回首页</el-button>
                </template>
            </el-result>
        </div>
    </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goToHome = () => {
    router.push('/')
}
</script>

<style scoped>
.submit-success {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f5f7fa;
}

.success-content {
    background-color: #fff;
    padding: 40px;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 500px;
    margin: 20px;
}

:deep(.el-result__icon) {
    margin-bottom: 20px;
}

:deep(.el-result__title) {
    margin-top: 20px;
    font-size: 24px;
    color: #67c23a;
}

:deep(.el-result__subtitle) {
    margin-top: 10px;
    font-size: 16px;
    color: #606266;
}
</style> 