<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-left">
        <img src="@/assets/register-bg.jpg" alt="在线课堂" />
      </div>
      <div class="register-right">
        <div class="register-form-wrapper">
          <h2 class="register-title">注册</h2>
          <p class="register-subtitle">开启您的学习之旅</p>
          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            class="register-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input
                v-model="registerForm.phone"
                placeholder="请输入手机号"
                size="large"
                :prefix-icon="Phone"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请输入密码（至少6位）"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item>
              <el-checkbox v-model="agreeProtocol">
                我已阅读并同意<a href="#">《用户协议》</a>和<a href="#">《隐私政策》</a>
              </el-checkbox>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="register-btn"
                :disabled="!agreeProtocol"
                @click="handleRegister"
              >
                注册
              </el-button>
            </el-form-item>
          </el-form>
          <div class="register-footer">
            <span>已有账号？</span>
            <router-link to="/login">立即登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Phone, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const registerFormRef = ref(null)
const loading = ref(false)
const agreeProtocol = ref(false)

const registerForm = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.register({
          username: registerForm.username,
          phone: registerForm.phone,
          password: registerForm.password
        })
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-container {
  width: 900px;
  height: 620px;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
}

.register-left {
  width: 450px;
  background: linear-gradient(135deg, #409EFF 0%, #667eea 100%);
  display: flex;
  align-items: center;
  justify-content: center;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.register-right {
  width: 450px;
  padding: 40px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow-y: auto;
}

.register-form-wrapper {
  width: 100%;
  max-width: 320px;
}

.register-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.register-subtitle {
  font-size: 14px;
  color: #909399;
  margin-bottom: 32px;
}

.register-form {
  :deep(.el-checkbox) {
    a {
      color: #409EFF;
      margin: 0 2px;

      &:hover {
        text-decoration: underline;
      }
    }
  }

  .register-btn {
    width: 100%;
  }
}

.register-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: #909399;

  a {
    color: #409EFF;
    margin-left: 4px;

    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
