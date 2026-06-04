<template>
  <div class="seckill-detail-page">
    <div class="page-container">
      <el-card v-if="loading" class="detail-card">
        <el-skeleton :rows="5" animated />
      </el-card>

      <template v-else-if="activity">
        <el-card class="detail-card">
          <div class="detail-layout">
            <div class="detail-info">
              <h2 class="activity-title">{{ activity.title }}</h2>
              <div class="countdown-section">
                <span class="countdown-label">距离结束</span>
                <span class="countdown-value">{{ countdown }}</span>
              </div>
              <div class="stock-section">
                <el-progress
                  :percentage="stockPercent"
                  :stroke-width="12"
                  :format="stockFormat"
                  :color="stockPercent > 50 ? '#67c23a' : stockPercent > 20 ? '#e6a23c' : '#f56c6c'"
                />
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="courses-card" v-if="activity.seckillDetails.length > 0">
          <template #header>
            <span class="courses-title">秒杀课程</span>
          </template>
          <div class="courses-list">
            <div
              v-for="detail in activity.seckillDetails"
              :key="detail.id"
              class="course-item"
              :class="{ active: selectedDetail?.id === detail.id }"
              @click="selectDetail(detail)"
            >
              <el-image class="course-cover" :src="MINIO_COURSE_COVER(detail.courseCover)" fit="cover" />
              <div class="course-info">
                <h4 class="course-title">{{ detail.courseTitle }}</h4>
                <div class="course-price-section">
                  <span class="seckill-price">&yen;{{ detail.coursePrice }}</span>
                  <span class="stock">库存: {{ detail.skCount }}</span>
                </div>
              </div>
              <div class="select-indicator">
                <el-radio :model-value="selectedDetail?.id" :value="detail.id" />
              </div>
            </div>
          </div>

          <div class="action-section">
            <el-button
              type="danger"
              size="large"
              :loading="seckilling"
              :disabled="isEnded || !selectedDetail || selectedDetail.stock <= 0 || !userStore.isLoggedIn"
              class="seckill-btn"
              @click="handleSeckill"
            >
              {{ userStore.isLoggedIn ? (isEnded ? '已结束' : !selectedDetail ? '请选择课程' : selectedDetail.stock <= 0 ? '已抢完' : '立即秒杀') : '请先登录' }}
            </el-button>
          </div>
        </el-card>
      </template>

      <el-card v-else class="detail-card">
        <el-result icon="error" title="获取秒杀详情失败">
          <template #extra>
            <el-button type="primary" @click="fetchDetail">重新获取</el-button>
          </template>
        </el-result>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { smsApi } from '@/api'
import { useUserStore } from '@/stores/user'
import { MINIO_COURSE_COVER } from '@/const'
import type { SeckillActivity, SeckillDetail } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activity = ref<SeckillActivity | null>(null)
const selectedDetail = ref<SeckillDetail | null>(null)
const loading = ref(false)
const seckilling = ref(false)
const now = ref(Date.now())

let timer: ReturnType<typeof setInterval> | null = null

const stockPercent = computed(() => {
  if (!activity.value) return 0
  const initialStock = 100
  return Math.round(((initialStock - activity.value.stock) / initialStock) * 100)
})

const isEnded = computed(() => {
  if (!activity.value) return true
  return now.value >= new Date(activity.value.endTime).getTime()
})

const countdown = computed(() => {
  if (!activity.value) return ''
  const end = new Date(activity.value.endTime).getTime()
  const diff = Math.max(0, end - now.value)
  if (diff <= 0) return '已结束'
  const hours = Math.floor(diff / 3600000)
  const minutes = Math.floor((diff % 3600000) / 60000)
  const seconds = Math.floor((diff % 60000) / 1000)
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
})

