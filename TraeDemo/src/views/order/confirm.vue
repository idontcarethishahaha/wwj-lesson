<template>
  <div class="confirm-page page-container">
    <div class="container">
      <div class="page-header">
        <h1>确认订单</h1>
      </div>

      <div class="confirm-content">
        <div class="order-items">
          <h3>订单课程</h3>
          <div
            v-for="item in selectedItems"
            :key="item.id"
            class="order-item"
          >
            <img :src="item.courseCover" :alt="item.courseName" />
            <div class="item-info">
              <h4>{{ item.courseName }}</h4>
              <p>{{ item.teacherName }}</p>
            </div>
            <div class="item-price">
              <span v-if="item.price > 0">¥{{ item.price }}</span>
              <span class="free" v-else>免费</span>
            </div>
          </div>
        </div>

        <div class="coupon-section">
          <h3>优惠券</h3>
          <el-select
            v-model="selectedCouponId"
            placeholder="选择优惠券"
            clearable
            @change="handleCouponChange"
          >
            <el-option
              v-for="coupon in availableCoupons"
              :key="coupon.id"
              :label="`${coupon.name}（满${coupon.minAmount}减${coupon.value}）`"
              :value="coupon.id"
            />
          </el-select>
        </div>

        <div class="order-summary">
          <div class="summary-row">
            <span>商品总价</span>
            <span>¥{{ totalPrice }}</span>
          </div>
          <div class="summary-row" v-if="couponDiscount > 0">
            <span>优惠券抵扣</span>
            <span class="discount">-¥{{ couponDiscount }}</span>
          </div>
          <div class="summary-row total">
            <span>应付金额</span>
            <span class="final-price">¥{{ finalPrice }}</span>
          </div>
        </div>

        <div class="order-actions">
          <el-button @click="goBack">返回购物车</el-button>
          <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">
            提交订单
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { orderApi } from '@/api/order'
import { marketingApi } from '@/api/marketing'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()

const submitting = ref(false)
const selectedCouponId = ref(null)
const availableCoupons = ref([])
const couponDiscount = ref(0)

const selectedItems = computed(() => cartStore.getSelectedItems())

const totalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + (item.price || 0), 0)
})

const finalPrice = computed(() => {
  return Math.max(0, totalPrice.value - couponDiscount.value)
})

const goBack = () => {
  router.push('/cart')
}

const loadCoupons = async () => {
  try {
    const res = await marketingApi.getMyCoupons()
    availableCoupons.value = (res.data || []).filter(c => c.status === 0 && c.minAmount <= totalPrice.value)
  } catch (error) {
    console.error('加载优惠券失败:', error)
  }
}

const handleCouponChange = (couponId) => {
  if (!couponId) {
    couponDiscount.value = 0
    return
  }
  const coupon = availableCoupons.value.find(c => c.id === couponId)
  if (coupon) {
    couponDiscount.value = coupon.type === 1 ? coupon.value : Math.floor(totalPrice.value * (10 - coupon.value) / 10)
  }
}

const handleSubmit = async () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请选择要购买的课程')
    return
  }

  submitting.value = true
  try {
    const courseIds = selectedItems.value.map(item => item.courseId)
    const res = await orderApi.createOrder({
      courseIds,
      couponId: selectedCouponId.value
    })

    for (const id of cartStore.selectedIds) {
      await cartStore.removeFromCart(id)
    }

    ElMessage.success('订单创建成功')
    router.push(`/order/pay/${res.data.orderId}`)
  } catch (error) {
    ElMessage.error(error.message || '订单创建失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (selectedItems.value.length === 0) {
    router.push('/cart')
    return
  }
  loadCoupons()
})
</script>

<style lang="scss" scoped>
.confirm-page {
  padding: 40px 0;

  .page-header {
    margin-bottom: 30px;

    h1 {
      font-size: 24px;
      color: #303133;
    }
  }
}

.confirm-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 30px;

  .order-items {
    margin-bottom: 30px;

    h3 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 20px;
      padding-bottom: 12px;
      border-bottom: 1px solid #EBEEF5;
    }

    .order-item {
      display: flex;
      align-items: center;
      padding: 16px 0;
      border-bottom: 1px solid #EBEEF5;

      &:last-child {
        border-bottom: none;
      }

      img {
        width: 120px;
        height: 80px;
        border-radius: 8px;
        object-fit: cover;
        margin-right: 16px;
      }

      .item-info {
        flex: 1;

        h4 {
          font-size: 16px;
          color: #303133;
          margin-bottom: 6px;
        }

        p {
          font-size: 13px;
          color: #909399;
        }
      }

      .item-price {
        font-size: 18px;
        font-weight: 600;
        color: #F56C6C;

        .free {
          color: #67C23A;
        }
      }
    }
  }

  .coupon-section {
    margin-bottom: 30px;
    padding-bottom: 30px;
    border-bottom: 1px solid #EBEEF5;

    h3 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 16px;
    }
  }

  .order-summary {
    margin-bottom: 30px;

    .summary-row {
      display: flex;
      justify-content: space-between;
      padding: 12px 0;
      font-size: 14px;
      color: #606266;

      .discount {
        color: #67C23A;
      }

      &.total {
        border-top: 1px solid #EBEEF5;
        margin-top: 12px;
        padding-top: 20px;
        font-size: 16px;
        font-weight: 600;

        .final-price {
          font-size: 28px;
          color: #F56C6C;
        }
      }
    }
  }

  .order-actions {
    display: flex;
    justify-content: flex-end;
    gap: 16px;

    :deep(.el-button--large) {
      padding: 16px 40px;
    }
  }
}
</style>
