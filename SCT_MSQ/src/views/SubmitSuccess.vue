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
    background-color: transparent;
}

.success-content {
    background-color: var(--shell);
    padding: 40px;
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-soft), var(--glass-spec);
    width: 100%;
    max-width: 500px;
    margin: 20px;
    outline: 1px solid var(--shell-border);
    backdrop-filter: blur(2px) saturate(1.8);
    -webkit-backdrop-filter: blur(2px) saturate(1.8);
}

:deep(.el-result__icon) {
    margin-bottom: 20px;
}

:deep(.el-result__title) {
    margin-top: 20px;
    font-size: 24px;
    color: var(--sct-success);
}

:deep(.el-result__subtitle) {
    margin-top: 10px;
    font-size: 16px;
    color: var(--ink-500);
}
</style> 