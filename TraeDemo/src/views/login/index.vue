<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <img src="@/assets/login-bg.jpg" alt="在线课堂" />
      </div>
      <div class="login-right">
        <div class="login-form-wrapper">
          <h2 class="login-title">登录</h2>
          <p class="login-subtitle">欢迎回到在线课堂平台</p>
          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名/手机号"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <div class="form-options">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                <router-link to="/forgot-password">忘记密码？</router-link>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                class="login-btn"
                @click="handleLogin"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
          <div class="login-footer">
            <span>还没有账号？</span>
            <router-link to="/register">立即注册</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        ElMessage.success('登录成功')

        await userStore.getUserInfo()

        const redirect = route.query.redirect || '/home'
        router.push(redirect)
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  width: 900px;
  height: 560px;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
}

.login-left {
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

.login-right {
  width: 450px;
  padding: 60px 50px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-form-wrapper {
  width: 100%;
  max-width: 320px;
}

.login-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
  margin-bottom: 32px;
}

.login-form {
  .form-options {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;

    a {
      color: #409EFF;
      font-size: 14px;

      &:hover {
        text-decoration: underline;
      }
    }
  }

  .login-btn {
    width: 100%;
  }
}

.login-footer {
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
