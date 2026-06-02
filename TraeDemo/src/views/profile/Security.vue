<template>
  <div class="security-page">
    <el-card shadow="never" class="security-card">
      <template #header>
        <div class="card-header">
          <el-icon :size="20"><Lock /></el-icon>
          <span>修改密码</span>
        </div>
      </template>
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="120px" class="security-form">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="passwordLoading" @click="handleChangePassword">确认修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="security-card">
      <template #header>
        <div class="card-header">
          <el-icon :size="20"><Iphone /></el-icon>
          <span>手机号绑定</span>
        </div>
      </template>
      <el-form :model="mobileForm" :rules="mobileRules" ref="mobileFormRef" label-width="120px" class="security-form">
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="mobileForm.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <div class="code-wrapper">
            <el-input v-model="mobileForm.code" placeholder="请输入验证码" maxlength="6" />
            <el-button :disabled="codeCountdown > 0" @click="handleSendCode">
              {{ codeCountdown > 0 ? `${codeCountdown}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="mobileLoading" @click="handleBindMobile">确认绑定</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Lock, Iphone } from '@element-plus/icons-vue'
import { rmsApi } from '@/api'

const passwordFormRef = ref()
const mobileFormRef = ref()
const passwordLoading = ref(false)
const mobileLoading = ref(false)
const codeCountdown = ref(0)
let countdownTimer: ReturnType<typeof setInterval> | null = null

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (_rule: any, value: string, callback: (error?: Error) => void) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const mobileForm = reactive({
  phone: '',
  code: ''
})

const validatePhone = (_rule: any, value: string, callback: (error?: Error) => void) => {
  if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('请输入正确的手机号'))
  } else {
    callback()
  }
}

const mobileRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 6, message: '验证码长度不正确', trigger: 'blur' }
  ]
}

async function handleChangePassword() {
  const valid = await passwordFormRef.value.validate().catch(() => false)
  if (!valid) return
  passwordLoading.value = true
  try {
    await rmsApi.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    passwordFormRef.value.resetFields()
  } catch {
    ElMessage.error('密码修改失败')
  } finally {
    passwordLoading.value = false
  }
}

async function handleSendCode() {
  const valid = await mobileFormRef.value.validateField('phone').catch(() => false)
  if (!valid) return
  try {
    await rmsApi.sendSmsCode(mobileForm.phone)
    ElMessage.success('验证码已发送')
    codeCountdown.value = 60
    countdownTimer = setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0) {
        if (countdownTimer) clearInterval(countdownTimer)
      }
    }, 1000)
  } catch {
    ElMessage.error('验证码发送失败')
  }
}

async function handleBindMobile() {
  const valid = await mobileFormRef.value.validate().catch(() => false)
  if (!valid) return
  mobileLoading.value = true
  try {
    await rmsApi.bindMobile({
      phone: mobileForm.phone,
      code: mobileForm.code
    })
    ElMessage.success('手机号绑定成功')
    mobileForm.phone = ''
    mobileForm.code = ''
    mobileFormRef.value.resetFields()
  } catch {
    ElMessage.error('手机号绑定失败')
  } finally {
    mobileLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.security-page {
  max-width: 680px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.security-card {
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
  }
}

.security-form {
  max-width: 420px;
}

.code-wrapper {
  display: flex;
  gap: 12px;
  width: 100%;

  .el-input {
    flex: 1;
  }

  .el-button {
    flex-shrink: 0;
    width: 120px;
  }
}
</style>