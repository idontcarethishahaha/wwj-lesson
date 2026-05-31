<template>
  <div class="coupon-page page-container">
    <div class="container">
      <div class="page-header">
        <h1>优惠券中心</h1>
        <p>领取优惠券，享受更多优惠</p>
      </div>

      <div class="coupon-grid" v-loading="loading">
        <div
          v-for="coupon in couponList"
          :key="coupon.id"
          class="coupon-card"
          :class="{ disabled: coupon.received || coupon.stock <= 0 }"
        >
          <div class="coupon-left">
            <div class="coupon-value">
              <span class="amount">{{ coupon.type === 1 ? '¥' : '' }}{{ coupon.value }}</span>
              <span class="unit">{{ coupon.type === 2 ? '折' : '' }}</span>
            </div>
            <div class="coupon-condition">
              满{{ coupon.minAmount }}可用
            </div>
          </div>
          <div class="coupon-right">
            <div class="coupon-name">{{ coupon.name }}</div>
            <div class="coupon-desc">{{ coupon.description }}</div>
            <div class="coupon-time">
              {{ formatTime(coupon.startTime) }} - {{ formatTime(coupon.endTime) }}
            </div>
            <div class="coupon-stock">
              剩余 <span>{{ coupon.stock }}</span> 张
            </div>
            <el-button
              type="danger"
              size="small"
              :disabled="coupon.received || coupon.stock <= 0"
              @click="receiveCoupon(coupon.id)"
            >
              {{ coupon.received ? '已领取' : '立即领取' }}
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && couponList.length === 0" description="暂无优惠券" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { marketingApi } from '@/api/marketing'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const couponList = ref([])

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}-${date.getDate()}`
}

const loadCoupons = async () => {
  loading.value = true
  try {
    const res = await marketingApi.getCouponList()
    couponList.value = res.data || []
  } catch (error) {
    ElMessage.error('加载优惠券失败')
  } finally {
    loading.value = false
  }
}

const receiveCoupon = async (id) => {
  try {
    await marketingApi.receiveCoupon(id)
    ElMessage.success('领取成功')
    await loadCoupons()
  } catch (error) {
    ElMessage.error(error.message || '领取失败')
  }
}

onMounted(() => {
  loadCoupons()
})
</script>

<style lang="scss" scoped>
.coupon-page {
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

.coupon-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;

  .coupon-card {
    display: flex;
    background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(255, 107, 107, 0.3);
    transition: all 0.3s;

    &:hover:not(.disabled) {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(255, 107, 107, 0.4);
    }

    &.disabled {
      background: linear-gradient(135deg, #999 0%, #777 100%);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    }

    .coupon-left {
      width: 120px;
      padding: 20px;
      text-align: center;
      color: #fff;
      border-right: 1px dashed rgba(255, 255, 255, 0.5);
      display: flex;
      flex-direction: column;
      justify-content: center;

      .coupon-value {
        .amount {
          font-size: 36px;
          font-weight: 700;
        }

        .unit {
          font-size: 16px;
        }
      }

      .coupon-condition {
        font-size: 12px;
        margin-top: 8px;
        opacity: 0.9;
      }
    }

    .coupon-right {
      flex: 1;
      padding: 16px;
      color: #fff;

      .coupon-name {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 6px;
      }

      .coupon-desc {
        font-size: 12px;
        opacity: 0.9;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .coupon-time {
        font-size: 11px;
        opacity: 0.8;
        margin-bottom: 8px;
      }

      .coupon-stock {
        font-size: 12px;
        margin-bottom: 10px;

        span {
          font-weight: 600;
        }
      }

      :deep(.el-button--small) {
        padding: 6px 16px;
      }
    }
  }
}
</style>
