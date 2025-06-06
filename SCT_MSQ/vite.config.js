import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    open: true,
    proxy: {
      // 获取路径中含'/api'的请求
      '/api': {
        // 后台服务所在的源
        target: 'http://localhost:48080',
        // target: 'http://127.0.0.1:4523/m1/6495443-6195651-default',
        // 是否修改源
        changeOrigin: true,
        // 重写路径
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  devServer: {
    host: '0.0.0.0',
    disableHostCheck: true,
    proxy: {
      // 获取路径中含'/api'的请求
      '/api': {
        // 后台服务所在的源
        target: 'http://localhost:48080',
        // target: 'http://127.0.0.1:4523/m1/6495443-6195651-default',
        // 是否修改源
        changeOrigin: true,
        // 重写路径
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
})
