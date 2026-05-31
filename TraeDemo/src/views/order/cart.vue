<template>
  <div class="cart-page page-container">
    <div class="container">
      <div class="page-header">
        <h1>我的购物车</h1>
      </div>

      <div class="cart-content" v-loading="loading">
        <div class="cart-list" v-if="cartStore.cartList.length > 0">
          <div class="cart-header">
            <el-checkbox
              v-model="cartStore.isAllSelected"
              @change="cartStore.toggleSelectAll"
            >
              全选
            </el-checkbox>
            <span class="header-info">课程信息</span>
            <span class="header-price">单价</span>
            <span class="header-action">操作</span>
          </div>

          <div
            v-for="item in cartStore.cartList"
            :key="item.id"
            class="cart-item"
          >
            <div class="item-select">
              <el-checkbox
                :model-value="cartStore.selectedIds.includes(item.id)"
                @change="() => cartStore.toggleSelect(item.id)"
              />
            </div>
            <div class="item-info" @click="goToCourse(item.courseId)">
              <img :src="item.courseCover" :alt="item.courseName" />
              <div class="item-detail">
                <h3>{{ item.courseName }}</h3>
                <p>{{ item.teacherName }}</p>
              </div>
            </div>
            <div class="item-price">
              <span class="price" v-if="item.price > 0">¥{{ item.price }}</span>
              <span class="price free" v-else>免费</span>
            </div>
            <div class="item-action">
              <el-button type="danger" text @click="handleRemove(item.id)">
                删除
              </el-button>
            </div>
          </div>
        </div>

        <el-empty v-else description="购物车是空的" />

        <div class="cart-footer" v-if="cartStore.cartList.length > 0">
          <div class="footer-left">
            <el-button type="danger" text @click="handleClear" :loading="clearing">
              清空购物车
            </el-button>
          </div>
          <div class="footer-right">
            <div class="total-info">
              已选择 <span class="count">{{ cartStore.selectedCount }}</span> 件课程，
              合计：<span class="total-price">¥{{ cartStore.totalPrice }}</span>
            </div>
            <el-button
              type="primary"
              size="large"
              :disabled="cartStore.selectedCount === 0"
              @click="goToConfirm"
            >
              去结算
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(false)
const clearing = ref(false)

const goToCourse = (courseId) => {
  router.push(`/course/detail/${courseId}`)
}

const handleRemove = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该课程吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cartStore.removeFromCart(id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    clearing.value = true
    await cartStore.clearCart()
    ElMessage.success('清空成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  } finally {
    clearing.value = false
  }
}

const goToConfirm = () => {
  if (cartStore.selectedCount === 0) {
    ElMessage.warning('请选择要结算的课程')
    return
  }
  router.push('/order/confirm')
}

onMounted(async () => {
  loading.value = true
  try {
    await cartStore.getCartList()
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.cart-page {
  padding: 40px 0;

  .page-header {
    margin-bottom: 30px;

    h1 {
      font-size: 24px;
      color: #303133;
    }
  }
}

.cart-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 24px;
}

.cart-list {
  .cart-header {
    display: flex;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid #EBEEF5;

    .header-info {
      flex: 1;
      margin-left: 16px;
      color: #909399;
      font-size: 14px;
    }

    .header-price {
      width: 120px;
      text-align: center;
      color: #909399;
      font-size: 14px;
    }

    .header-action {
      width: 80px;
      text-align: center;
      color: #909399;
      font-size: 14px;
    }
  }

  .cart-item {
    display: flex;
    align-items: center;
    padding: 20px 0;
    border-bottom: 1px solid #EBEEF5;

    &:last-child {
      border-bottom: none;
    }

    .item-select {
      margin-right: 16px;
    }

    .item-info {
      flex: 1;
      display: flex;
      align-items: center;
      cursor: pointer;

      img {
        width: 120px;
        height: 80px;
        border-radius: 8px;
        object-fit: cover;
        margin-right: 16px;
      }

      .item-detail {
        h3 {
          font-size: 16px;
          color: #303133;
          margin-bottom: 8px;
        }

        p {
          font-size: 13px;
          color: #909399;
        }
      }
    }

    .item-price {
      width: 120px;
      text-align: center;

      .price {
        font-size: 18px;
        font-weight: 600;
        color: #F56C6C;

        &.free {
          color: #67C23A;
        }
      }
    }

    .item-action {
      width: 80px;
      text-align: center;
    }
  }
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #EBEEF5;

  .footer-right {
    display: flex;
    align-items: center;
    gap: 24px;

    .total-info {
      font-size: 14px;
      color: #606266;

      .count {
        color: #409EFF;
        font-weight: 600;
      }

      .total-price {
        font-size: 24px;
        font-weight: 700;
        color: #F56C6C;
      }
    }
  }
}
</style>
