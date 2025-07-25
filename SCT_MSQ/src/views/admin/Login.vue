<template>
    <div class="login-container">
        <form class="form-control" action="">
            <p class="login-title">后台登录</p>
            <div class="input-field">
                <input required class="input" type="text" v-model="username" @blur="validateUsername" />
                <label class="label" for="input">账 号</label>
                <span class="login-error-message" v-if="usernameError">{{ usernameError }}</span>
            </div>
            <div class="input-field">
                <input required class="input" type="password" v-model="password" @blur="validatePassword" />
                <label class="label" for="input">密 码</label>
                <span class="login-error-message" v-if="passwordError">{{ passwordError }}</span>
            </div>
            <button class="submit-btn" @click="submit">登录</button>
        </form>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { login } from '@/api/Auth.js'
import { useTokenStore } from '@/stores/token.js';
import useUserInfoStore from '@/stores/userInfo.js'
import { useRouter } from 'vue-router'

const router = useRouter()
const store = useTokenStore()
const userInfoStore = useUserInfoStore()

const username = ref('')
const password = ref('')
const usernameError = ref('')
const passwordError = ref('')
const loginData = ref({
    username: '',
    password: ''
})

const validateUsername = () => {
    if (!username.value) {
        usernameError.value = '请输入账号'
    } else {
        usernameError.value = ''
    }
}

const validatePassword = () => {
    if (!password.value) {
        passwordError.value = '请输入密码'
    } else {
        passwordError.value = ''
    }
}

const submit = async (e: Event) =>  {
    e.preventDefault()
    validateUsername()
    validatePassword()
    if (!usernameError.value && !passwordError.value) {
        loginData.value = {
            username: username.value,
            password: password.value
        }
        const response = await login(loginData.value)
        store.setToken(response.data.token)
        userInfoStore.setInfo(response.data.userInfo)
        router.push('/admin/main')
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
    document.title = getServerShortName() + '后台登录'
})
</script>

<style>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    width: 100%;
    /* margin-top: -100px; */
}

/* From Uiverse.io by VitorBaraoDias */ 
.form-control {
  margin: 20px;
  background-color: #ffffff;
  box-shadow: 0 15px 25px rgba(0, 0, 0, 0.6);
  width: 400px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  gap: 10px;
  padding: 25px;
  border-radius: 8px;
}
.login-title {
  font-size: 28px;
  font-weight: 800;
  margin-bottom: 0px;
}
.input-field {
  position: relative;
  width: 100%;
  margin-bottom: 5px;
}

.input {
  margin-top: 15px;
  width: 100%;
  outline: none;
  border-radius: 8px;
  height: 45px;
  border: 1.5px solid #ecedec;
  background: transparent;
  padding-left: 10px;
}
.input:focus {
  border: 1.5px solid #2d79f3;
}
.input-field .label {
  position: absolute;
  top: 25px;
  left: 15px;
  color: #ccc;
  transition: all 0.3s ease;
  pointer-events: none;
  z-index: 2;
}
.input-field .input:focus ~ .label,
.input-field .input:valid ~ .label {
  top: 5px;
  left: 5px;
  font-size: 12px;
  color: #2d79f3;
  background-color: #ffffff;
  padding-left: 5px;
  padding-right: 5px;
}
.submit-btn {
  margin-top: 30px;
  height: 55px;
  background: #f2f2f2;
  border-radius: 11px;
  border: 0;
  outline: none;
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
  background: linear-gradient(180deg, #363636 0%, #1b1b1b 50%, #000000 100%);
  box-shadow: 0px 0px 0px 0px #ffffff, 0px 0px 0px 0px #000000;
  transition: all 0.3s cubic-bezier(0.15, 0.83, 0.66, 1);
  cursor: pointer;
}

.submit-btn:hover {
  box-shadow: 0px 0px 0px 2px #ffffff, 0px 0px 0px 4px #0000003a;
}

.login-error-message {
    color: #ff4d4f;
    font-size: 12px;
    position: absolute;
    left: 0;
    bottom: -20px;
}

</style>