<template>
  <div class="seckill-detail-page page-container" v-loading="loading">
    <div class="container">
      <div class="detail-header">
        <el-button text @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>

      <div class="seckill-content" v-if="seckillInfo.id">
        <div class="seckill-image">
          <img :src="seckillInfo.image" :alt="seckillInfo.title" />
        </div>
        <div class="seckill-info">
          <h1 class="seckill-title">{{ seckillInfo.title }}</h1>
          <p class="seckill-desc">{{ seckillInfo.description }}</p>

          <div class="seckill-price-info">
            <div class="price-row">
              <span class="label">秒杀价</span>
              <span class="seckill-price">¥{{ seckillInfo.seckillPrice }}</span>
              <span class="original-price">¥{{ seckillInfo.originalPrice }}</span>
            </div>
            <div class="stock-row">
              <span class="label">剩余库存</span>
              <span class="stock-count">{{ seckillInfo.stock }}</span>
            </div>
            <div class="time-row">
              <span class="label">活动时间</span>
              <span class="time-value">
                {{ formatTime(seckillInfo.startTime) }} - {{ formatTime(seckillInfo.endTime) }}
              </span>
            </div>
          </div>

          <div class="seckill-progress">
            <el-progress
              :percentage="Math.round((seckillInfo.soldCount / seckillInfo.totalCount) * 100)"
              :stroke-width="10"
              color="#F56C6C"
            />
            <div class="progress-text">
              <span>已抢购 {{ seckillInfo.soldCount }} 件</span>
              <span>剩余 {{ seckillInfo.stock }} 件</span>
            </div>
          </div>

          <div class="seckill-actions">
            <el-button type="danger" size="large" :loading="buying" @click="handleBuy">
              立即抢购
            </el-button>
          </div>
        </div>
      </div>

      <div class="seckill-rules">
        <h3>秒杀规则</h3>
        <ul>
          <li>1. 秒杀商品数量有限，先到先得</li>
          <li>2. 每个用户限购1件</li>
          <li>3. 秒杀订单支付有效期为15分钟，超时自动取消</li>
          <li>4. 秒杀商品不可使用优惠券</li>
          <li>5. 秒杀活动最终解释权归平台所有</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { marketingApi } from '@/api/marketing'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const buying = ref(false)
const seckillInfo = ref({})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
}

const goBack = () => {
  router.back()
}

const loadSeckillDetail = async () => {
  loading.value = true
  try {
    const res = await marketingApi.getSeckillDetail(route.params.id)
    seckillInfo.value = res.data || {}
  } catch (error) {
    ElMessage.error('加载秒杀详情失败')
  } finally {
    loading.value = false
  }
}

const handleBuy = async () => {
  buying.value = true
  try {
    const res = await marketingApi.createSeckillOrder({
      seckillId: seckillInfo.value.id,
      courseId: seckillInfo.value.courseId
    })
    ElMessage.success('下单成功')
    router.push(`/order/pay/${res.data.orderId}`)
  } catch (error) {
    ElMessage.error(error.message || '抢购失败')
  } finally {
    buying.value = false
  }
}

onMounted(() => {
  loadSeckillDetail()
})
</script>

<style lang="scss" scoped>
.seckill-detail-page {
  padding: 40px 0;

  .detail-header {
    margin-bottom: 20px;
  }
}

.seckill-content {
  display: flex;
  gap: 40px;
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;

  .seckill-image {
    width: 400px;
    height: 300px;
    border-radius: 8px;
    overflow: hidden;
    flex-shrink: 0;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .seckill-info {
    flex: 1;

    .seckill-title {
      font-size: 24px;
      color: #303133;
      margin-bottom: 12px;
    }

    .seckill-desc {
      font-size: 14px;
      color: #909399;
      margin-bottom: 24px;
    }

    .seckill-price-info {
      margin-bottom: 24px;

      .price-row {
        display: flex;
        align-items: baseline;
        margin-bottom: 12px;

        .label {
          color: #909399;
          font-size: 14px;
          margin-right: 16px;
          width: 70px;
        }

        .seckill-price {
          color: #F56C6C;
          font-size: 32px;
          font-weight: 700;
          margin-right: 16px;
        }

        .original-price {
          color: #909399;
          font-size: 18px;
          text-decoration: line-through;
        }
      }

      .stock-row, .time-row {
        display: flex;
        align-items: center;
        margin-bottom: 12px;

        .label {
          color: #909399;
          font-size: 14px;
          margin-right: 16px;
          width: 70px;
        }

        .stock-count {
          color: #F56C6C;
          font-size: 18px;
          font-weight: 600;
        }

        .time-value {
          color: #606266;
          font-size: 14px;
        }
      }
    }

    .seckill-progress {
      margin-bottom: 24px;

      .progress-text {
        display: flex;
        justify-content: space-between;
        margin-top: 8px;
        font-size: 13px;
        color: #909399;
      }
    }

    .seckill-actions {
      :deep(.el-button--large) {
        padding: 16px 60px;
      }
    }
  }
}

.seckill-rules {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  h3 {
    font-size: 18px;
    color: #303133;
    margin-bottom: 16px;
  }

  ul {
    li {
      color: #606266;
      font-size: 14px;
      line-height: 2;
      padding-left: 20px;
      position: relative;

      &::before {
        content: '•';
        position: absolute;
        left: 0;
        color: #409EFF;
      }
    }
  }
}
</style>
