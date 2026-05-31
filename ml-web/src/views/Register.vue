<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <h2 class="register-title">创建账号</h2>
        <p class="register-subtitle">注册新账号以开始学习之旅</p>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="0"
          class="register-form"
          @keyup.enter="handleRegister"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请确认密码"
              :prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input
              v-model="form.phone"
              placeholder="请输入手机号"
              :prefix-icon="Iphone"
              size="large"
            />
          </el-form-item>
          <el-form-item prop="code">
            <div class="code-wrapper">
              <el-input
                v-model="form.code"
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
              class="register-btn"
              :loading="registering"
              @click="handleRegister"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="login-link">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Iphone, Key } from '@element-plus/icons-vue'
import { rmsApi } from '@/api'
import type { FormInstance } from 'element-plus'

const router = useRouter()

const formRef = ref<FormInstance>()
const registering = ref(false)
const codeSending = ref(false)
const codeCountdown = ref(0)
let countdownTimer: ReturnType<typeof setInterval> | null = null

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  code: ''
})

const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度在 6 到 32 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

async function handleRegister() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  registering.value = true
  try {
    await rmsApi.register({
      username: form.username,
      password: form.password,
      phone: form.phone,
      code: form.code
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // error handled by interceptor
  } finally {
    registering.value = false
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
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }

  codeSending.value = true
  try {
    await rmsApi.sendSmsCode(form.phone)
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
.register-page {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.register-container {
  width: 100%;
  max-width: 440px;
}

.register-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.register-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  text-align: center;
  margin: 0 0 8px;
}

.register-subtitle {
  font-size: 14px;
  color: #909399;
  text-align: center;
  margin: 0 0 32px;
}

.register-form {
  margin-top: 8px;
}

.register-btn {
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

.register-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #909399;
}

.login-link {
  color: #409EFF;
  font-weight: 500;

  &:hover {
    text-decoration: underline;
  }
}
</style>