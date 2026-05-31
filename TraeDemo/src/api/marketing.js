import request from '@/utils/axios'

export const marketingApi = {
  getBanners() {
    return request({
      url: '/sms-server/api/v1/banner/list',
      method: 'get'
    })
  },

  getNotices(params) {
    return request({
      url: '/sms-server/api/v1/notice/list',
      method: 'get',
      params
    })
  },

  getNoticeDetail(id) {
    return request({
      url: `/sms-server/api/v1/notice/detail/${id}`,
      method: 'get'
    })
  },

  getCouponList() {
    return request({
      url: '/sms-server/api/v1/coupon/list',
      method: 'get'
    })
  },

  receiveCoupon(id) {
    return request({
      url: `/sms-server/api/v1/coupon/receive/${id}`,
      method: 'post'
    })
  },

  getMyCoupons() {
    return request({
      url: '/sms-server/api/v1/coupon/my',
      method: 'get'
    })
  },

  getSeckillList() {
    return request({
      url: '/sms-server/api/v1/seckill/list',
      method: 'get'
    })
  },

  getSeckillDetail(id) {
    return request({
      url: `/sms-server/api/v1/seckill/detail/${id}`,
      method: 'get'
    })
  },

  createSeckillOrder(data) {
    return request({
      url: '/sms-server/api/v1/seckill/order',
      method: 'post',
      data
    })
  }
}
