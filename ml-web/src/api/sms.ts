import request from './request'
import type { ApiResult, Banner, Notice, Coupon, SeckillActivity, PageResult, Order } from '@/types'

export const smsApi = {
  getBanners(): Promise<ApiResult<Banner[]>> {
    return request.get('/sale-server/api/v1/banner/list')
  },

  getNotices(params: { pageNum?: number; pageSize?: number }): Promise<ApiResult<PageResult<Notice>>> {
    return request.get('/sale-server/api/v1/notice/page', { params })
  },

  getNoticeDetail(id: number): Promise<ApiResult<Notice>> {
    return request.get(`/sale-server/api/v1/notice/select/${id}`)
  },

  getCouponList(): Promise<ApiResult<Coupon[]>> {
    return request.get('/sale-server/api/v1/coupons/simpleList')
  },

  receiveCoupon(id: number): Promise<ApiResult<null>> {
    return request.post(`/sale-server/api/v1/coupons/receive/${id}`)
  },

  getMyCoupons(): Promise<ApiResult<Coupon[]>> {
    return request.get('/sale-server/api/v1/coupons/mine')
  },

  exchangeCoupon(data: { code: string }): Promise<ApiResult<null>> {
    return request.post('/sale-server/api/v1/coupons/exchange', data)
  },

  getSeckillList(): Promise<ApiResult<SeckillActivity[]>> {
    return request.get('/sale-server/api/v1/seckill/today')
  },

  getSeckillDetail(id: number): Promise<ApiResult<SeckillActivity>> {
    return request.get(`/sale-server/api/v1/seckill/select/${id}`)
  },

  seckillOrder(data: { activityId: number; courseId: number }): Promise<ApiResult<Order>> {
    return request.post('/sale-server/api/v1/seckill/order', data)
  }
}