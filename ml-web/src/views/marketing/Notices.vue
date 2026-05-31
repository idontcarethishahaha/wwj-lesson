<template>
  <div class="notices-page">
    <div class="page-container">
      <el-card class="notices-card">
        <template #header>
          <span class="title">系统通知</span>
        </template>

        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="5" animated />
        </div>

        <div v-else class="notice-list">
          <div
            class="notice-item"
            v-for="notice in notices"
            :key="notice.id"
            @click="handleViewDetail(notice)"
          >
            <div class="notice-icon">
              <el-icon :size="24" color="#409EFF"><Bell /></el-icon>
            </div>
            <div class="notice-content">
              <div class="notice-title">{{ notice.content }}</div>
              <div class="notice-time">{{ notice.createTime }}</div>
            </div>
            <div class="notice-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>

          <el-empty v-if="notices.length === 0" description="暂无通知" />
        </div>

        <div class="pagination-wrapper" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next, total"
            background
            @current-change="handlePageChange"
          />
        </div>
      </el-card>
    </div>

    <el-dialog
      v-model="detailVisible"
      :title="detailNotice?.title || '通知详情'"
      width="600px"
      destroy-on-close
    >
      <div class="notice-detail-content" v-if="detailNotice">
        <div class="detail-time">{{ detailNotice.createTime }}</div>
        <div class="detail-body">{{ detailNotice.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell, ArrowRight } from '@element-plus/icons-vue'
import { smsApi } from '@/api'
import type { Notice } from '@/types'

const notices = ref<Notice[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const detailVisible = ref(false)
const detailNotice = ref<Notice | null>(null)

onMounted(() => {
  fetchNotices()
})

async function fetchNotices() {
  loading.value = true
  try {
    const res = await smsApi.getNotices({ pageNum: currentPage.value, pageSize: pageSize.value })
    notices.value = res.data.records
    total.value = res.data.total
  } catch {
    ElMessage.error('获取通知列表失败')
  } finally {
    loading.value = false
  }
}

function handlePageChange(page: number) {
  currentPage.value = page
  fetchNotices()
}

async function handleViewDetail(notice: Notice) {
  try {
    const res = await smsApi.getNoticeDetail(notice.id)
    detailNotice.value = res.data
    detailVisible.value = true
  } catch {
    ElMessage.error('获取通知详情失败')
  }
}
</script>

<style scoped lang="scss">
.notices-page {
  padding: 24px 0;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
}

.notices-card {
  .title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
}

.loading-state {
  padding: 40px;
}

.notice-list {
  .notice-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px 0;
    cursor: pointer;
    transition: background 0.2s;
    border-bottom: 1px solid #f5f5f5;

    &:hover {
      background: #fafafa;
      margin: 0 -16px;
      padding: 16px;
      border-radius: 4px;
      border-bottom-color: transparent;
    }

    &:last-child {
      border-bottom: none;
    }
  }

  .notice-icon {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #ecf5ff;
    border-radius: 8px;
    flex-shrink: 0;
  }

  .notice-content {
    flex: 1;
    min-width: 0;

    .notice-title {
      font-size: 15px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .notice-time {
      font-size: 12px;
      color: #c0c4cc;
    }
  }

  .notice-arrow {
    color: #c0c4cc;
    flex-shrink: 0;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.notice-detail-content {
  .detail-time {
    font-size: 13px;
    color: #909399;
    margin-bottom: 16px;
  }

  .detail-body {
    font-size: 15px;
    color: #303133;
    line-height: 1.8;
    white-space: pre-wrap;
  }
}
</style>