<template>
    <div class="main-container">
        <div class="mobile-menu-btn" 
            :class="{ 'mobile-menu-btn-scrolled': !showButton }" 
            v-if="isMobile && isCollapsed" 
            @click="toggleSidebar"
        >
            <!-- <el-icon class="menu-icon">
                <component :is="isCollapsed ? 'Expand' : 'Fold'" />
            </el-icon> -->
            <span v-if="showButton" class="menu-text">菜单</span>
            <span v-else>></span>
        </div>
        <div class="sidebar" :class="{ 'sidebar-collapsed': isCollapsed }">
            <div class="logo">
                <h1>SCT网站后台</h1>
            </div>
            <nav class="nav-menu">
                <router-link to="/admin/main/questionnaire-review" class="nav-item" @click="handleMenuClick">
                    <i class="el-icon-document-checked"></i>
                    <span v-show="!isCollapsed">问卷审核</span>
                </router-link>
                <router-link to="/admin/main/questionnaire-manage" class="nav-item" @click="handleMenuClick">
                    <i class="el-icon-document"></i>
                    <span v-show="!isCollapsed">问卷管理</span>
                </router-link>
                <router-link v-if="userInfo.id === 0" to="/admin/main/system-manage" class="nav-item" @click="handleMenuClick">
                    <i class="el-icon-picture"></i>
                    <span v-show="!isCollapsed">网站管理</span>
                </router-link>
                <router-link v-if="userInfo.id === 0" to="/admin/main/admin-manage" class="nav-item" @click="handleMenuClick">
                    <i class="el-icon-user"></i>
                    <span v-show="!isCollapsed">账号管理</span>
                </router-link>
                <router-link to="/admin/main/server-member-manage" class="nav-item" @click="handleMenuClick">
                    <i class="el-icon-user-filled"></i>
                    <span v-show="!isCollapsed">成员管理</span>
                </router-link>
            </nav>
        </div>
        <div class="main-content" :class="{ 'main-content-expanded': isCollapsed }" @click="handleMainContentClick">
            <div class="user-info">
                <el-dropdown trigger="click" @command="handleCommand">
                    <div class="user-dropdown-link">
                        <el-avatar :size="32" :src="userInfo.avatar || ''">
                            {{ userInfo.username ? userInfo.username.charAt(0) : 'U' }}
                        </el-avatar>
                        <span class="username">{{ userInfo.username || '未登录' }}</span>
                        <el-icon class="el-icon--right"><arrow-down /></el-icon>
                    </div>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
                            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
            <router-view></router-view>
        </div>
        <div class="sidebar-mask" v-if="isMobile && !isCollapsed" @click="toggleSidebar"></div>

        <!-- 修改密码弹窗 -->
        <el-dialog
            v-model="passwordDialogVisible"
            title="修改密码"
            width="400px"
            destroy-on-close
        >
            <el-form :model="passwordForm" label-width="100px" :rules="passwordRules" ref="passwordFormRef">
                <el-form-item label="原密码" prop="oldPassword">
                    <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                    <el-input v-model="passwordForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认新密码" prop="confirmPassword">
                    <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="passwordDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleChangePassword">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Expand, Fold, ArrowDown } from '@element-plus/icons-vue'
import { ElDropdown, ElDropdownMenu, ElDropdownItem, ElAvatar, ElIcon, ElMessageBox, ElMessage } from 'element-plus'
import { useTokenStore } from '@/stores/token.js';
import useUserInfoStore from '@/stores/userInfo'
import { useRouter } from 'vue-router'
import type { FormInstance } from 'element-plus'

interface UserInfo {
    username?: string;
    avatar?: string;
    id?: number;
}

interface PasswordForm {
    oldPassword: string;
    newPassword: string;
    confirmPassword: string;
}

const store = useTokenStore()
const router = useRouter()
const userInfoStore = useUserInfoStore()
const userInfo = userInfoStore.info as UserInfo

const isCollapsed = ref(false)
const isMobile = ref(false)
const showButton = ref(true)
let lastScrollTop = 0

