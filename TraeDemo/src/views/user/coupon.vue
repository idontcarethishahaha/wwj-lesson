<template>
  <div class="my-coupon-page page-container">
    <div class="container">
      <div class="page-header">
        <h1>我的优惠券</h1>
      </div>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="可使用" name="available" />
        <el-tab-pane label="已使用" name="used" />
        <el-tab-pane label="已过期" name="expired" />
      </el-tabs>

      <div class="coupon-list" v-loading="loading">
        <div
          v-for="coupon in couponList"
          :key="coupon.id"
          class="coupon-card"
          :class="{ disabled: activeTab !== 'available' }"
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
            <div class="coupon-status" v-if="activeTab === 'used'">
              已使用
            </div>
            <div class="coupon-status expired" v-else-if="activeTab === 'expired'">
              已过期
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && couponList.length === 0" :description="emptyDescription" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { marketingApi } from '@/api/marketing'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const activeTab = ref('available')
const couponList = ref([])

const emptyDescription = computed(() => {
  switch (activeTab.value) {
    case 'available':
      return '暂无可使用的优惠券'
    case 'used':
      return '暂无已使用的优惠券'
    case 'expired':
      return '暂无已过期的优惠券'
    default:
      return '暂无优惠券'
  }
})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}-${date.getDate()}`
}

const loadMyCoupons = async () => {
  loading.value = true
  try {
    const res = await marketingApi.getMyCoupons()
    const allCoupons = res.data || []
    switch (activeTab.value) {
      case 'available':
        couponList.value = allCoupons.filter(c => c.status === 0)
        break
      case 'used':
        couponList.value = allCoupons.filter(c => c.status === 1)
        break
      case 'expired':
        couponList.value = allCoupons.filter(c => c.status === 2)
        break
    }
  } catch (error) {
    ElMessage.error('加载优惠券失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  loadMyCoupons()
}

onMounted(() => {
  loadMyCoupons()
})
</script>

<style lang="scss" scoped>
.my-coupon-page {
  padding: 40px 0;

  .page-header {
    margin-bottom: 30px;

    h1 {
      font-size: 24px;
      color: #303133;
    }
  }
}

.coupon-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;

  .coupon-card {
    display: flex;
    background: linear-gradient(135deg, #67C23A 0%, #95EC7B 100%);
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 16px rgba(103, 194, 58, 0.3);
    transition: all 0.3s;

    &:hover:not(.disabled) {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(103, 194, 58, 0.4);
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
      }

      .coupon-status {
        margin-top: 12px;
        font-size: 14px;
        font-weight: 600;

        &.expired {
          opacity: 0.7;
        }
      }
    }
  }
}
</style>
