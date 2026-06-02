<template>
  <div class="confirm-page">
    <div class="page-container">
      <el-card class="confirm-card">
        <template #header>
          <span class="title">确认订单</span>
        </template>

        <div class="order-items" v-if="items.length > 0">
          <div class="section-title">课程信息</div>
          <div class="item-list">
            <div class="order-item" v-for="item in items" :key="item.id">
              <el-image class="item-cover" :src="MINIO_COURSE_COVER(item.courseCover)" fit="cover" />
              <div class="item-detail">
                <h4>{{ item.courseTitle }}</h4>
                <span class="item-price">&yen;{{ item.coursePrice }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="order-summary">
          <div class="summary-row">
            <span class="summary-label">课程数量</span>
            <span>{{ totalCount }} 门</span>
          </div>
          <div class="summary-row total">
            <span class="summary-label">订单总额</span>
            <span class="summary-price">&yen;{{ totalAmount.toFixed(2) }}</span>
          </div>
        </div>

        <div class="submit-section">
          <el-button @click="$router.back()">返回</el-button>
          <el-button type="primary" size="large" :loading="submitting" @click="handleSubmit">提交订单</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { omsApi } from '@/api'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { MINIO_COURSE_COVER } from '@/const'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const items = computed(() => cartStore.items)
const totalAmount = computed(() => cartStore.items.reduce((sum, item) => sum + item.coursePrice, 0))
const totalCount = computed(() => cartStore.items.length)

const submitting = computed(() => false)

onMounted(() => {
  if (cartStore.items.length === 0) {
    cartStore.fetchCart()
  }
})

let _submitting = false

async function handleSubmit() {
  if (_submitting) return
  if (items.value.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  _submitting = true
  try {
    const fkUserId = userStore.userInfo?.id || 0
    const courseIds = items.value.map(item => item.fkCourseId)
    const res = await omsApi.createOrder({
      fkUserId,
      courseIds,
      totalAmount: totalAmount.value
    })
    ElMessage.success('订单创建成功')
    await cartStore.fetchCart()
    router.push(`/orders/pay/${res.data.id}`)
  } catch {
    ElMessage.error('创建订单失败')
  } finally {
    _submitting = false
  }
}
</script>

<style scoped lang="scss">
.confirm-page {
  padding: 24px 0;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
}

.confirm-card {
  .title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
}

.section-title {
  font-size: 15px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.item-list {
  .order-item {
    display: flex;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid #f5f5f5;
    gap: 16px;

    &:last-child {
      border-bottom: none;
    }
  }

  .item-cover {
    width: 100px;
    height: 56px;
    border-radius: 4px;
    overflow: hidden;
    flex-shrink: 0;
  }

  .item-detail {
    flex: 1;
    min-width: 0;

    h4 {
      margin: 0 0 6px;
      font-size: 14px;
      color: #303133;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .item-price {
      font-size: 13px;
      color: #f56c6c;
    }
  }

  .item-quantity {
    font-size: 13px;
    color: #909399;
    width: 60px;
    text-align: center;
  }

  .item-subtotal {
    font-size: 15px;
    color: #f56c6c;
    font-weight: 500;
    width: 100px;
    text-align: right;
  }
}

.order-summary {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;

  .summary-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 6px 0;
    font-size: 14px;
    color: #606266;

    &.total {
      margin-top: 8px;
      padding-top: 12px;
      border-top: 1px dashed #e4e7ed;
    }

    .summary-price {
      font-size: 22px;
      font-weight: 700;
      color: #f56c6c;
    }
  }
}

.submit-section {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
</style>