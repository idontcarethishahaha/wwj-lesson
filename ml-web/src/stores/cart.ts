import { defineStore } from 'pinia'
import { ref } from 'vue'
import { omsApi } from '@/api'
import type { CartItem } from '@/types'
import { ElMessage } from 'element-plus'
import { useUserStore } from './user'

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const userStore = useUserStore()

  async function fetchCart() {
    try {
      const res = await omsApi.getCartList()
      items.value = res.data
    } catch {
      items.value = []
    }
  }

  async function addToCart(fkCourseId: number) {
    const fkUserId = userStore.userInfo?.id || 0
    await omsApi.addToCart({ fkUserId, fkCourseId })
    ElMessage.success('已加入购物车')
    await fetchCart()
  }

  async function updateQuantity(id: number, quantity: number) {
    if (quantity < 1) return
    await omsApi.updateCartQuantity({ id, quantity })
    await fetchCart()
  }

  async function removeItem(id: number) {
    await omsApi.removeCartItem(id)
    ElMessage.success('已移除')
    await fetchCart()
  }

  const totalAmount = () => items.value.reduce((sum, item) => sum + item.coursePrice, 0)

  return {
    items,
    fetchCart,
    addToCart,
    updateQuantity,
    removeItem,
    totalAmount
  }
})