<template>
  <div class="pay-page page-container">
    <div class="container">
      <div class="page-header">
        <h1>订单支付</h1>
      </div>

      <div class="pay-content" v-loading="loading">
        <div class="order-info">
          <div class="order-number">
            订单号：<span>{{ orderInfo.orderNo }}</span>
          </div>
          <div class="order-amount">
            应付金额：<span class="amount">¥{{ orderInfo.totalAmount }}</span>
          </div>
        </div>

        <div class="pay-methods">
          <h3>选择支付方式</h3>
          <div class="method-list">
            <div
              class="method-item"
              :class="{ active: payMethod === 'alipay' }"
              @click="payMethod = 'alipay'"
            >
              <img src="@/assets/alipay.png" alt="支付宝" />
              <span>支付宝</span>
              <el-icon v-if="payMethod === 'alipay'"><Check /></el-icon>
            </div>
            <div
              class="method-item"
              :class="{ active: payMethod === 'wechat' }"
              @click="payMethod = 'wechat'"
            >
              <img src="@/assets/wechat.png" alt="微信支付" />
              <span>微信支付</span>
              <el-icon v-if="payMethod === 'wechat'"><Check /></el-icon>
            </div>
          </div>
        </div>

        <div class="pay-qrcode" v-if="showQrcode">
          <div class="qrcode-wrapper">
            <img :src="qrcodeUrl" alt="支付二维码" />
          </div>
          <p>请使用{{ payMethod === 'alipay' ? '支付宝' : '微信' }}扫描二维码支付</p>
        </div>

        <div class="pay-actions">
          <el-button size="large" @click="handleCancel">取消支付</el-button>
          <el-button type="primary" size="large" :loading="paying" @click="handlePay">
            确认支付
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { orderApi } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const paying = ref(false)
const orderInfo = ref({})
const payMethod = ref('alipay')
const showQrcode = ref(false)
const qrcodeUrl = ref('')
const pollTimer = ref(null)

const loadOrderInfo = async () => {
  loading.value = true
  try {
    const res = await orderApi.getOrderDetail(route.params.id)
    orderInfo.value = res.data || {}
  } catch (error) {
    ElMessage.error('加载订单信息失败')
  } finally {
    loading.value = false
  }
}

const handlePay = async () => {
  paying.value = true
  try {
    const res = await orderApi.payOrder(orderInfo.value.id, {
      payMethod: payMethod.value
    })

    if (res.data?.qrcode) {
      qrcodeUrl.value = res.data.qrcode
      showQrcode.value = true
      startPolling()
    } else {
      ElMessage.success('支付成功')
      router.push('/order/list')
    }
  } catch (error) {
    ElMessage.error(error.message || '支付失败')
  } finally {
    paying.value = false
  }
}

const startPolling = () => {
  pollTimer.value = setInterval(async () => {
    try {
      const res = await orderApi.getPayResult(orderInfo.value.id)
      if (res.data?.status === 'paid') {
        ElMessage.success('支付成功')
        clearInterval(pollTimer.value)
        router.push('/order/list')
      }
    } catch (error) {
      console.error('查询支付结果失败:', error)
    }
  }, 3000)
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
    router.push('/order/list')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

onMounted(() => {
  loadOrderInfo()
})

onUnmounted(() => {
  if (pollTimer.value) {
    clearInterval(pollTimer.value)
  }
})
</script>

<style lang="scss" scoped>
.pay-page {
  padding: 40px 0;

  .page-header {
    margin-bottom: 30px;

    h1 {
      font-size: 24px;
      color: #303133;
    }
  }
}

.pay-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 40px;
}

.order-info {
  text-align: center;
  padding-bottom: 30px;
  border-bottom: 1px solid #EBEEF5;
  margin-bottom: 30px;

  .order-number {
    font-size: 14px;
    color: #909399;
    margin-bottom: 12px;

    span {
      color: #303133;
    }
  }

  .order-amount {
    .amount {
      font-size: 36px;
      font-weight: 700;
      color: #F56C6C;
    }
  }
}

.pay-methods {
  margin-bottom: 30px;

  h3 {
    font-size: 18px;
    color: #303133;
    margin-bottom: 20px;
  }

  .method-list {
    display: flex;
    gap: 20px;

    .method-item {
      width: 180px;
      padding: 20px;
      border: 2px solid #EBEEF5;
      border-radius: 12px;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 12px;
      cursor: pointer;
      transition: all 0.3s;
      position: relative;

      &:hover {
        border-color: #409EFF;
      }

      &.active {
        border-color: #409EFF;
        background: rgba(64, 158, 255, 0.05);
      }

      img {
        width: 60px;
        height: 60px;
      }

      span {
        font-size: 14px;
        color: #303133;
      }

      .el-icon {
        position: absolute;
        top: 8px;
        right: 8px;
        color: #409EFF;
        font-size: 20px;
      }
    }
  }
}

.pay-qrcode {
  text-align: center;
  padding: 30px;
  background: #f5f7fa;
  border-radius: 12px;
  margin-bottom: 30px;

  .qrcode-wrapper {
    width: 200px;
    height: 200px;
    margin: 0 auto 16px;
    background: #fff;
    padding: 12px;
    border-radius: 8px;

    img {
      width: 100%;
      height: 100%;
    }
  }

  p {
    color: #606266;
    font-size: 14px;
  }
}

.pay-actions {
  display: flex;
  justify-content: center;
  gap: 16px;

  :deep(.el-button--large) {
    padding: 16px 40px;
  }
}
</style>
