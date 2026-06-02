<template>
  <div class="order-list-page">
    <div class="page-container">
      <el-card class="list-card">
        <template #header>
          <div class="list-header">
            <span class="title">我的订单</span>
          </div>
        </template>

        <el-tabs v-model="activeStatus" @tab-change="handleTabChange">
          <el-tab-pane label="全部" :name="''" />
          <el-tab-pane label="待支付" :name="'0'" />
          <el-tab-pane label="已支付" :name="'1'" />
          <el-tab-pane label="已取消" :name="'2'" />
        </el-tabs>

        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="5" animated />
        </div>

        <div v-else-if="orders.length === 0" class="empty-state">
          <el-empty description="暂无订单" />
        </div>

        <div v-else class="order-list">
          <div class="order-card" v-for="order in orders" :key="order.id">
            <div class="order-header">
              <div class="order-meta">
                <span class="order-no">订单号：{{ order.sn }}</span>
                <span class="order-time">{{ order.created }}</span>
              </div>
              <el-tag :type="getStatusType(order.status)" size="small">
                {{ getStatusText(order.status) }}
              </el-tag>
            </div>
            <div class="order-body">
              <div class="order-items">
                <div class="order-item" v-for="item in order.orderDetails" :key="item.courseId">
                  <el-image class="item-cover" :src="MINIO_COURSE_COVER(item.courseCover)" fit="cover" />
                  <div class="item-info">
                    <span class="item-title">{{ item.courseTitle }}</span>
                  </div>
                  <span class="item-price">&yen;{{ item.coursePrice }}</span>
                </div>
              </div>
              <div class="order-total">
                <span class="total-label">合计：</span>
                <span class="total-price">&yen;{{ order.totalAmount.toFixed(2) }}</span>
              </div>
            </div>
            <div class="order-footer">
              <el-button size="small" @click="$router.push(`/orders/${order.id}`)">订单详情</el-button>
              <template v-if="order.status === 0">
                <el-button size="small" type="danger" plain @click="handleCancel(order.id)">取消订单</el-button>
                <el-button size="small" type="primary" @click="$router.push(`/orders/pay/${order.id}`)">去支付</el-button>
              </template>
            </div>
          </div>
        </div>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="prev, pager, next, total"
              background
              @current-change="handlePageChange"
          />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { omsApi } from '@/api'
import { MINIO_COURSE_COVER } from '@/const'
import { useUserStore } from '@/stores/user'
import type { Order } from '@/types'

const userStore = useUserStore()
const activeStatus = ref('')
const orders = ref<Order[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const loading = ref(false)

onMounted(() => {
  fetchOrders()
})

async function fetchOrders() {
  loading.value = true
  try {
    const params: { status?: number; pageNum: number; pageSize: number; username?: string } = {
      pageNum: currentPage.value,
      pageSize: pageSize.value
    }
    if (activeStatus.value) {
      params.status = Number(activeStatus.value)
    }
    const userInfo = userStore.userInfo || JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (userInfo && userInfo.username) {
      params.username = userInfo.username
    }
    const res = await omsApi.getOrderList(params)
    orders.value = res.data.records
    total.value = res.data.total
  } catch {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  currentPage.value = 1
  fetchOrders()
}

function handlePageChange(page: number) {
  currentPage.value = page
  fetchOrders()
}

function getStatusText(status: number): string {
  const statusMap: Record<number, string> = {
    0: '待支付',
    1: '已支付',
    2: '已取消',
    3: '其他'
  }
  return statusMap[status] || '未知'
}

function getStatusType(status: number): string {
  const typeMap: Record<number, string> = {
    0: 'warning',
    1: 'success',
    2: 'danger',
    3: 'info'
  }
  return typeMap[status] || 'info'
}

async function handleCancel(id: number) {
  try {
    await ElMessageBox.confirm('确定要取消此订单吗？', '提示', { type: 'warning' })
    await omsApi.cancelOrder(id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch {
    if (ElMessageBox) return
  }
}
</script>

<style scoped lang="scss">
.order-list-page {
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

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;

  .order-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 12px 20px;
    background: #fafafa;
    border-bottom: 1px solid #ebeef5;
  }

  .order-meta {
    display: flex;
    align-items: center;
    gap: 20px;

    .order-no {
      font-size: 13px;
      color: #909399;
    }

    .order-time {
      font-size: 13px;
      color: #909399;
    }
  }

  .order-body {
    padding: 16px 20px;
  }

  .order-items {
    .order-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 8px 0;

      & + .order-item {
        border-top: 1px solid #f5f5f5;
      }
    }
  }

  .item-cover {
    width: 80px;
    height: 45px;
    border-radius: 4px;
    overflow: hidden;
    flex-shrink: 0;
  }

  .item-info {
    flex: 1;
    min-width: 0;

    .item-title {
      font-size: 14px;
      color: #303133;
    }
  }

  .item-price {
    font-size: 14px;
    color: #f56c6c;
    font-weight: 500;
    white-space: nowrap;
  }

  .order-total {
    text-align: right;
    padding-top: 12px;
    margin-top: 8px;
    border-top: 1px dashed #e4e7ed;

    .total-label {
      font-size: 13px;
      color: #606266;
    }

    .total-price {
      font-size: 18px;
      font-weight: 700;
      color: #f56c6c;
    }
  }

  .order-footer {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    padding: 12px 20px;
    background: #fafafa;
    border-top: 1px solid #ebeef5;
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>