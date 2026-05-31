import { defineStore } from 'pinia'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),

  getters: {
    isLoggedIn: state => !!state.token,
    avatar: state => state.userInfo?.avatar || '',
    nickname: state => state.userInfo?.nickname || '用户',
    userId: state => state.userInfo?.id || null
  },

  actions: {
    async login(loginForm) {
      const res = await userApi.login(loginForm)
      this.token = res.data.token
      localStorage.setItem('token', res.data.token)
      return res
    },

    async register(registerForm) {
      const res = await userApi.register(registerForm)
      return res
    },

    async getUserInfo() {
      if (!this.token) return
      try {
        const res = await userApi.getUserInfo()
        this.userInfo = res.data
        return res
      } catch (error) {
        this.logout()
        throw error
      }
    },

    async updateUserInfo(data) {
      const res = await userApi.updateUserInfo(data)
      this.userInfo = { ...this.userInfo, ...data }
      return res
    },

    async updatePassword(passwordData) {
      const res = await userApi.updatePassword(passwordData)
      return res
    },

    async uploadAvatar(file) {
      const res = await userApi.uploadAvatar(file)
      this.userInfo = { ...this.userInfo, avatar: res.data.url }
      return res
    },

    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    }
  }
})
