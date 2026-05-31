<template>
  <div class="notice-detail-page page-container" v-loading="loading">
    <div class="container">
      <div class="detail-header">
        <el-button text @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>

      <div class="notice-content" v-if="noticeInfo.id">
        <h1 class="notice-title">{{ noticeInfo.title }}</h1>
        <div class="notice-meta">
          <span class="publish-time">
            <el-icon><Clock /></el-icon>
            {{ formatTime(noticeInfo.createTime) }}
          </span>
          <span class="views">
            <el-icon><View /></el-icon>
            {{ noticeInfo.viewCount }} 次阅读
          </span>
        </div>
        <div class="notice-body" v-html="noticeInfo.content"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { marketingApi } from '@/api/marketing'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Clock, View } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const noticeInfo = ref({})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const goBack = () => {
  router.back()
}

const loadNoticeDetail = async () => {
  loading.value = true
  try {
    const res = await marketingApi.getNoticeDetail(route.params.id)
    noticeInfo.value = res.data || {}
  } catch (error) {
    ElMessage.error('加载通知详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadNoticeDetail()
})
</script>

<style lang="scss" scoped>
.notice-detail-page {
  padding: 40px 0;

  .detail-header {
    margin-bottom: 20px;
  }
}

.notice-content {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  .notice-title {
    font-size: 28px;
    color: #303133;
    text-align: center;
    margin-bottom: 20px;
    font-weight: 700;
  }

  .notice-meta {
    display: flex;
    justify-content: center;
    gap: 32px;
    padding-bottom: 24px;
    border-bottom: 1px solid #EBEEF5;
    margin-bottom: 32px;

    span {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #909399;
      font-size: 14px;
    }
  }

  .notice-body {
    line-height: 1.8;
    color: #606266;
    font-size: 15px;

    :deep(p) {
      margin-bottom: 16px;
    }

    :deep(img) {
      max-width: 100%;
      border-radius: 8px;
      margin: 16px 0;
    }
  }
}
</style>
