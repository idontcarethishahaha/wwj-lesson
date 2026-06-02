<template>
  <div class="cart-page">
    <div class="page-container">
      <el-card class="cart-card" v-if="cartStore.items.length > 0">
        <template #header>
          <div class="cart-header">
            <span class="title">购物车</span>
            <span class="count">{{ cartStore.items.length }} 门课程</span>
          </div>
        </template>
        <div class="cart-items">
          <div class="cart-item" v-for="item in cartStore.items" :key="item.id">
            <div class="item-cover">
              <el-image :src="MINIO_COURSE_COVER(item.courseCover)" fit="cover" />
            </div>
            <div class="item-info">
              <h3 class="item-title">{{ item.courseTitle }}</h3>
              <div class="item-meta">
                <span class="item-price">&yen;{{ item.coursePrice }}</span>
              </div>
            </div>
            <div class="item-action">
              <el-button type="danger" link @click="handleRemove(item.id)">删除</el-button>
            </div>
          </div>
        </div>
        <div class="cart-footer">
          <div class="footer-left">
            <el-button @click="cartStore.fetchCart()">刷新</el-button>
          </div>
          <div class="footer-right">
            <div class="total-info">
              <span class="total-label">合计：</span>
              <span class="total-price">&yen;{{ totalAmount.toFixed(2) }}</span>
            </div>
            <el-button type="primary" size="large" @click="handleCheckout">结算</el-button>
          </div>
        </div>
      </el-card>
      <el-card v-else class="empty-card">
        <el-empty description="购物车是空的">
          <el-button type="primary" @click="$router.push('/courses')">去选课</el-button>
        </el-empty>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCartStore } from '@/stores/cart'
import { MINIO_COURSE_COVER } from '@/const'

const router = useRouter()
const cartStore = useCartStore()

const totalAmount = computed(() => cartStore.items.reduce((sum, item) => sum + item.coursePrice, 0))

onMounted(() => {
  cartStore.fetchCart()
})

async function handleRemove(id: number) {
  try {
    await ElMessageBox.confirm('确定要移除此课程吗？', '提示', { type: 'warning' })
    await cartStore.removeItem(id)
  } catch {
    if (ElMessageBox) return
  }
}

function handleCheckout() {
  if (cartStore.items.length === 0) {
    ElMessage.warning('购物车为空')
    return
  }
  router.push('/orders/confirm')
}
</script>

<style scoped lang="scss">
.cart-page {
  padding: 24px 0;
}

.page-container {
  max-width: 900px;
  margin: 0 auto;
}

.cart-card {
  .cart-header {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .title {
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }

    .count {
      font-size: 14px;
      color: #909399;
    }
  }
}

.cart-items {
  .cart-item {
    display: flex;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid #f0f0f0;
    gap: 20px;

    &:last-child {
      border-bottom: none;
    }
  }

  .item-cover {
    width: 120px;
    height: 68px;
    flex-shrink: 0;
    border-radius: 4px;
    overflow: hidden;

    .el-image {
      width: 100%;
      height: 100%;
    }
  }

  .item-info {
    flex: 1;
    min-width: 0;

    .item-title {
      font-size: 15px;
      font-weight: 500;
      color: #303133;
      margin: 0 0 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .item-meta {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .item-price {
      font-size: 14px;
      color: #f56c6c;
      font-weight: 500;
    }
  }

  .item-quantity {
    width: 120px;
    text-align: center;
  }

  .item-subtotal {
    width: 100px;
    text-align: right;

    .subtotal-label {
      font-size: 12px;
      color: #909399;
      margin-right: 4px;
    }

    .subtotal-price {
      font-size: 15px;
      color: #f56c6c;
      font-weight: 600;
    }
  }

  .item-action {
    width: 60px;
    text-align: center;
  }
}

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;

  .footer-right {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .total-info {
    display: flex;
    align-items: baseline;

    .total-label {
      font-size: 14px;
      color: #606266;
    }

    .total-price {
      font-size: 24px;
      font-weight: 700;
      color: #f56c6c;
    }
  }
}

.empty-card {
  :deep(.el-card__body) {
    display: flex;
    justify-content: center;
    padding: 60px 0;
  }
}
</style>