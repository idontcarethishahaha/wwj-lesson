<template>
  <div class="profile-page page-container">
    <div class="container">
      <div class="page-header">
        <h1>个人中心</h1>
      </div>

      <div class="profile-content" v-loading="loading">
        <div class="profile-header">
          <el-avatar :size="100" :src="userInfo.avatar">
            {{ userInfo.nickname?.charAt(0) }}
          </el-avatar>
          <div class="user-info">
            <h2>{{ userInfo.nickname }}</h2>
            <p>{{ userInfo.signature || '这个人很懒，什么都没写' }}</p>
          </div>
        </div>

        <el-form
          ref="formRef"
          :model="formData"
          :rules="rules"
          label-width="100px"
          class="profile-form"
        >
          <el-form-item label="用户名">
            <el-input v-model="userInfo.username" disabled />
          </el-form-item>
          <el-form-item label="昵称" prop="nickname">
            <el-input v-model="formData.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="userInfo.phone" disabled />
          </el-form-item>
          <el-form-item label="个人签名" prop="signature">
            <el-input
              v-model="formData.signature"
              type="textarea"
              :rows="3"
              placeholder="请输入个人签名"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="formData.gender">
              <el-radio :label="1">男</el-radio>
              <el-radio :label="0">女</el-radio>
              <el-radio :label="-1">保密</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="生日">
            <el-date-picker
              v-model="formData.birthday"
              type="date"
              placeholder="选择生日"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSave" :loading="saving">
              保存修改
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)
const saving = ref(false)
const formRef = ref(null)

const userInfo = ref({
  username: '',
  phone: '',
  avatar: '',
  nickname: '',
  signature: '',
  gender: -1,
  birthday: ''
})

const formData = reactive({
  nickname: '',
  signature: '',
  gender: -1,
  birthday: ''
})

const rules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ]
}

const loadUserInfo = async () => {
  loading.value = true
  try {
    await userStore.getUserInfo()
    Object.assign(userInfo.value, userStore.userInfo || {})
    formData.nickname = userInfo.value.nickname || ''
    formData.signature = userInfo.value.signature || ''
    formData.gender = userInfo.value.gender ?? -1
    formData.birthday = userInfo.value.birthday || ''
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        await userStore.updateUserInfo(formData)
        ElMessage.success('保存成功')
        await loadUserInfo()
      } catch (error) {
        ElMessage.error(error.message || '保存失败')
      } finally {
        saving.value = false
      }
    }
  })
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.profile-page {
  padding: 40px 0;

  .page-header {
    margin-bottom: 30px;

    h1 {
      font-size: 24px;
      color: #303133;
    }
  }
}

.profile-content {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  .profile-header {
    display: flex;
    align-items: center;
    gap: 24px;
    padding-bottom: 30px;
    border-bottom: 1px solid #EBEEF5;
    margin-bottom: 30px;

    .user-info {
      h2 {
        font-size: 22px;
        color: #303133;
        margin-bottom: 8px;
      }

      p {
        color: #909399;
        font-size: 14px;
      }
    }
  }

  .profile-form {
    max-width: 600px;

    :deep(.el-input.is-disabled .el-input__wrapper) {
      background-color: #F5F7FA;
    }
  }
}
</style>
