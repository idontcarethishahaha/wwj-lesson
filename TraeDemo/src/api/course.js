import request from '@/utils/axios'

export const courseApi = {
  getCategories() {
    return request({
      url: '/course-server/api/v1/category/list',
      method: 'get'
    })
  },

  getCourseList(params) {
    return request({
      url: '/course-server/api/v1/course/list',
      method: 'get',
      params
    })
  },

  getCourseDetail(id) {
    return request({
      url: `/course-server/api/v1/course/detail/${id}`,
      method: 'get'
    })
  },

  getCourseChapters(courseId) {
    return request({
      url: `/course-server/api/v1/chapter/list/${courseId}`,
      method: 'get'
    })
  },

  getChapterItems(chapterId) {
    return request({
      url: `/course-server/api/v1/item/list/${chapterId}`,
      method: 'get'
    })
  },

  getVideoPlayInfo(itemId) {
    return request({
      url: `/course-server/api/v1/video/play/${itemId}`,
      method: 'get'
    })
  },

  getComments(itemId, params) {
    return request({
      url: `/course-server/api/v1/comment/list/${itemId}`,
      method: 'get',
      params
    })
  },

  addComment(data) {
    return request({
      url: '/course-server/api/v1/comment/add',
      method: 'post',
      data
    })
  },

  reportComment(data) {
    return request({
      url: '/course-server/api/v1/comment/report',
      method: 'post',
      data
    })
  },

  getSeasonList(courseId) {
    return request({
      url: `/course-server/api/v1/season/list/${courseId}`,
      method: 'get'
    })
  },

  getEpisodeList(seasonId) {
    return request({
      url: `/course-server/api/v1/episode/list/${seasonId}`,
      method: 'get'
    })
  }
}