// 修改密码相关
const passwordDialogVisible = ref(false)
const passwordFormRef = ref<FormInstance>()
const passwordForm = ref<PasswordForm>({
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
    if (value === '') {
        callback(new Error('请再次输入新密码'))
    } else if (value !== passwordForm.value.newPassword) {
        callback(new Error('两次输入的密码不一致'))
    } else {
        callback()
    }
}

const passwordRules = {
    oldPassword: [
        { required: true, message: '请输入原密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
    ],
    newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请再次输入新密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
    ]
}

import { changePassword } from '@/api/Auth'
const handleChangePassword = async () => {
    if (!passwordFormRef.value) return
    
    await passwordFormRef.value.validate(async (valid) => {
        if (valid) {
            try {
                const updatePasswordDTO = {
                    oldPassword: passwordForm.value.oldPassword,
                    newPassword: passwordForm.value.newPassword
                }
                await changePassword(updatePasswordDTO)
                ElMessage.success('密码修改成功')
                passwordDialogVisible.value = false
                // 清空表单
                passwordForm.value = {
                    oldPassword: '',
                    newPassword: '',
                    confirmPassword: ''
                }
            } catch (error) {
                // ElMessage.error('密码修改失败')
            }
        }
    })
}

const checkMobile = () => {
    isMobile.value = window.innerWidth <= 768
    if (isMobile.value) {
        isCollapsed.value = true
    }
}

const toggleSidebar = () => {
    isCollapsed.value = !isCollapsed.value
}

const handleScroll = () => {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop
    if (scrollTop > lastScrollTop && scrollTop > 100) {
        // 向下滚动且超过100px时隐藏按钮
        showButton.value = false
    } else {
        // 向上滚动或回到顶部时显示按钮
        showButton.value = true
    }
    lastScrollTop = scrollTop
}

const handleMainContentClick = () => {
    if (isMobile.value && !isCollapsed.value) {
        isCollapsed.value = true
    }
}

const handleMenuClick = () => {
    if (isMobile.value) {
        isCollapsed.value = true
    }
}

const handleCommand = (command: string) => {
    switch (command) {
        case 'changePassword':
            passwordDialogVisible.value = true
            break
        case 'logout':
            ElMessageBox.confirm(
                '确定要退出登录吗？',
                '提示',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            ).then(() => {
                store.removeToken()
                userInfoStore.removeInfo()
                router.push('/admin/login')
            }).catch(() => {})
            break
    }
}

function getServerShortName() {
    const CACHE_KEY = 'navbar_config_cache'
    const cacheStr = localStorage.getItem(CACHE_KEY)
    if (cacheStr) {
        try {
            const cache = JSON.parse(cacheStr)
            if (cache.data) {
                const item = cache.data.find(item => item.configKey === 'server_short_name')
                if (item && item.configValue) return item.configValue
            }
        } catch (e) {}
    }
    return 'SCT'
}

onMounted(() => {
    document.title = getServerShortName() + '后台'
    checkMobile()
    window.addEventListener('resize', checkMobile)
    window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
    window.removeEventListener('resize', checkMobile)
    window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.main-container {
    display: flex;
    min-height: 100vh;
}

.sidebar {
    width: 240px;
    background-color: #304156;
    color: #fff;
    padding: 20px 0;
    transition: all 0.3s;
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 1000;
    box-shadow: 2px 0 8px rgba(0,0,0,.08);
}

.sidebar-collapsed {
    width: 64px;
}

.logo {
    padding: 0 20px;
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.logo h1 {
    color: #fff;
    font-size: 20px;
    margin: 0;
    white-space: nowrap;
    overflow: hidden;
}

.collapse-btn {
    background: transparent;
    border: none;
    color: #fff;
    padding: 0;
    font-size: 20px;
}

.nav-menu {
    display: flex;
    flex-direction: column;
}

.nav-item {
    padding: 12px 20px;
    color: #bfcbd9;
    text-decoration: none;
    display: flex;
    align-items: center;
    transition: all 0.3s;
    white-space: nowrap;
}

.nav-item:hover {
    color: #fff;
    background-color: #263445;
}

.nav-item.router-link-active {
    color: #409EFF;
    background-color: #263445;
}

.nav-item i {
    margin-right: 8px;
    font-size: 18px;
}

.main-content {
    flex: 1;
    padding: 20px;
    background-color: #f0f2f5;
    transition: all 0.3s;
    margin-left: 240px;
}

.main-content-expanded {
    margin-left: 64px;
}

.mobile-menu-btn {
    position: fixed;
    top: 10px;
    left: 10px;
    z-index: 1001;
    background-color: #304156;
    color: #fff;
    height: 40px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    transition: all 0.3s ease;
    padding: 0 15px;
}

.mobile-menu-btn-scrolled {
    width: 20px;
    padding: 0;
    left: 0;
    border-radius: 0 4px 4px 0;
    background-color: rgba(48, 65, 86, 0.9);
}

.menu-icon {
    font-size: 20px;
    color: white !important;
}

.menu-text {
    font-size: 14px;
}

.sidebar-mask {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 999;
}

.user-info {
    position: absolute;
    top: 20px;
    right: 20px;
    display: flex;
    align-items: center;
    gap: 8px;
    z-index: 1000;
}

.user-dropdown-link {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 4px;
    transition: background-color 0.3s;
}

.user-dropdown-link:hover {
    background-color: rgba(0, 0, 0, 0.04);
}

.username {
    color: #606266;
    font-size: 14px;
}

@media screen and (max-width: 768px) {
    .sidebar {
        position: fixed;
        height: 100vh;
        z-index: 1000;
        transform: translateX(0);
        box-shadow: 2px 0 8px rgba(0,0,0,.15);
    }
    .sidebar-collapsed {
        transform: translateX(-100%);
    }
    .main-content {
        margin-left: 0 !important;
        padding-top: 60px;
    }
}
</style>