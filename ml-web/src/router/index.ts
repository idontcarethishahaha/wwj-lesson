import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'courses',
        name: 'CourseList',
        component: () => import('@/views/course/List.vue'),
        meta: { title: '课程列表' }
      },
      {
        path: 'courses/category/:id',
        name: 'CourseCategory',
        component: () => import('@/views/course/Category.vue'),
        meta: { title: '课程分类' }
      },
      {
        path: 'courses/:id',
        name: 'CourseDetail',
        component: () => import('@/views/course/Detail.vue'),
        meta: { title: '课程详情' }
      },
      {
        path: 'courses/:courseId/video/:episodeId',
        name: 'VideoPlayer',
        component: () => import('@/views/course/VideoPlayer.vue'),
        meta: { title: '视频播放', requiresAuth: true }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/order/Cart.vue'),
        meta: { title: '购物车', requiresAuth: true }
      },
      {
        path: 'orders/confirm',
        name: 'OrderConfirm',
        component: () => import('@/views/order/Confirm.vue'),
        meta: { title: '确认订单', requiresAuth: true }
      },
      {
        path: 'orders/pay/:id',
        name: 'OrderPay',
        component: () => import('@/views/order/Pay.vue'),
        meta: { title: '支付', requiresAuth: true }
      },
      {
        path: 'orders',
        name: 'OrderList',
        component: () => import('@/views/order/List.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'orders/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/Detail.vue'),
        meta: { title: '订单详情', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Index.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'profile/security',
        name: 'ProfileSecurity',
        component: () => import('@/views/profile/Security.vue'),
        meta: { title: '安全设置', requiresAuth: true }
      },
      {
        path: 'profile/learning',
        name: 'ProfileLearning',
        component: () => import('@/views/profile/Learning.vue'),
        meta: { title: '学习记录', requiresAuth: true }
      },
      {
        path: 'coupons',
        name: 'CouponList',
        component: () => import('@/views/marketing/Coupon.vue'),
        meta: { title: '优惠券领取', requiresAuth: true }
      },
      {
        path: 'coupons/mine',
        name: 'MyCoupons',
        component: () => import('@/views/marketing/MyCoupons.vue'),
        meta: { title: '我的优惠券', requiresAuth: true }
      },
      {
        path: 'exchange',
        name: 'Exchange',
        component: () => import('@/views/marketing/Exchange.vue'),
        meta: { title: '口令兑换', requiresAuth: true }
      },
      {
        path: 'seckill',
        name: 'SeckillList',
        component: () => import('@/views/marketing/SeckillList.vue'),
        meta: { title: '秒杀活动' }
      },
      {
        path: 'seckill/:id',
        name: 'SeckillDetail',
        component: () => import('@/views/marketing/SeckillDetail.vue'),
        meta: { title: '秒杀详情', requiresAuth: true }
      },
      {
        path: 'notices',
        name: 'NoticeList',
        component: () => import('@/views/marketing/Notices.vue'),
        meta: { title: '系统通知' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title} | 在线课堂`
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router