import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { rmsApi } from '@/api'
import type { UserInfo } from '@/types'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const avatar = computed(() => userInfo.value?.avatar || '')
  const nickname = computed(() => userInfo.value?.nickname || '')

  function setToken(val: string) {
    token.value = val
    localStorage.setItem('token', val)
  }

  function setUserInfo(info: UserInfo) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  async function fetchUserInfo() {
    try {
      const cached = localStorage.getItem('userInfo')
      let uid = 0
      if (cached) {
        try {
          const info = JSON.parse(cached)
          uid = info.id || 0
        } catch {}
      }
      const res = await rmsApi.getUserInfo(uid)
      setUserInfo(res.data)
      return res.data
    } catch {
      return null
    }
  }

  function loadLocalUser() {
    const cached = localStorage.getItem('userInfo')
    if (cached) {
      try {
        userInfo.value = JSON.parse(cached)
      } catch {
        localStorage.removeItem('userInfo')
      }
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    avatar,
    nickname,
    setToken,
    setUserInfo,
    fetchUserInfo,
    loadLocalUser,
    logout
  }
})