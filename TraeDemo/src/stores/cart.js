import { defineStore } from 'pinia'
import { orderApi } from '@/api/order'

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartList: [],
    selectedIds: [],
    loading: false
  }),

  getters: {
    totalPrice: state => {
      return state.cartList
        .filter(item => state.selectedIds.includes(item.id))
        .reduce((sum, item) => sum + (item.price || 0), 0)
    },
    totalCount: state => state.cartList.length,
    selectedCount: state => state.selectedIds.length,
    isAllSelected: state => {
      return state.cartList.length > 0 && 
             state.selectedIds.length === state.cartList.length
    }
  },

  actions: {
    async getCartList() {
      this.loading = true
      try {
        const res = await orderApi.getCartList()
        this.cartList = res.data || []
        this.selectedIds = this.cartList.map(item => item.id)
        return res
      } finally {
        this.loading = false
      }
    },

    async addToCart(courseId) {
      const res = await orderApi.addToCart({ courseId })
      await this.getCartList()
      return res
    },

    async removeFromCart(id) {
      const res = await orderApi.removeFromCart(id)
      this.cartList = this.cartList.filter(item => item.id !== id)
      this.selectedIds = this.selectedIds.filter(itemId => itemId !== id)
      return res
    },

    async clearCart() {
      const res = await orderApi.clearCart()
      this.cartList = []
      this.selectedIds = []
      return res
    },

    toggleSelect(id) {
      const index = this.selectedIds.indexOf(id)
      if (index > -1) {
        this.selectedIds.splice(index, 1)
      } else {
        this.selectedIds.push(id)
      }
    },

    toggleSelectAll() {
      if (this.isAllSelected) {
        this.selectedIds = []
      } else {
        this.selectedIds = this.cartList.map(item => item.id)
      }
    },

    getSelectedItems() {
      return this.cartList.filter(item => this.selectedIds.includes(item.id))
    }
  }
})
