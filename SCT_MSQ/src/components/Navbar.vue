<template>
  <nav class="navbar">
    <div class="nav-left">
      <img 
        :src="logoUrl" 
        alt="Logo" 
        class="logo-image"
        @click="goToHome"
        @error="handleImageError"
      />
      <div class="logo" @click="goToHome">{{ serverShortName }}</div>
    </div>
    <div class="nav-right">
      <el-button type="text" class="nav-btn" @click="goToResult">查询问卷结果</el-button>
      <el-button type="text" class="nav-btn" @click="goToOverview">浏览{{ serverShortName }}</el-button>
      <el-button v-if="showStockListTool" type="text" class="nav-btn" @click="goBuildTool">备货列表生成工具</el-button>
    </div>
  </nav>
</template>

<script>
import { getConfig } from '@/api/System'
export default {
  name: 'Navbar',
  data() {
    return {
      configMap: {},
      logoUrl: '',
      serverShortName: '',
      showStockListTool: false
    }
  },
  async created() {
    await this.fetchConfig()
  },
  methods: {
    async fetchConfig() {
      const CACHE_KEY = 'navbar_config_cache'
      const CACHE_EXPIRE = 10 * 60 * 1000 // 5分钟

      // 读取缓存
      const cacheStr = localStorage.getItem(CACHE_KEY)
      if (cacheStr) {
        try {
          const cache = JSON.parse(cacheStr)
          if (Date.now() - cache.time < CACHE_EXPIRE) {
            this.applyConfig(cache.data)
            return
          }
        } catch (e) {
          // 解析失败则忽略缓存
        }
      }

      // 无缓存或已过期，重新请求
      try {
        const res = await getConfig()
        if (res && res.data) {
          localStorage.setItem(CACHE_KEY, JSON.stringify({
            time: Date.now(),
            data: res.data
          }))
          this.applyConfig(res.data)
        }
      } catch (e) {
        // 错误处理
      }
    },
    applyConfig(data) {
      const map = {}
      data.forEach(item => {
        map[item.configKey] = item.configValue
      })
      this.configMap = map
      this.logoUrl = map['logo'] || ''
      this.serverShortName = map['server_short_name'] || ''
      this.showStockListTool = map['stock_list_tool'] === '1'
    },
    goToHome() {
      this.$router.push('/')
    },
    goToOverview() {
      this.$router.push('/overview')
    },
    goBuildTool() {
      this.$router.push('/build-tool')
    },
    goToResult() {
      this.$router.push('/msq/result')
    },
    handleImageError() {
      // 图片加载失败时的处理
      console.warn('Logo图片加载失败')
    }
  }
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30px;
  height: 60px;
  background-color: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.nav-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-image {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  object-fit: cover;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.logo-image:hover {
  transform: scale(1.05);
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  color: #409EFF;
  letter-spacing: 2px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.logo:hover {
  color: #337ecc;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-btn {
  font-size: 1rem;
  color: #606266;
}

.nav-btn:hover {
  color: #409EFF;
}
</style> 