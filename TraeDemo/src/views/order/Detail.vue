<template>
  <div class="order-detail-page">
    <div class="page-container">
      <el-card v-if="loading" class="detail-card">
        <el-skeleton :rows="5" animated />
      </el-card>

      <el-card v-else-if="order" class="detail-card">
        <template #header>
          <div class="detail-header">
            <span class="title">订单详情</span>
            <el-tag :type="getStatusType(order.status)">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
        </template>

        <div class="info-section">
          <div class="section-title">订单信息</div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ order.sn }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ order.created }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(order.status)" size="small">
                {{ getStatusText(order.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="订单金额">
              <span class="amount">&yen;{{ order.totalAmount.toFixed(2) }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="items-section">
          <div class="section-title">课程明细</div>
          <div class="item-list">
            <div class="item-row" v-for="item in order.orderDetails" :key="item.courseId">
              <el-image class="item-cover" :src="MINIO_COURSE_COVER(item.courseCover)" fit="cover" />
              <div class="item-info">
                <span class="item-title">{{ item.courseTitle }}</span>
              </div>
              <span class="item-price">&yen;{{ item.coursePrice }}</span>
            </div>
          </div>
          <div class="total-row">
            <span class="total-label">合计：</span>
            <span class="total-price">&yen;{{ order.totalAmount.toFixed(2) }}</span>
          </div>
        </div>

        <div class="action-section">
          <el-button @click="$router.back()">返回</el-button>
          <el-button v-if="order.status === 0" type="primary" @click="$router.push(`/orders/pay/${order.id}`)">去支付</el-button>
        </div>
      </el-card>

      <el-card v-else class="detail-card">
        <el-result icon="error" title="获取订单信息失败">
          <template #extra>
            <el-button type="primary" @click="fetchDetail">重新获取</el-button>
          </template>
        </el-result>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { omsApi } from '@/api'
import { MINIO_COURSE_COVER } from '@/const'
import type { Order } from '@/types'

const route = useRoute()

const order = ref<Order | null>(null)
const loading = ref(true)

onMounted(() => {
  fetchDetail()
})

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

async function fetchDetail() {
  loading.value = true
  try {
    const res = await omsApi.getOrderDetail(Number(route.params.id))
    order.value = res.data
  } catch {
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.order-detail-page {
  padding: 24px 0;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
}

.detail-card {
  .detail-header {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }
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

.info-section {
  margin-bottom: 24px;

  .amount {
    font-size: 15px;
    color: #f56c6c;
    font-weight: 600;
  }
}

.items-section {
  margin-bottom: 24px;

  .item-list {
    border: 1px solid #ebeef5;
    border-radius: 4px;
  }

  .item-row {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;

    & + .item-row {
      border-top: 1px solid #f5f5f5;
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
  }

  .total-row {
    text-align: right;
    padding: 16px;
    border-top: 1px dashed #e4e7ed;

    .total-label {
      font-size: 14px;
      color: #606266;
    }

    .total-price {
      font-size: 20px;
      font-weight: 700;
      color: #f56c6c;
    }
  }
}

.action-section {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}
</style>