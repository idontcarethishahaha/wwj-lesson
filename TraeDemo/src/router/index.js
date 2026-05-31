import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

NProgress.configure({ showSpinner: false })

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'course/category',
        name: 'CourseCategory',
        component: () => import('@/views/course/category.vue'),
        meta: { title: '课程分类' }
      },
      {
        path: 'course/list',
        name: 'CourseList',
        component: () => import('@/views/course/list.vue'),
        meta: { title: '课程列表' }
      },
      {
        path: 'course/detail/:id',
        name: 'CourseDetail',
        component: () => import('@/views/course/detail.vue'),
        meta: { title: '课程详情' }
      },
      {
        path: 'course/play/:id',
        name: 'CoursePlay',
        component: () => import('@/views/course/play.vue'),
        meta: { title: '视频播放', requiresAuth: true }
      },
      {
        path: 'marketing/coupon',
        name: 'Coupon',
        component: () => import('@/views/marketing/coupon.vue'),
        meta: { title: '优惠券' }
      },
      {
        path: 'marketing/seckill',
        name: 'Seckill',
        component: () => import('@/views/marketing/seckill.vue'),
        meta: { title: '秒杀活动' }
      },
      {
        path: 'marketing/seckill/:id',
        name: 'SeckillDetail',
        component: () => import('@/views/marketing/seckillDetail.vue'),
        meta: { title: '秒杀详情' }
      },
      {
        path: 'marketing/notice',
        name: 'Notice',
        component: () => import('@/views/marketing/notice.vue'),
        meta: { title: '通知公告' }
      },
      {
        path: 'notice/detail/:id',
        name: 'NoticeDetail',
        component: () => import('@/views/marketing/noticeDetail.vue'),
        meta: { title: '公告详情' }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/order/cart.vue'),
        meta: { title: '购物车', requiresAuth: true }
      },
      {
        path: 'order/confirm',
        name: 'OrderConfirm',
        component: () => import('@/views/order/confirm.vue'),
        meta: { title: '确认订单', requiresAuth: true }
      },
      {
        path: 'order/pay/:id',
        name: 'OrderPay',
        component: () => import('@/views/order/pay.vue'),
        meta: { title: '订单支付', requiresAuth: true }
      },
      {
        path: 'order/list',
        name: 'OrderList',
        component: () => import('@/views/order/list.vue'),
        meta: { title: '我的订单', requiresAuth: true }
      },
      {
        path: 'order/detail/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/detail.vue'),
        meta: { title: '订单详情', requiresAuth: true }
      },
      {
        path: 'user/profile',
        name: 'UserProfile',
        component: () => import('@/views/user/profile.vue'),
        meta: { title: '个人资料', requiresAuth: true }
      },
      {
        path: 'user/avatar',
        name: 'UserAvatar',
        component: () => import('@/views/user/avatar.vue'),
        meta: { title: '头像设置', requiresAuth: true }
      },
      {
        path: 'user/password',
        name: 'UserPassword',
        component: () => import('@/views/user/password.vue'),
        meta: { title: '修改密码', requiresAuth: true }
      },
      {
        path: 'user/coupon',
        name: 'MyCoupon',
        component: () => import('@/views/user/coupon.vue'),
        meta: { title: '我的优惠券', requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/index.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404 Not Found' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  NProgress.start()

  document.title = to.meta.title ? `${to.meta.title} - 在线课堂平台` : '在线课堂平台'

  const userStore = useUserStore()

  if (to.meta.requiresAuth) {
    if (!userStore.token) {
      ElMessage.warning('请先登录')
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }

  if ((to.name === 'Login' || to.name === 'Register') && userStore.token) {
    next({ name: 'Home' })
    return
  }

  next()
})

router.afterEach(() => {
  NProgress.done()
})

export default router
