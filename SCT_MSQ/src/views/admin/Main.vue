<template>
    <div class="main-container">
        <div class="sidebar">
            <div class="logo">
                <h1>SCT问卷后台</h1>
            </div>
            <nav class="nav-menu">
                <router-link to="/admin/main/questionnaire-review" class="nav-item">
                    <i class="el-icon-document-checked"></i>
                    问卷审核
                </router-link>
                <router-link to="/admin/main/questionnaire-manage" class="nav-item">
                    <i class="el-icon-document"></i>
                    问卷管理
                </router-link>
                <router-link to="/admin/main/image-manage" class="nav-item">
                    <i class="el-icon-picture"></i>
                    图片管理
                </router-link>
            </nav>
        </div>
        <div class="main-content">
            <router-view></router-view>
        </div>
    </div>
</template>

<script setup lang="ts">
// 组件逻辑将在这里添加
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
}

.logo {
    padding: 0 20px;
    margin-bottom: 20px;
}

.logo h1 {
    color: #fff;
    font-size: 20px;
    margin: 0;
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
}

.main-content {
    flex: 1;
    padding: 20px;
    background-color: #f0f2f5;
}

.content {
    background-color: #fff;
    padding: 20px;
    border-radius: 4px;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
</style>