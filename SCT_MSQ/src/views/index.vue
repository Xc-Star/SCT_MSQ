<template>
  <div class="home-container">
    <h1>欢迎来到问卷系统</h1>
    <el-button type="primary" @click="goToMsqView">进入问卷</el-button>
  </div>
</template>

<script>
export default {
  name: 'Home',
  methods: {
    goToMsqView() {
      this.$router.push('/msq')
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