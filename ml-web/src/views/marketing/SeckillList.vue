<template>
  <div class="seckill-list-page">
    <div class="page-container">
      <el-card class="list-card">
        <template #header>
          <span class="title">今日秒杀活动</span>
        </template>

        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="4" animated />
        </div>

        <div v-else-if="activities.length === 0" class="empty-state">
          <el-empty description="暂无秒杀活动" />
        </div>

        <div v-else class="activity-grid">
          <div
            class="activity-card"
            v-for="(activity, index) in activities"
            :key="activity.id"
            @click="$router.push(`/seckill/${activity.id}`)"
          >
            <div class="activity-cover" :class="`cover-${index % 4}`">
              <div class="cover-content">
                <span class="seckill-badge">秒杀</span>
                <span class="cover-text">{{ activity.title }}</span>
              </div>
            </div>
            <div class="activity-info">
              <h3 class="activity-title">{{ activity.title }}</h3>
              <div class="activity-price">
                <span class="seckill-price">&yen;{{ activity.price }}</span>
                <span class="original-price">&yen;{{ activity.originalPrice }}</span>
              </div>
              <div class="activity-meta">
                <span class="stock" :class="{ 'low-stock': activity.stock <= 10 }">
                  剩余 {{ activity.stock }} 件
                </span>
                <span class="countdown" v-if="countdowns[activity.id]">
                  {{ countdowns[activity.id] }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { smsApi } from '@/api'
import type { SeckillActivity } from '@/types'

const activities = ref<SeckillActivity[]>([])
const loading = ref(false)
const countdowns = ref<Record<number, string>>({})

let timer: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  fetchActivities()
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
})

async function fetchActivities() {
  loading.value = true
  try {
    const res = await smsApi.getSeckillList()
    activities.value = res.data
    startCountdown()
  } catch {
    ElMessage.error('获取秒杀活动列表失败')
  } finally {
    loading.value = false
  }
}

function startCountdown() {
  updateCountdowns()
  timer = setInterval(updateCountdowns, 1000)
}

function updateCountdowns() {
  const now = Date.now()
  activities.value.forEach(activity => {
    const end = new Date(activity.endTime).getTime()
    const diff = Math.max(0, end - now)
    if (diff <= 0) {
      countdowns.value[activity.id] = '已结束'
    } else {
      const hours = Math.floor(diff / 3600000)
      const minutes = Math.floor((diff % 3600000) / 60000)
      const seconds = Math.floor((diff % 60000) / 1000)
      countdowns.value[activity.id] = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
    }
  })
}
</script>

<style scoped lang="scss">
.seckill-list-page {
  padding: 24px 0;
}

.page-container {
  max-width: 900px;
  margin: 0 auto;
}

.list-card {
  .title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
}

.loading-state {
  padding: 40px;
}

.empty-state {
  padding: 40px 0;
}

.activity-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.activity-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.3s, transform 0.2s;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
}

.activity-cover {
  width: 100%;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  color: #fff;
  
  &.cover-0 {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
  
  &.cover-1 {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }
  
  &.cover-2 {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }
  
  &.cover-3 {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  }
}

.cover-content {
  text-align: center;
  padding: 20px;
}

.seckill-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.3);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  margin-bottom: 10px;
}

.cover-text {
  display: block;
  font-size: 16px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.activity-info {
  padding: 16px;

  .activity-title {
    font-size: 15px;
    font-weight: 500;
    color: #303133;
    margin: 0 0 10px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .activity-price {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin-bottom: 10px;

    .seckill-price {
      font-size: 22px;
      font-weight: 700;
      color: #f56c6c;
    }

    .original-price {
      font-size: 13px;
      color: #c0c4cc;
      text-decoration: line-through;
    }
  }

  .activity-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .stock {
      font-size: 12px;
      color: #909399;

      &.low-stock {
        color: #f56c6c;
        font-weight: 500;
      }
    }

    .countdown {
      font-size: 14px;
      font-weight: 600;
      color: #e6a23c;
      font-variant-numeric: tabular-nums;
    }
  }
}
</style>