onMounted(() => {
  fetchDetail()
  timer = setInterval(() => {
    now.value = Date.now()
  }, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})

function stockFormat() {
  if (!activity.value) return ''
  return `已抢 ${stockPercent.value}%`
}

function selectDetail(detail: SeckillDetail) {
  selectedDetail.value = detail
}

async function fetchDetail() {
  loading.value = true
  try {
    const res = await smsApi.getSeckillDetail(Number(route.params.id))
    activity.value = res.data
    if (activity.value.seckillDetails && activity.value.seckillDetails.length > 0) {
      selectedDetail.value = activity.value.seckillDetails[0]
    }
  } catch {
    ElMessage.error('获取秒杀详情失败')
  } finally {
    loading.value = false
  }
}

async function handleSeckill() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (!activity.value || !selectedDetail.value || isEnded.value || selectedDetail.value.stock <= 0) return
  seckilling.value = true
  try {
    const res = await smsApi.seckillOrder({
      fkSeckillId: activity.value.id,
      fkCourseId: selectedDetail.value.fkCourseId,
      fkUserId: userStore.userInfo?.id || 0,
      price: selectedDetail.value.coursePrice,
      skPrice: selectedDetail.value.skPrice,
    })
    ElMessage.success('秒杀成功')
    router.push(`/orders`)
  } catch {
    ElMessage.error('秒杀失败')
  } finally {
    seckilling.value = false
  }
}
</script>

<style scoped lang="scss">
.seckill-detail-page {
  padding: 24px 0;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
}

.detail-card {
  padding: 8px;
}

.detail-layout {
  display: flex;
  gap: 32px;
  align-items: flex-start;
}

.detail-cover {
  width: 320px;
  flex-shrink: 0;

  .el-image {
    width: 100%;
    border-radius: 8px;
  }
}

.detail-info {
  flex: 1;
  min-width: 0;

  .activity-title {
    font-size: 22px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 20px;
  }

  .price-section {
    display: flex;
    align-items: center;
    gap: 24px;
    margin-bottom: 20px;
    padding: 16px;
    background: linear-gradient(135deg, #fef0f0, #fff);
    border-radius: 8px;
  }

  .seckill-price {
    display: flex;
    align-items: baseline;
    gap: 8px;

    .label {
      font-size: 14px;
      color: #909399;
    }

    .value {
      font-size: 32px;
      font-weight: 700;
      color: #f56c6c;
    }
  }

  .original-price {
    display: flex;
    align-items: baseline;
    gap: 8px;

    .label {
      font-size: 14px;
      color: #909399;
    }

    .value {
      font-size: 16px;
      color: #c0c4cc;
      text-decoration: line-through;
    }
  }

  .stock-section {
    margin-bottom: 20px;
  }

  .countdown-section {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 24px;
    padding: 12px 16px;
    background: #fdf6ec;
    border-radius: 8px;

    .countdown-label {
      font-size: 14px;
      color: #606266;
    }

    .countdown-value {
      font-size: 24px;
      font-weight: 700;
      color: #e6a23c;
      font-variant-numeric: tabular-nums;
      letter-spacing: 2px;
    }
  }
}

.courses-card {
  margin-top: 24px;

  .courses-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
}

.courses-list {
  .course-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    margin-bottom: 12px;
    border: 2px solid #f0f0f0;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      border-color: #f56c6c;
    }

    &.active {
      border-color: #f56c6c;
      background: #fff5f5;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }

  .course-cover {
    width: 120px;
    height: 68px;
    border-radius: 6px;
    flex-shrink: 0;
  }

  .course-info {
    flex: 1;
    min-width: 0;

    .course-title {
      font-size: 15px;
      font-weight: 500;
      color: #303133;
      margin: 0 0 10px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .course-price-section {
      display: flex;
      align-items: center;
      gap: 16px;

      .seckill-price {
        font-size: 20px;
        font-weight: 700;
        color: #f56c6c;
      }

      .stock {
        font-size: 13px;
        color: #909399;
      }
    }
  }

  .select-indicator {
    .el-radio {
      --el-radio-size: 20px;
    }
  }
}

.action-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;

  .seckill-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
  }
}
</style>