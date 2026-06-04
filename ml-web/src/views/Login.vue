<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <h2 class="login-title">欢迎回来</h2>
        <p class="login-subtitle">登录您的账号以继续</p>

        <el-tabs v-model="activeTab" stretch>
          <el-tab-pane label="账号密码登录" name="account">
            <el-form
              ref="accountFormRef"
              :model="accountForm"
              :rules="accountRules"
              label-width="0"
              class="login-form"
              @keyup.enter="handleAccountLogin"
            >
              <el-form-item prop="username">
                <el-input
                  v-model="accountForm.username"
                  placeholder="请输入用户名"
                  :prefix-icon="User"
                  size="large"
                />
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  v-model="accountForm.password"
                  type="password"
                  placeholder="请输入密码"
                  :prefix-icon="Lock"
                  size="large"
                  show-password
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  class="login-btn"
                  :loading="accountLoading"
                  @click="handleAccountLogin"
                >
                  登录
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="手机号验证码登录" name="mobile">
            <el-form
              ref="mobileFormRef"
              :model="mobileForm"
              :rules="mobileRules"
              label-width="0"
              class="login-form"
              @keyup.enter="handleMobileLogin"
            >
              <el-form-item prop="phone">
                <el-input
                  v-model="mobileForm.phone"
                  placeholder="请输入手机号"
                  :prefix-icon="Iphone"
                  size="large"
                />
              </el-form-item>
              <el-form-item prop="code">
                <div class="code-wrapper">
                  <el-input
                    v-model="mobileForm.code"
                    placeholder="请输入验证码"
                    :prefix-icon="Key"
                    size="large"
                    class="code-input"
                  />
                  <el-button
                    size="large"
                    :disabled="codeSending || codeCountdown > 0"
                    @click="handleSendCode"
                  >
                    {{ codeCountdown > 0 ? `${codeCountdown}s` : '获取验证码' }}
                  </el-button>
                </div>
              </el-form-item>
              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  class="login-btn"
                  :loading="mobileLoading"
                  @click="handleMobileLogin"
                >
                  登录
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <div class="login-footer">
          <span>没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, Key } from '@element-plus/icons-vue'
import { rmsApi } from '@/api'
import { useUserStore } from '@/stores/user'
import type { FormInstance } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('account')
const accountLoading = ref(false)
const mobileLoading = ref(false)
const codeSending = ref(false)
const codeCountdown = ref(0)
let countdownTimer: ReturnType<typeof setInterval> | null = null

const accountFormRef = ref<FormInstance>()
const mobileFormRef = ref<FormInstance>()

const accountForm = reactive({
  username: 'jiahao',
  password: '123456789'
})

const mobileForm = reactive({
  phone: '',
  code: ''
})

const accountRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const mobileRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

function doLogin(token: string, userInfo: any) {
  userStore.setToken(token)
  userStore.setUserInfo(userInfo)
  ElMessage.success('登录成功')
  const redirect = (route.query.redirect as string) || '/'
  router.push(redirect)
}

async function handleAccountLogin() {
  const valid = await accountFormRef.value?.validate().catch(() => false)
  if (!valid) return

  accountLoading.value = true
  try {
    const res = await rmsApi.login({
      username: accountForm.username,
      password: accountForm.password
    })
    doLogin(res.data.token, res.data.user)
  } catch {
    accountForm.password = ''
  } finally {
    accountLoading.value = false
  }
}

async function handleMobileLogin() {
  const valid = await mobileFormRef.value?.validate().catch(() => false)
  if (!valid) return

  mobileLoading.value = true
  try {
    const res = await rmsApi.loginByMobile({
      phone: mobileForm.phone,
      code: mobileForm.code
    })
    doLogin(res.data.token, res.data.userInfo)
  } catch {
    // error handled by interceptor
  } finally {
    mobileLoading.value = false
  }
}

function startCountdown() {
  codeCountdown.value = 60
  countdownTimer = setInterval(() => {
    codeCountdown.value--
    if (codeCountdown.value <= 0) {
      if (countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }
  }, 1000)
}

async function handleSendCode() {
  if (!/^1[3-9]\d{9}$/.test(mobileForm.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }

  codeSending.value = true
  try {
    await rmsApi.sendSmsCode(mobileForm.phone)
    ElMessage.success('验证码已发送')
    startCountdown()
  } catch {
    // error handled by interceptor
  } finally {
    codeSending.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.login-container {
  width: 100%;
  max-width: 440px;
}

.login-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.login-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  text-align: center;
  margin: 0 0 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
  text-align: center;
  margin: 0 0 32px;
}

.login-form {
  margin-top: 8px;
}

.login-btn {
  width: 100%;
}

.code-wrapper {
  display: flex;
  gap: 12px;
  width: 100%;

  .code-input {
    flex: 1;
  }
}

.login-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #909399;
}

.register-link {
  color: #409EFF;
  font-weight: 500;

  &:hover {
    text-decoration: underline;
  }
}
</style>