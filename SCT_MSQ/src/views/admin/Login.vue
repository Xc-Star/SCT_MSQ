<template>
    <div class="login-container">
        <form class="form-control" action="">
            <p class="login-title">后台登录</p>
            <div class="input-field">
                <input required id="login-username" class="input" type="text" autocomplete="username" v-model="username" @blur="validateUsername" />
                <label class="label" for="login-username">账 号</label>
                <span class="login-error-message" v-if="usernameError">{{ usernameError }}</span>
            </div>
            <div class="input-field">
                <input required id="login-password" class="input" type="password" autocomplete="current-password" v-model="password" @blur="validatePassword" />
                <label class="label" for="login-password">密 码</label>
                <span class="login-error-message" v-if="passwordError">{{ passwordError }}</span>
            </div>
            <button class="submit-btn" :disabled="submitting" @click="submit">{{ submitting ? '登录中...' : '登录' }}</button>
        </form>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { login } from '@/api/Auth.js'
import { getServerShortName } from '@/api/System'
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
const submitting = ref(false)
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
  if (submitting.value) return
    validateUsername()
    validatePassword()
    if (!usernameError.value && !passwordError.value) {
        loginData.value = {
            username: username.value,
            password: password.value
        }
        submitting.value = true
        try {
          const response = await login(loginData.value)
          store.setToken(response.data.token)
          userInfoStore.setInfo(response.data.userInfo)
          await router.push('/admin/main')
        } catch {
        } finally {
          submitting.value = false
        }
    }
}

onMounted(async () => {
    document.title = (await getServerShortName()) + '后台登录'
})
</script>

<style scoped>
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
  background-color: var(--shell);
  box-shadow: var(--shadow-soft), var(--glass-spec);
  width: 400px;
  display: flex;
  justify-content: center;
  flex-direction: column;
  gap: 10px;
  padding: 25px;
  border-radius: var(--radius-lg);
  outline: 1px solid var(--shell-border);
  backdrop-filter: blur(2px) saturate(1.8);
  -webkit-backdrop-filter: blur(2px) saturate(1.8);
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
  border-radius: var(--radius-md);
  height: 45px;
  border: 1.5px solid var(--hair);
  color: var(--ink-900);
  background: transparent;
  padding-left: 10px;
}
.input:focus {
  border: 1.5px solid var(--aqua-500);
}
.input-field .label {
  position: absolute;
  top: 25px;
  left: 15px;
  color: var(--ink-300);
  transition: all 0.3s ease;
  pointer-events: none;
  z-index: 2;
}
.input-field .input:focus ~ .label,
.input-field .input:valid ~ .label {
  top: 5px;
  left: 5px;
  font-size: 12px;
  color: var(--aqua-500);
  background-color: var(--mist-50);
  padding-left: 5px;
  padding-right: 5px;
}
.submit-btn {
  margin-top: 30px;
  height: 55px;
  border-radius: var(--radius-pill);
  border: 0;
  outline: none;
  color: var(--ink-900);
  font-size: 18px;
  font-weight: 700;
  background: var(--sct-button-bg);
  box-shadow: inset 0 0 0 1px var(--shell-border), var(--shadow-soft), var(--glass-spec);
  backdrop-filter: blur(2px) saturate(1.8);
  -webkit-backdrop-filter: blur(2px) saturate(1.8);
  transition: background-color 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.submit-btn:hover {
  background: var(--sct-button-hover-bg);
  box-shadow: inset 0 0 0 1px var(--aqua-400), var(--shadow-soft), var(--glass-spec);
}
.submit-btn:active { transform: scale(0.96); }
.submit-btn:focus-visible, .input:focus-visible {
  outline: 3px solid var(--focus-ring);
  outline-offset: 3px;
}

.login-error-message {
    color: var(--sct-danger);
    font-size: 12px;
    position: absolute;
    left: 0;
    bottom: -20px;
}

</style>