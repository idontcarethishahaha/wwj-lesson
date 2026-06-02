<template>
  <div class="pay-page">
    <div class="page-container">
      <el-card class="pay-card">
        <template #header>
          <span class="title">支付</span>
        </template>

        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="3" animated />
        </div>

        <div v-else-if="qrCodeBase64" class="pay-content">
          <el-result icon="success" title="订单已创建" sub-title="请使用扫码支付完成交易">
            <template #extra>
              <div class="qr-section">
                <div class="qr-wrapper">
                  <img :src="qrCodeBase64" alt="支付二维码" class="qr-image" />
                </div>
                <p class="qr-tip">请使用支付工具扫码支付</p>
              </div>
              <div class="order-info">
                <p>订单号：{{ orderNo }}</p>
                <p>金额：&yen;{{ orderAmount }}</p>
              </div>
              <el-button type="info" plain @click="handleViewOrder">查看订单</el-button>
            </template>
          </el-result>
        </div>

        <div v-else class="error-state">
          <el-result icon="error" title="获取支付信息失败">
            <template #extra>
              <el-button type="primary" @click="fetchPayInfo">重新获取</el-button>
            </template>
          </el-result>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { omsApi } from '@/api'

const route = useRoute()
const router = useRouter()

const orderId = Number(route.params.id)
const qrCodeBase64 = ref('')
const orderNo = ref('')
const orderAmount = ref(0)
const loading = ref(true)

let pollingTimer: ReturnType<typeof setInterval> | null = null

onMounted(() => {
  fetchPayInfo()
})

onUnmounted(() => {
  stopPolling()
})

async function fetchPayInfo() {
  loading.value = true
  try {
    const detailRes = await omsApi.getOrderDetail(orderId)
    orderNo.value = detailRes.data.sn
    orderAmount.value = detailRes.data.totalAmount
    const blob = await omsApi.getPayQrCode({
      sn: detailRes.data.sn,
      payAmount: detailRes.data.totalAmount
    })
    qrCodeBase64.value = URL.createObjectURL(blob)
    // 打印二维码base64编码
    console.log(qrCodeBase64.value)
    startPolling()
  } catch {
    ElMessage.error('获取支付信息失败')
  } finally {
    loading.value = false
  }
}

function startPolling() {
  pollingTimer = setInterval(async () => {
    try {
      const res = await omsApi.getOrderStatus(orderId)
      if (res.data.status === 1) {
        ElMessage.success('支付成功')
        stopPolling()
        router.replace(`/orders/${orderId}`)
      }
    } catch {
      stopPolling()
    }
  }, 3000)
}

function stopPolling() {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

function handleViewOrder() {
  router.push(`/orders/${orderId}`)
}
</script>

<style scoped lang="scss">
.pay-page {
  padding: 24px 0;
}

.page-container {
  max-width: 600px;
  margin: 0 auto;
}

.pay-card {
  .title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
}

.loading-state {
  padding: 40px;
}

.pay-content {
  .qr-section {
    margin-bottom: 16px;

    .qr-wrapper {
      display: inline-block;
      padding: 16px;
      border: 1px solid #e4e7ed;
      border-radius: 8px;
      background: #fff;
    }

    .qr-image {
      width: 200px;
      height: 200px;
      display: block;
    }

    .qr-tip {
      margin-top: 12px;
      color: #909399;
      font-size: 13px;
    }
  }

  .order-info {
    margin: 16px 0;
    font-size: 14px;
    color: #606266;

    p {
      margin: 4px 0;
    }
  }
}

.error-state {
  padding: 20px 0;
}
</style>