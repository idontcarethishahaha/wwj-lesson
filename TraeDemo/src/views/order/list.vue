<template>
  <div class="order-list-page page-container">
    <div class="container">
      <div class="page-header">
        <h1>我的订单</h1>
      </div>

      <el-tabs v-model="status" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="待支付" name="unpaid" />
        <el-tab-pane label="已支付" name="paid" />
        <el-tab-pane label="已取消" name="cancelled" />
      </el-tabs>

      <div class="order-list" v-loading="loading">
        <div
          v-for="order in orderList"
          :key="order.id"
          class="order-card"
          @click="goToDetail(order.id)"
        >
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span class="order-time">{{ formatTime(order.createTime) }}</span>
            <el-tag :type="getStatusType(order.status)">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
          <div class="order-items">
            <div
              v-for="item in order.items"
              :key="item.id"
              class="order-item"
            >
              <img :src="item.courseCover" :alt="item.courseName" />
              <div class="item-info">
                <h4>{{ item.courseName }}</h4>
                <p>{{ item.teacherName }}</p>
              </div>
              <div class="item-price">¥{{ item.price }}</div>
            </div>
          </div>
          <div class="order-footer">
            <div class="order-total">
              合计：<span class="total-price">¥{{ order.totalAmount }}</span>
            </div>
            <div class="order-actions" @click.stop>
              <el-button
                v-if="order.status === 'unpaid'"
                type="primary"
                size="small"
                @click="goToPay(order.id)"
              >
                去支付
              </el-button>
              <el-button
                v-if="order.status === 'unpaid'"
                size="small"
                @click="handleCancel(order.id)"
              >
                取消订单
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && orderList.length === 0" description="暂无订单" />

      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="limit"
          :total="total"
          :page-sizes="[10, 20, 30]"
          layout="total, prev, pager, next"
          @current-change="loadOrders"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const status = ref('all')
const orderList = ref([])
const page = ref(1)
const limit = ref(10)
const total = ref(0)

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
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

const loadOrders = async () => {
  loading.value = true
  try {
    const params = { page: page.value, limit: limit.value }
    if (status.value !== 'all') {
      params.status = status.value
    }
    const res = await orderApi.getOrderList(params)
    orderList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  page.value = 1
  loadOrders()
}

const goToDetail = (id) => {
  router.push(`/order/detail/${id}`)
}

const goToPay = (id) => {
  router.push(`/order/pay/${id}`)
}

const handleCancel = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '继续支付',
      type: 'warning'
    })
    await orderApi.cancelOrder(id)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style lang="scss" scoped>
.order-list-page {
  padding: 40px 0;

  .page-header {
    margin-bottom: 30px;

    h1 {
      font-size: 24px;
      color: #303133;
    }
  }
}

.order-list {
  .order-card {
    background: #fff;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 16px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    }

    .order-header {
      display: flex;
      align-items: center;
      gap: 16px;
      padding-bottom: 16px;
      border-bottom: 1px solid #EBEEF5;
      margin-bottom: 16px;

      .order-no {
        color: #909399;
        font-size: 13px;
      }

      .order-time {
        color: #909399;
        font-size: 13px;
        flex: 1;
      }
    }

    .order-items {
      .order-item {
        display: flex;
        align-items: center;
        padding: 12px 0;

        img {
          width: 80px;
          height: 60px;
          border-radius: 6px;
          object-fit: cover;
          margin-right: 12px;
        }

        .item-info {
          flex: 1;

          h4 {
            font-size: 14px;
            color: #303133;
            margin-bottom: 4px;
          }

          p {
            font-size: 12px;
            color: #909399;
          }
        }

        .item-price {
          font-size: 14px;
          color: #606266;
        }
      }
    }

    .order-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 16px;
      border-top: 1px solid #EBEEF5;

      .order-total {
        font-size: 14px;
        color: #606266;

        .total-price {
          font-size: 20px;
          font-weight: 700;
          color: #F56C6C;
        }
      }

      .order-actions {
        display: flex;
        gap: 8px;
      }
    }
  }
}

.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
</style>
