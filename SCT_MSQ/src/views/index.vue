<template>
  <div class="home-container">
    <h1>欢迎来到SCT</h1>
    <el-button type="primary" @click="goToMsqView">进入问卷</el-button>
    <el-button type="primary" @click="goBuildTool">备货列表生成工具</el-button>
  </div>
</template>

<script>
export default {
  name: 'Home',
  methods: {
    goToMsqView() {
      this.$router.push('/msq')
    },
    goBuildTool() {
      this.$router.push('/build-tool')
    }
  }
}
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  text-align: center;
}

h1 {
  margin-bottom: 30px;
  color: #409EFF;
}
</style>