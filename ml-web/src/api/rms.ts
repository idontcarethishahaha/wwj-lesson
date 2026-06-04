import request from './request'
import type { ApiResult, UserInfo } from '@/types'

export const rmsApi = {
  login(data: { username: string; password: string }): Promise<ApiResult<{ token: string; userInfo: UserInfo }>> {
    return request.post('/user-server/api/v1/user/loginByAccount', data)
  },

  loginByMobile(data: { phone: string; code: string }): Promise<ApiResult<{ token: string; userInfo: UserInfo }>> {
    return request.post('/user-server/api/v1/user/login/mobile', data)
  },

  register(data: { username: string; password: string; phone: string; code: string }): Promise<ApiResult<null>> {
    return request.post('/user-server/api/v1/user/register', data)
  },

  logout(): Promise<ApiResult<null>> {
    return request.post('/user-server/api/v1/user/logout')
  },

  getUserInfo(uid: number): Promise<ApiResult<UserInfo>> {
    return request.get(`/user-server/api/v1/user/select/${uid}`)
  },

  updateProfile(data: { userId?: number; nickname?: string; signature?: string }): Promise<ApiResult<null>> {
    return request.put('/user-server/api/v1/user/info', data)
  },

  // uploadAvatar(data: FormData): Promise<ApiResult<{ url: string }>> {
  //   return request.post('/user-server/api/v1/user/avatar', data, {
  //     headers: { 'Content-Type': 'multipart/form-data' }
  //   })
  // },

  uploadAvatar(id: number | string, data: FormData): Promise<ApiResult<{ url: string }>> {
    return request.put(`/user-server/api/v1/user/updateAvatar/${id}`, data, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  changePassword(data: { id: number; oldPassword: string; newPassword: string }): Promise<ApiResult<null>> {
    return request.put('/user-server/api/v1/user/updatePassword', data)
  },

  bindMobile(data: { phone: string; code: string }): Promise<ApiResult<null>> {
    return request.put('/user-server/api/v1/user/mobile', data)
  },

  getLearningRecord(): Promise<ApiResult<any[]>> {
    return request.get('/user-server/api/v1/user/learning-record')
  },

  getRole(userId: number): Promise<ApiResult<{ role: string }>> {
    return request.get(`/user-server/api/v1/user/role?userId=${userId}`)
  },

  sendSmsCode(phone: string): Promise<ApiResult<null>> {
    return request.post('/user-server/api/v1/sms/code', { phone })
  }
}