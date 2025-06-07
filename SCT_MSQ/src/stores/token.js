// 定义stores

import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * token: 名字, 唯一性
 * function: 函数的内部可以定义状态的所有内容
 */
export const useTokenStore = defineStore('token', () => {

    // 定义响应式变量
    const token = ref('')

    // 定义一个函数修改token
    const setToken = (newToken) => {
        token.value = newToken
    }

    // 定义一个函数删除token
    const removeToken = () => {
        token.value = ''
    }

    return {
        token,
        setToken,
        removeToken
    }
},
{
    persist: true
}
)