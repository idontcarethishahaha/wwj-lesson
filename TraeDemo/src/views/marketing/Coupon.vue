<template>
  <div class="coupon-page">
    <div class="page-container">
      <el-card class="coupon-card">
        <template #header>
          <span class="title">领券中心</span>
        </template>

        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="3" animated />
        </div>

        <div v-else-if="coupons.length === 0" class="empty-state">
          <el-empty description="暂无可用优惠券" />
        </div>

        <div v-else class="coupon-list">
          <div class="coupon-item" v-for="coupon in coupons" :key="coupon.id">
            <div class="coupon-left">
              <div class="coupon-amount">
                <span class="currency">&yen;</span>
                <span class="value">{{ coupon.cpPrice }}</span>
              </div>
              <div class="coupon-condition">{{ coupon.code }}</div>
            </div>
            <div class="coupon-center">
              <div class="coupon-name">{{ coupon.title }}</div>
              <div class="coupon-time">{{ coupon.startTime }} ~ {{ coupon.endTime }}</div>
            </div>
            <div class="coupon-right">
              <el-button
                :type="coupon.received ? 'info' : 'primary'"
                :disabled="coupon.received"
                size="small"
                @click="handleReceive(coupon.id)"
              >
                {{ coupon.received ? '已领取' : '领取' }}
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { smsApi } from '@/api'
import type { Coupon } from '@/types'

const coupons = ref<Coupon[]>([])
const loading = ref(false)

onMounted(() => {
  fetchCoupons()
})

async function fetchCoupons() {
  loading.value = true
  try {
    const res = await smsApi.getCouponList()
    coupons.value = res.data
  } catch {
    ElMessage.error('获取优惠券列表失败')
  } finally {
    loading.value = false
  }
}

async function handleReceive(id: number) {
  try {
    await smsApi.receiveCoupon(id)
    ElMessage.success('领取成功')
    fetchCoupons()
  } catch {
    ElMessage.error('领取失败')
  }
}
</script>

<style scoped lang="scss">
.coupon-page {
  padding: 24px 0;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
}

.coupon-card {
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

.coupon-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.coupon-item {
  display: flex;
  align-items: center;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  background: linear-gradient(135deg, #fdf6ec 0%, #fff 40%);
  transition: box-shadow 0.3s;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  }
}

.coupon-left {
  width: 140px;
  text-align: center;
  padding: 20px 12px;
  background: linear-gradient(135deg, #f56c6c, #e6a23c);
  color: #fff;
  flex-shrink: 0;

  .coupon-amount {
    display: flex;
    align-items: baseline;
    justify-content: center;
    gap: 2px;

    .currency {
      font-size: 16px;
    }

    .value {
      font-size: 36px;
      font-weight: 700;
      line-height: 1;
    }
  }

  .coupon-condition {
    margin-top: 6px;
    font-size: 12px;
    opacity: 0.9;
  }
}

.coupon-center {
  flex: 1;
  padding: 20px 16px;
  min-width: 0;

  .coupon-name {
    font-size: 15px;
    font-weight: 500;
    color: #303133;
    margin-bottom: 8px;
  }

  .coupon-time {
    font-size: 12px;
    color: #909399;
  }
}

.coupon-right {
  padding: 20px 16px;
  flex-shrink: 0;
}
</style>