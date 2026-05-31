<template>
  <div class="notice-page page-container">
    <div class="container">
      <div class="page-header">
        <h1>系统通知</h1>
        <p>了解平台最新动态和活动信息</p>
      </div>

      <div class="notice-list" v-loading="loading">
        <div
          v-for="notice in noticeList"
          :key="notice.id"
          class="notice-card"
          @click="goToDetail(notice.id)"
        >
          <div class="notice-icon">
            <el-icon :size="24"><Bell /></el-icon>
          </div>
          <div class="notice-content">
            <h3 class="notice-title">{{ notice.title }}</h3>
            <p class="notice-summary">{{ notice.summary }}</p>
            <div class="notice-time">{{ formatTime(notice.createTime) }}</div>
          </div>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>

      <el-empty v-if="!loading && noticeList.length === 0" description="暂无通知" />

      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="limit"
          :total="total"
          :page-sizes="[10, 20, 30]"
          layout="total, prev, pager, next"
          @current-change="loadNotices"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { marketingApi } from '@/api/marketing'
import { Bell, ArrowRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const noticeList = ref([])
const page = ref(1)
const limit = ref(10)
const total = ref(0)

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const goToDetail = (id) => {
  router.push(`/notice/detail/${id}`)
}

const loadNotices = async () => {
  loading.value = true
  try {
    const res = await marketingApi.getNotices({ page: page.value, limit: limit.value })
    noticeList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    ElMessage.error('加载通知列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadNotices()
})
</script>

<style lang="scss" scoped>
.notice-page {
  padding: 40px 0;

  .page-header {
    text-align: center;
    margin-bottom: 40px;

    h1 {
      font-size: 28px;
      color: #303133;
      margin-bottom: 8px;
    }

    p {
      color: #909399;
      font-size: 14px;
    }
  }
}

.notice-list {
  .notice-card {
    display: flex;
    align-items: center;
    gap: 20px;
    background: #fff;
    padding: 24px;
    border-radius: 12px;
    margin-bottom: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateX(4px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    }

    .notice-icon {
      width: 48px;
      height: 48px;
      background: linear-gradient(135deg, #409EFF, #667eea);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      flex-shrink: 0;
    }

    .notice-content {
      flex: 1;

      .notice-title {
        font-size: 16px;
        color: #303133;
        margin-bottom: 8px;
        font-weight: 600;
      }

      .notice-summary {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .notice-time {
        font-size: 13px;
        color: #C0C4CC;
      }
    }

    .arrow {
      color: #C0C4CC;
      font-size: 20px;
    }
  }
}

.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
</style>
