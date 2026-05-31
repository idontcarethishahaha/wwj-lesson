import request from '@/utils/axios'

export const userApi = {
  login(data) {
    return request({
      url: '/user-server/api/v1/user/login',
      method: 'post',
      data
    })
  },

  register(data) {
    return request({
      url: '/user-server/api/v1/user/register',
      method: 'post',
      data
    })
  },

  getUserInfo() {
    return request({
      url: '/user-server/api/v1/user/info',
      method: 'get'
    })
  },

  updateUserInfo(data) {
    return request({
      url: '/user-server/api/v1/user/info',
      method: 'put',
      data
    })
  },

  updatePassword(data) {
    return request({
      url: '/user-server/api/v1/user/password',
      method: 'put',
      data
    })
  },

  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/user-server/api/v1/user/avatar',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  sendCode(phone) {
    return request({
      url: '/user-server/api/v1/user/sendCode',
      method: 'post',
      params: { phone }
    })
  }
}
