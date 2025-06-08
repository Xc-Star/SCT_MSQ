<template>
    <div class="main-container">
        <div class="mobile-menu-btn" 
            :class="{ 'mobile-menu-btn-scrolled': !showButton }" 
            v-if="isMobile && isCollapsed" 
            @click="toggleSidebar"
        >
            <el-icon>
                <template v-if="showButton">菜单</template>
                <template v-else>></template>
                <component :is="isCollapsed ? 'Expand' : 'Fold'" />
            </el-icon>
        </div>
        <div class="sidebar" :class="{ 'sidebar-collapsed': isCollapsed }">
            <div class="logo">
                <h1>SCT问卷后台</h1>
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
                <router-link to="/admin/main/image-manage" class="nav-item" @click="handleMenuClick">
                    <i class="el-icon-picture"></i>
                    <span v-show="!isCollapsed">图片管理</span>
                </router-link>
            </nav>
        </div>
        <div class="main-content" :class="{ 'main-content-expanded': isCollapsed }" @click="handleMainContentClick">
            <router-view></router-view>
        </div>
        <div class="sidebar-mask" v-if="isMobile && !isCollapsed" @click="toggleSidebar"></div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { Expand, Fold } from '@element-plus/icons-vue'

const isCollapsed = ref(false)
const isMobile = ref(false)
const showButton = ref(true)
let lastScrollTop = 0

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

onMounted(() => {
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
    position: relative;
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
}

.main-content-expanded {
    margin-left: -176px;
}

.mobile-menu-btn {
    position: fixed;
    top: 10px;
    left: 10px;
    z-index: 1001;
    background-color: #304156;
    color: #fff;
    width: 80px;
    height: 40px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
    transition: all 0.3s ease;
}

.mobile-menu-btn-scrolled {
    width: 20px;
    height: 40px;
    left: 0;
    border-radius: 0 4px 4px 0;
    background-color: rgba(48, 65, 86, 0.9);
}

.mobile-menu-btn-scrolled .el-icon {
    font-size: 24px;
}

.mobile-menu-btn .el-icon {
    font-size: 20px;
    color: white !important;
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

@media screen and (max-width: 768px) {
    .sidebar {
        position: fixed;
        height: 100vh;
        z-index: 1000;
        transform: translateX(0);
        box-shadow: 2px 0 8px rgba(0,0,0,.15);
        /* padding-top: 60px; */
    }

    .sidebar-collapsed {
        transform: translateX(-100%);
    }

    .main-content {
        margin-left: 0 !important;
        padding-top: 60px;
    }

    .logo {
        padding-top: 0;
    }

    .collapse-btn {
        display: block;
    }
}
</style>