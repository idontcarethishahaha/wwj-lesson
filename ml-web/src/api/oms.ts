import request from './request'
import type { ApiResult, CartItem, Order, PageResult } from '@/types'

export const omsApi = {
  getCartList(): Promise<ApiResult<CartItem[]>> {
    return request.get('/order-server/api/v1/cart/list')
  },

  addToCart(data: { fkUserId: number; fkCourseId: number }): Promise<ApiResult<null>> {
    return request.post('/order-server/api/v1/cart/insert', data)
  },

  updateCartQuantity(data: { id: number; quantity: number }): Promise<ApiResult<null>> {
    return request.put('/order-server/api/v1/cart/update', data)
  },

  removeCartItem(id: number): Promise<ApiResult<null>> {
    return request.delete(`/order-server/api/v1/cart/delete/${id}`)
  },

  createOrder(data: { fkUserId: number; courseIds: number[]; payAmount: number; fkCouponsId?: number }): Promise<ApiResult<Order>> {
    return request.post('/order-server/api/v1/order/prePay', data)
  },

  getOrderList(params: { status?: number; pageNum?: number; pageSize?: number }): Promise<ApiResult<PageResult<Order>>> {
    return request.get('/order-server/api/v1/order/page', { params })
  },

  getOrderDetail(id: number): Promise<ApiResult<Order>> {
    return request.get(`/order-server/api/v1/order/select/${id}`)
  },

  getPayQrCode(data: { sn: string; payAmount: number }): Promise<Blob> {
    return request.post('/order-server/api/v1/order/getQrCode', data, { responseType: 'blob' })
  },

  cancelOrder(id: number): Promise<ApiResult<null>> {
    return request.post(`/order-server/api/v1/order/cancel/${id}`)
  },

  getOrderStatus(id: number): Promise<ApiResult<{ status: number; statusText: string }>> {
    return request.get(`/order-server/api/v1/order/status/${id}`)
  }
}