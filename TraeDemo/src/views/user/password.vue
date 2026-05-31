<template>
  <div class="password-page page-container">
    <div class="container">
      <div class="page-header">
        <h1>修改密码</h1>
      </div>

      <div class="password-content">
        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="120px"
          class="password-form"
        >
          <el-form-item label="当前密码" prop="oldPassword">
            <el-input
              v-model="formData.oldPassword"
              type="password"
              placeholder="请输入当前密码"
              show-password
            />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input
              v-model="formData.newPassword"
              type="password"
              placeholder="请输入新密码（至少6位）"
              show-password
            />
          </el-form-item>
          <el-form-item label="确认新密码" prop="confirmPassword">
            <el-input
              v-model="formData.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              show-password
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              确认修改
            </el-button>
            <el-button @click="goBack">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const submitting = ref(false)

const formData = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== formData.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await userStore.updatePassword({
          oldPassword: formData.oldPassword,
          newPassword: formData.newPassword
        })
        ElMessage.success('密码修改成功')
        userStore.logout()
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.message || '修改失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const goBack = () => {
  router.back()
}
</script>

<style lang="scss" scoped>
.password-page {
  padding: 40px 0;

  .page-header {
    margin-bottom: 30px;

    h1 {
      font-size: 24px;
      color: #303133;
    }
  }
}

.password-content {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  .password-form {
    max-width: 500px;
  }
}
</style>
