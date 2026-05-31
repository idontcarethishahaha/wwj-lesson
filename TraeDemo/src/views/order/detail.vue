<template>
  <div class="order-detail-page page-container" v-loading="loading">
    <div class="container">
      <div class="detail-header">
        <el-button text @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回订单列表
        </el-button>
      </div>

      <div class="order-content" v-if="orderInfo.id">
        <div class="order-status">
          <el-steps :active="getStepActive(orderInfo.status)" align-center>
            <el-step title="提交订单" :description="formatTime(orderInfo.createTime)" />
            <el-step title="完成支付" :description="formatTime(orderInfo.payTime)" />
            <el-step title="开始学习" description="" />
          </el-steps>
        </div>

        <div class="order-info-card">
          <h3>订单信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">订单号</span>
              <span class="value">{{ orderInfo.orderNo }}</span>
            </div>
            <div class="info-item">
              <span class="label">订单状态</span>
              <el-tag :type="getStatusType(orderInfo.status)">
                {{ getStatusText(orderInfo.status) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">下单时间</span>
              <span class="value">{{ formatFullTime(orderInfo.createTime) }}</span>
            </div>
            <div class="info-item" v-if="orderInfo.payTime">
              <span class="label">支付时间</span>
              <span class="value">{{ formatFullTime(orderInfo.payTime) }}</span>
            </div>
          </div>
        </div>

        <div class="order-items-card">
          <h3>订单课程</h3>
          <div
            v-for="item in orderInfo.items"
            :key="item.id"
            class="order-item"
            @click="goToCourse(item.courseId)"
          >
            <img :src="item.courseCover" :alt="item.courseName" />
            <div class="item-info">
              <h4>{{ item.courseName }}</h4>
              <p>{{ item.teacherName }}</p>
            </div>
            <div class="item-price">¥{{ item.price }}</div>
          </div>
        </div>

        <div class="order-summary-card">
          <h3>订单金额</h3>
          <div class="summary-rows">
            <div class="summary-row">
              <span>商品总价</span>
              <span>¥{{ orderInfo.totalAmount }}</span>
            </div>
            <div class="summary-row" v-if="orderInfo.couponDiscount > 0">
              <span>优惠券抵扣</span>
              <span class="discount">-¥{{ orderInfo.couponDiscount }}</span>
            </div>
            <div class="summary-row total">
              <span>实付金额</span>
              <span class="final-price">¥{{ orderInfo.payAmount }}</span>
            </div>
          </div>
        </div>

        <div class="order-actions" v-if="orderInfo.status === 'unpaid'">
          <el-button size="large" @click="handleCancel">取消订单</el-button>
          <el-button type="primary" size="large" @click="goToPay">去支付</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { orderApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const orderInfo = ref({})

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatFullTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

const getStepActive = (status) => {
  const stepMap = {
    unpaid: 0,
    paid: 1,
    cancelled: -1
  }
  return stepMap[status] ?? 0
}

const getStatusType = (status) => {
  const typeMap = {
    unpaid: 'warning',
    paid: 'success',
    cancelled: 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    unpaid: '待支付',
    paid: '已支付',
    cancelled: '已取消'
  }
  return textMap[status] || status
}

const goBack = () => {
  router.push('/order/list')
}

const goToCourse = (courseId) => {
  router.push(`/course/detail/${courseId}`)
}

const goToPay = () => {
  router.push(`/order/pay/${orderInfo.value.id}`)
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '继续支付',
      type: 'warning'
    })
    await orderApi.cancelOrder(orderInfo.value.id)
    ElMessage.success('订单已取消')
    await loadOrderDetail()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

const loadOrderDetail = async () => {
  loading.value = true
  try {
    const res = await orderApi.getOrderDetail(route.params.id)
    orderInfo.value = res.data || {}
  } catch (error) {
    ElMessage.error('加载订单详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style lang="scss" scoped>
.order-detail-page {
  padding: 40px 0;

  .detail-header {
    margin-bottom: 20px;
  }
}

.order-content {
  .order-status {
    background: #fff;
    padding: 40px;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    margin-bottom: 20px;
  }

  .order-info-card, .order-items-card, .order-summary-card {
    background: #fff;
    padding: 24px;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    margin-bottom: 20px;

    h3 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 20px;
      padding-bottom: 12px;
      border-bottom: 1px solid #EBEEF5;
    }
  }

  .order-info-card {
    .info-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;

      .info-item {
        display: flex;
        align-items: center;
        gap: 12px;

        .label {
          color: #909399;
          font-size: 14px;
        }

        .value {
          color: #303133;
          font-size: 14px;
        }
      }
    }
  }

  .order-items-card {
    .order-item {
      display: flex;
      align-items: center;
      padding: 16px 0;
      border-bottom: 1px solid #EBEEF5;
      cursor: pointer;
      transition: background-color 0.3s;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background-color: #F5F7FA;
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
      }
    }
  }

  .order-summary-card {
    .summary-rows {
      .summary-row {
        display: flex;
        justify-content: space-between;
        padding: 10px 0;
        font-size: 14px;
        color: #606266;

        .discount {
          color: #67C23A;
        }

        &.total {
          border-top: 1px solid #EBEEF5;
          margin-top: 10px;
          padding-top: 20px;
          font-size: 16px;
          font-weight: 600;

          .final-price {
            font-size: 24px;
            color: #F56C6C;
          }
        }
      }
    }
  }

  .order-actions {
    display: flex;
    justify-content: center;
    gap: 16px;

    :deep(.el-button--large) {
      padding: 16px 40px;
    }
  }
}
</style>
