import axios from 'axios'
import { ElMessage, ElLoading } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

let loadingInstance = null
let requestCount = 0

const showLoading = () => {
  if (requestCount === 0) {
    loadingInstance = ElLoading.service({
      lock: true,
      text: '加载中...',
      background: 'rgba(0, 0, 0, 0.1)'
    })
  }
  requestCount++
}

const hideLoading = () => {
  requestCount--
  if (requestCount <= 0) {
    requestCount = 0
    loadingInstance?.close()
  }
}

service.interceptors.request.use(
  config => {
    showLoading()
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    hideLoading()
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    hideLoading()
    const res = response.data

    if (res.code && res.code !== 200 && res.code !== 0) {
      ElMessage.error(res.message || '请求失败')

      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        router.push('/login')
      }

      return Promise.reject(new Error(res.message || '请求失败'))
    }

    return res
  },
  error => {
    hideLoading()

    if (error.response) {
      switch (error.response.status) {
        case 400:
          ElMessage.error('请求参数错误')
          break
        case 401:
          ElMessage.error('未授权，请重新登录')
          const userStore = useUserStore()
          userStore.logout()
          router.push('/login')
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.message || '请求失败')
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时')
    } else {
      ElMessage.error(error.message || '网络错误')
    }

    return Promise.reject(error)
  }
)

export default service
