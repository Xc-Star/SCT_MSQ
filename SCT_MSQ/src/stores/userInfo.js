
import { defineStore } from 'pinia'
import { ref } from 'vue'

const useUserInfoStore = defineStore('userInfo', () => {
    // 从 localStorage 获取初始用户信息
    const getStoredInfo = () => {
        const storedInfo = localStorage.getItem('userInfo')
        return storedInfo ? JSON.parse(storedInfo) : {}
    }

    // 定义状态相关的内容
    const info = ref(getStoredInfo())

    const setInfo = (newInfo) => {
        info.value = newInfo
        // 同时保存到 localStorage
        localStorage.setItem('userInfo', JSON.stringify(newInfo))
    }

    const removeInfo = () => {
        info.value = {}
        // 同时从 localStorage 中删除
        localStorage.removeItem('userInfo')
    }

    return {
        info,
        setInfo,
        removeInfo
    }
}, { persist: true })

export default useUserInfoStore