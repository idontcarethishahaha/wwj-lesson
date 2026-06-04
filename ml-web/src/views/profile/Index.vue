<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="profile-avatar">
        <el-upload
          :show-file-list="false"
          :http-request="handleUpload"
          accept="image/*"
        >
          <el-avatar
              :size="96"
              :src="userStore.userInfo?.avatar ? MINIO_AVATAR(userStore.userInfo.avatar) : ''"
          >
            <el-icon :size="40"><UserFilled /></el-icon>
          </el-avatar>
          <div class="avatar-overlay">
            <el-icon :size="24"><Camera /></el-icon>
            <span>更换头像</span>
          </div>
        </el-upload>
      </div>
      <div class="profile-info">
        <h2 class="nickname">{{ userStore.userInfo?.nickname || '未设置昵称' }}</h2>
        <p class="signature">{{ userStore.userInfo?.info || '这个人很懒，什么都没写...' }}</p>
      </div>
    </div>

    <div class="profile-content">
      <div class="content-left">
        <el-card shadow="never" class="info-card">
          <template #header>
            <div class="card-header">
              <span>个人信息</span>
              <el-button type="primary" size="small" @click="openEditDialog">编辑资料</el-button>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户名">{{ userStore.userInfo?.username }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userStore.userInfo?.phone || '未绑定' }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ userStore.userInfo?.role || '-' }}</el-descriptions-item>
            <el-descriptions-item label="注册时间">{{ userStore.userInfo?.created }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never" class="quick-links">
          <template #header>
            <span>快捷入口</span>
          </template>
          <div class="links">
            <el-button text @click="$router.push('/profile/security')">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </el-button>
            <el-button text @click="$router.push('/profile/learning')">
              <el-icon><Reading /></el-icon>
              <span>学习记录</span>
            </el-button>
          </div>
        </el-card>
      </div>
    </div>

    <el-dialog v-model="showEditDialog" title="编辑资料" width="480px" destroy-on-close>
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="80px">
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="签名" prop="signature">
          <el-input
            v-model="editForm.signature"
            type="textarea"
            :rows="4"
            placeholder="请输入个人签名"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSaveProfile">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled, Camera, Lock, Reading } from '@element-plus/icons-vue'
import { rmsApi } from '@/api'
import { useUserStore } from '@/stores/user'
import type { UploadRequestOptions } from 'element-plus'
import { MINIO_AVATAR } from "@/const/index.js";

const userStore = useUserStore()

const showEditDialog = ref(false)
const saving = ref(false)

const editForm = reactive({
  nickname: '',
  signature: ''
})

const editRules = {
  nickname: [
    { max: 20, message: '昵称最多20个字符', trigger: 'blur' }
  ],
  signature: [
    { max: 100, message: '签名最多100个字符', trigger: 'blur' }
  ]
}

const editFormRef = ref()

// ========== 已修改完成的上传方法 ==========
async function handleUpload(options: UploadRequestOptions) {
  // 取出当前登录用户ID
  const userId = userStore.userInfo?.id
  if (!userId) {
    ElMessage.error('未获取到用户信息')
    return
  }

  const formData = new FormData()
  // 和后端 @RequestParam("avatarFile") 保持一致
  formData.append('avatarFile', options.file)

  try {
    const res = await rmsApi.uploadAvatar(userId, formData)
    if (userStore.userInfo) {
      userStore.userInfo.avatar = res.data.url
    }
    ElMessage.success('头像更新成功')
  } catch (err) {
    console.error('上传异常：', err)
    ElMessage.error('头像上传失败')
  }
}

async function handleSaveProfile() {
  const valid = await editFormRef.value.validate().catch(() => false)
  if (!valid) return
  saving.value = true
  try {
    await rmsApi.updateProfile({
      userId: userStore.userInfo?.id,
      nickname: editForm.nickname,
      signature: editForm.signature
    })
    if (userStore.userInfo) {
      userStore.userInfo.nickname = editForm.nickname
      userStore.userInfo.signature = editForm.signature
    }
    ElMessage.success('资料更新成功')
    showEditDialog.value = false
  } catch {
    ElMessage.error('资料更新失败')
  } finally {
    saving.value = false
  }
}

function openEditDialog() {
  editForm.nickname = userStore.userInfo?.nickname || ''
  editForm.signature = userStore.userInfo?.signature || ''
  showEditDialog.value = true
}

onMounted(() => {
})
</script>

<style scoped lang="scss">
.profile-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 40px;
  margin-bottom: 24px;
  color: #fff;
}

.profile-avatar {
  position: relative;
  cursor: pointer;

  .avatar-overlay {
    position: absolute;
    inset: 0;
    border-radius: 50%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.3s;
    color: #fff;
    font-size: 12px;
    gap: 4px;
  }

  &:hover .avatar-overlay {
    opacity: 1;
  }
}

.profile-info {
  flex: 1;

  .nickname {
    font-size: 24px;
    font-weight: 600;
    margin: 0 0 8px;
  }

  .signature {
    font-size: 14px;
    opacity: 0.85;
    margin: 0;
  }
}

.profile-content {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 24px;
}

.content-left {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.quick-links {
  .links {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .el-button {
      justify-content: flex-start;
      gap: 8px;
      height: 44px;
    }
  }
}

.role-card {
  .role-content {
    min-height: 100px;
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .role-badge {
    display: flex;
    justify-content: center;
  }

  .permissions {
    h4 {
      margin: 0 0 12px;
      font-size: 14px;
      color: #606266;
    }

    .permission-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }
  }
}
</style>