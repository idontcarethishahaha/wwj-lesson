import request from './request'
import type { ApiResult, Course, Category, Season, Episode, Comment, Danmaku, PageResult } from '@/types'

export const cmsApi = {
  getCategories(): Promise<ApiResult<Category[]>> {
    return request.get('/course-server/api/v1/category/simpleList')
  },

  getCourseList(params: { fkCategoryId?: number; keyword?: string; sort?: string; pageNum?: number; pageSize?: number }): Promise<ApiResult<PageResult<Course>>> {
    return request.get('/course-server/api/v1/course/page', { params })
  },

  getCourseDetail(id: number): Promise<ApiResult<Course>> {
    return request.get(`/course-server/api/v1/course/select/${id}`)
  },

  getRecommendCourses(): Promise<ApiResult<Course[]>> {
    return request.get('/course-server/api/v1/course/recommend')
  },

  searchCourses(params: { keyword: string; pageNum?: number; pageSize?: number }): Promise<ApiResult<PageResult<Course>>> {
    return request.get('/course-server/api/v1/course/search', { params })
  },

  getSeasons(courseId: number): Promise<ApiResult<Season[]>> {
    return request.get(`/course-server/api/v1/season/list/${courseId}`)
  },

  getEpisodes(seasonId: number): Promise<ApiResult<Episode[]>> {
    return request.get(`/course-server/api/v1/episode/list/${seasonId}`)
  },

  getEpisode(id: number): Promise<ApiResult<Episode>> {
    return request.get(`/course-server/api/v1/episode/select/${id}`)
  },

  getComments(params: { pageNum?: number; pageSize?: number }): Promise<ApiResult<PageResult<Comment>>> {
    return request.get(`/course-server/api/v1/comment/page`, { params })
  },

  postComment(data: { fkEpisodeId: number; fkUserId: number; pid: number; content: string }): Promise<ApiResult<null>> {
    return request.post('/course-server/api/v1/comment/insert', data)
  },

  likeComment(commentId: number): Promise<ApiResult<null>> {
    return request.post(`/course-server/api/v1/comment/like/${commentId}`)
  },

  reportComment(data: { commentId: number; reason: string }): Promise<ApiResult<null>> {
    return request.post('/course-server/api/v1/comment/report', data)
  },

  sendDanmaku(data: { episodeId: number; content: string; time: number }): Promise<ApiResult<null>> {
    return request.post('/course-server/api/v1/danmaku/send', data)
  },

  getDanmakus(episodeId: number): Promise<ApiResult<Danmaku[]>> {
    return request.get(`/course-server/api/v1/danmaku/list/${episodeId}`)
  }
}