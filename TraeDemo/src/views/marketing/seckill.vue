<template>
  <div class="seckill-page page-container">
    <div class="container">
      <div class="page-header">
        <div class="seckill-title">
          <el-icon :size="32"><Lightning /></el-icon>
          <h1>秒杀专区</h1>
        </div>
        <p>限时抢购，错过不再有</p>
      </div>

      <div class="seckill-timer" v-if="seckillList.length > 0">
        <span class="label">距离结束</span>
        <div class="countdown">
          <span class="time-block">{{ timeRemaining.hours }}</span>
          <span class="separator">:</span>
          <span class="time-block">{{ timeRemaining.minutes }}</span>
          <span class="separator">:</span>
          <span class="time-block">{{ timeRemaining.seconds }}</span>
        </div>
      </div>

      <div class="seckill-grid" v-loading="loading">
        <div
          v-for="item in seckillList"
          :key="item.id"
          class="seckill-card"
          @click="goToDetail(item.id)"
        >
          <div class="seckill-image">
            <img :src="item.image" :alt="item.title" />
            <div class="seckill-tag">秒杀</div>
          </div>
          <div class="seckill-info">
            <h3 class="seckill-title">{{ item.title }}</h3>
            <p class="seckill-desc">{{ item.description }}</p>
            <div class="seckill-price">
              <span class="current-price">¥{{ item.seckillPrice }}</span>
              <span class="original-price">¥{{ item.originalPrice }}</span>
            </div>
            <el-progress
              :percentage="Math.round((item.soldCount / item.totalCount) * 100)"
              :stroke-width="6"
              :show-text="false"
              color="#F56C6C"
            />
            <div class="seckill-meta">
              <span>已抢 {{ item.soldCount }} 件</span>
              <span>剩余 {{ item.totalCount - item.soldCount }} 件</span>
            </div>
            <el-button type="danger" class="seckill-btn">立即抢购</el-button>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && seckillList.length === 0" description="暂无秒杀活动" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { marketingApi } from '@/api/marketing'
import { Lightning } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const seckillList = ref([])

const timeRemaining = reactive({
  hours: '00',
  minutes: '00',
  seconds: '00'
})

let timer = null

const loadSeckillList = async () => {
  loading.value = true
  try {
    const res = await marketingApi.getSeckillList()
    seckillList.value = res.data?.records || []
    updateTimeRemaining()
  } catch (error) {
    ElMessage.error('加载秒杀列表失败')
  } finally {
    loading.value = false
  }
}

const updateTimeRemaining = () => {
  if (seckillList.value.length === 0) return

  const nearestEndTime = Math.min(...seckillList.value.map(item => new Date(item.endTime).getTime()))
  const now = new Date().getTime()
  const diff = nearestEndTime - now

  if (diff > 0) {
    const hours = Math.floor(diff / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    const seconds = Math.floor((diff % (1000 * 60)) / 1000)
    timeRemaining.hours = String(hours).padStart(2, '0')
    timeRemaining.minutes = String(minutes).padStart(2, '0')
    timeRemaining.seconds = String(seconds).padStart(2, '0')
  }
}

const goToDetail = (id) => {
  router.push(`/marketing/seckill/${id}`)
}

onMounted(() => {
  loadSeckillList()
  timer = setInterval(updateTimeRemaining, 1000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style lang="scss" scoped>
.seckill-page {
  padding: 40px 0;

  .page-header {
    text-align: center;
    margin-bottom: 32px;

    .seckill-title {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12px;
      color: #F56C6C;

      h1 {
        font-size: 28px;
        margin: 0;
      }
    }

    p {
      color: #909399;
      font-size: 14px;
      margin-top: 8px;
    }
  }
}

.seckill-timer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 32px;

  .label {
    color: #606266;
    font-size: 14px;
  }

  .countdown {
    display: flex;
    align-items: center;

    .time-block {
      background: #F56C6C;
      color: #fff;
      font-size: 20px;
      font-weight: 700;
      padding: 8px 12px;
      border-radius: 4px;
      min-width: 40px;
      text-align: center;
    }

    .separator {
      font-size: 20px;
      font-weight: 700;
      color: #F56C6C;
      margin: 0 4px;
    }
  }
}

.seckill-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;

  .seckill-card {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    }

    .seckill-image {
      position: relative;
      height: 180px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .seckill-tag {
        position: absolute;
        top: 12px;
        left: 12px;
        background: #F56C6C;
        color: #fff;
        padding: 4px 12px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 600;
      }
    }

    .seckill-info {
      padding: 16px;

      .seckill-title {
        font-size: 16px;
        color: #303133;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .seckill-desc {
        font-size: 13px;
        color: #909399;
        margin-bottom: 12px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .seckill-price {
        margin-bottom: 12px;

        .current-price {
          color: #F56C6C;
          font-size: 22px;
          font-weight: 700;
          margin-right: 8px;
        }

        .original-price {
          color: #909399;
          font-size: 14px;
          text-decoration: line-through;
        }
      }

      .seckill-meta {
        display: flex;
        justify-content: space-between;
        font-size: 12px;
        color: #909399;
        margin: 8px 0 12px;
      }

      .seckill-btn {
        width: 100%;
      }
    }
  }
}
</style>
