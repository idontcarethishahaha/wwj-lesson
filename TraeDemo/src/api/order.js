import request from '@/utils/axios'

export const orderApi = {
  getCartList() {
    return request({
      url: '/order-server/api/v1/cart/list',
      method: 'get'
    })
  },

  addToCart(data) {
    return request({
      url: '/order-server/api/v1/cart/add',
      method: 'post',
      data
    })
  },

  removeFromCart(id) {
    return request({
      url: `/order-server/api/v1/cart/remove/${id}`,
      method: 'delete'
    })
  },

  clearCart() {
    return request({
      url: '/order-server/api/v1/cart/clear',
      method: 'delete'
    })
  },

  createOrder(data) {
    return request({
      url: '/order-server/api/v1/order/create',
      method: 'post',
      data
    })
  },

  getOrderList(params) {
    return request({
      url: '/order-server/api/v1/order/list',
      method: 'get',
      params
    })
  },

  getOrderDetail(id) {
    return request({
      url: `/order-server/api/v1/order/detail/${id}`,
      method: 'get'
    })
  },

  cancelOrder(id) {
    return request({
      url: `/order-server/api/v1/order/cancel/${id}`,
      method: 'put'
    })
  },

  payOrder(id, data) {
    return request({
      url: `/order-server/api/v1/order/pay/${id}`,
      method: 'post',
      data
    })
  },

  getPayResult(id) {
    return request({
      url: `/order-server/api/v1/order/pay/result/${id}`,
      method: 'get'
    })
  },

  useCoupon(couponId) {
    return request({
      url: '/order-server/api/v1/coupon/use',
      method: 'post',
      params: { couponId }
    })
  }
}
