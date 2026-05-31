<template>
  <div class="avatar-page page-container">
    <div class="container">
      <div class="page-header">
        <h1>头像设置</h1>
      </div>

      <div class="avatar-content">
        <div class="avatar-preview">
          <el-avatar :size="200" :src="previewUrl">
            {{ nickname?.charAt(0) }}
          </el-avatar>
          <p class="preview-tip">头像预览</p>
        </div>

        <div class="avatar-upload">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
            accept="image/*"
            class="upload-area"
          >
            <el-button type="primary" size="large">
              <el-icon><Upload /></el-icon>
              选择图片
            </el-button>
          </el-upload>
          <p class="upload-tip">
            支持 JPG、PNG 格式，文件小于 2MB
          </p>
          <el-button
            type="success"
            size="large"
            :loading="uploading"
            :disabled="!selectedFile"
            @click="handleUpload"
          >
            上传头像
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const uploadRef = ref(null)
const selectedFile = ref(null)
const previewUrl = ref('')
const uploading = ref(false)

const nickname = computed(() => userStore.userInfo?.nickname || '')

onMounted(() => {
  previewUrl.value = userStore.userInfo?.avatar || ''
})

const handleFileChange = (file) => {
  const isLt2M = file.size / 1024 / 1024 < 2
  const isImage = file.raw.type.startsWith('image/')

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return
  }

  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return
  }

  selectedFile.value = file.raw
  previewUrl.value = URL.createObjectURL(file.raw)
}

const handleUpload = async () => {
  if (!selectedFile.value) return

  uploading.value = true
  try {
    await userStore.uploadAvatar(selectedFile.value)
    ElMessage.success('头像上传成功')
    router.push('/user/profile')
  } catch (error) {
    ElMessage.error(error.message || '上传失败')
  } finally {
    uploading.value = false
  }
}
</script>

<style lang="scss" scoped>
.avatar-page {
  padding: 40px 0;

  .page-header {
    margin-bottom: 30px;

    h1 {
      font-size: 24px;
      color: #303133;
    }
  }
}

.avatar-content {
  background: #fff;
  padding: 60px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  align-items: center;

  .avatar-preview {
    text-align: center;
    margin-bottom: 40px;

    :deep(.el-avatar) {
      border: 4px solid #EBEEF5;
    }

    .preview-tip {
      margin-top: 16px;
      color: #909399;
      font-size: 14px;
    }
  }

  .avatar-upload {
    text-align: center;

    .upload-area {
      margin-bottom: 16px;
    }

    .upload-tip {
      color: #909399;
      font-size: 13px;
      margin-bottom: 24px;
    }
  }
}
</style>
