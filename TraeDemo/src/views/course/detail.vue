<template>
  <div class="course-detail-page page-container" v-loading="loading">
    <div class="container">
      <div class="course-header">
        <div class="course-cover">
          <img :src="courseDetail.cover" :alt="courseDetail.title" />
          <div class="play-btn" @click="goToPlay">
            <el-icon :size="48"><VideoPlay /></el-icon>
          </div>
        </div>
        <div class="course-info">
          <h1 class="course-title">{{ courseDetail.title }}</h1>
          <p class="course-subtitle">{{ courseDetail.description }}</p>
          <div class="course-meta">
            <span class="meta-item">
              <el-icon><User /></el-icon>
              {{ courseDetail.teacherName }}
            </span>
            <span class="meta-item">
              <el-icon><Avatar /></el-icon>
              {{ courseDetail.studentCount }} 学员
            </span>
            <span class="meta-item">
              <el-icon><Star /></el-icon>
              {{ courseDetail.rating }} 分
            </span>
          </div>
          <div class="course-price">
            <span class="price-label">课程价格</span>
            <span class="price-value" v-if="courseDetail.price > 0">¥{{ courseDetail.price }}</span>
            <span class="price-value free" v-else>免费</span>
          </div>
          <div class="course-actions">
            <el-button type="primary" size="large" @click="handleAddToCart" :loading="adding">
              加入购物车
            </el-button>
            <el-button type="danger" size="large" @click="handleBuyNow">
              立即购买
            </el-button>
          </div>
        </div>
      </div>

      <div class="course-content">
        <div class="content-main">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="课程介绍" name="intro">
              <div class="intro-content" v-html="courseDetail.introduction"></div>
            </el-tab-pane>
            <el-tab-pane label="课程目录" name="catalog">
              <div class="catalog-list">
                <div
                  v-for="chapter in chapters"
                  :key="chapter.id"
                  class="chapter-item"
                >
                  <div class="chapter-header" @click="toggleChapter(chapter.id)">
                    <div class="chapter-info">
                      <el-icon><Folder /></el-icon>
                      <span class="chapter-title">{{ chapter.title }}</span>
                      <span class="chapter-duration">{{ chapter.duration }}</span>
                    </div>
                    <el-icon class="arrow">
                      <ArrowRight v-if="!expandedChapters.includes(chapter.id)" />
                      <ArrowDown v-else />
                    </el-icon>
                  </div>
                  <div class="chapter-items" v-show="expandedChapters.includes(chapter.id)">
                    <div
                      v-for="item in chapter.items"
                      :key="item.id"
                      class="item-row"
                      @click="goToPlayItem(item.id)"
                    >
                      <el-icon><VideoCamera /></el-icon>
                      <span class="item-title">{{ item.title }}</span>
                      <span class="item-duration">{{ item.duration }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="用户评价" name="review">
              <div class="review-list">
                <div v-if="reviews.length === 0" class="empty-state">
                  <p>暂无评价</p>
                </div>
                <div v-for="review in reviews" :key="review.id" class="review-item">
                  <div class="review-header">
                    <el-avatar :size="40" :src="review.userAvatar">
                      {{ review.userName?.charAt(0) }}
                    </el-avatar>
                    <div class="review-info">
                      <span class="user-name">{{ review.userName }}</span>
                      <el-rate v-model="review.rating" disabled size="small" />
                    </div>
                    <span class="review-time">{{ formatTime(review.createTime) }}</span>
                  </div>
                  <p class="review-content">{{ review.content }}</p>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
        <div class="content-side">
          <div class="side-card">
            <h3>课程讲师</h3>
            <div class="teacher-info">
              <el-avatar :size="80" :src="courseDetail.teacherAvatar">
                {{ courseDetail.teacherName?.charAt(0) }}
              </el-avatar>
              <div class="teacher-detail">
                <p class="teacher-name">{{ courseDetail.teacherName }}</p>
                <p class="teacher-title">{{ courseDetail.teacherTitle }}</p>
              </div>
            </div>
            <p class="teacher-bio">{{ courseDetail.teacherBio }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { courseApi } from '@/api/course'
import { useCartStore } from '@/stores/cart'
import { ElMessage, ElMessageBox } from 'element-plus'
import { VideoPlay, User, Avatar, Star, Folder, VideoCamera, ArrowRight, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const loading = ref(false)
const adding = ref(false)
const courseDetail = ref({})
const chapters = ref([])
const reviews = ref([])
const activeTab = ref('intro')
const expandedChapters = ref([])

const toggleChapter = (id) => {
  const index = expandedChapters.value.indexOf(id)
  if (index > -1) {
    expandedChapters.value.splice(index, 1)
  } else {
    expandedChapters.value.push(id)
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const loadCourseDetail = async () => {
  loading.value = true
  try {
    const courseId = route.params.id
    const [detailRes, chapterRes] = await Promise.all([
      courseApi.getCourseDetail(courseId),
      courseApi.getCourseChapters(courseId)
    ])

    courseDetail.value = detailRes.data || {}
    chapters.value = chapterRes.data || []

    if (chapters.value.length > 0) {
      expandedChapters.value = [chapters.value[0].id]
    }
  } catch (error) {
    ElMessage.error('加载课程详情失败')
  } finally {
    loading.value = false
  }
}

const handleAddToCart = async () => {
  try {
    adding.value = true
    await cartStore.addToCart(courseDetail.value.id)
    ElMessage.success('已加入购物车')
  } catch (error) {
    ElMessage.error(error.message || '加入购物车失败')
  } finally {
    adding.value = false
  }
}

const handleBuyNow = async () => {
  try {
    await handleAddToCart()
    router.push('/cart')
  } catch (error) {
    console.error(error)
  }
}

const goToPlay = () => {
  if (chapters.value.length > 0 && chapters.value[0].items?.length > 0) {
    router.push(`/course/play/${chapters.value[0].items[0].id}`)
  }
}

const goToPlayItem = (itemId) => {
  router.push(`/course/play/${itemId}`)
}

onMounted(() => {
  loadCourseDetail()
})
</script>

<style lang="scss" scoped>
.course-detail-page {
  padding: 40px 0;
}

.course-header {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;

  .course-cover {
    width: 480px;
    height: 270px;
    border-radius: 12px;
    overflow: hidden;
    position: relative;
    flex-shrink: 0;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .play-btn {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 80px;
      height: 80px;
      background: rgba(64, 158, 255, 0.9);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        transform: translate(-50%, -50%) scale(1.1);
        background: rgba(64, 158, 255, 1);
      }
    }
  }

  .course-info {
    flex: 1;

    .course-title {
      font-size: 28px;
      color: #303133;
      margin-bottom: 12px;
    }

    .course-subtitle {
      font-size: 14px;
      color: #909399;
      margin-bottom: 20px;
    }

    .course-meta {
      display: flex;
      gap: 24px;
      margin-bottom: 24px;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 6px;
        color: #606266;
        font-size: 14px;
      }
    }

    .course-price {
      margin-bottom: 24px;

      .price-label {
        color: #909399;
        font-size: 14px;
        margin-right: 12px;
      }

      .price-value {
        font-size: 32px;
        font-weight: 600;
        color: #F56C6C;

        &.free {
          color: #67C23A;
        }
      }
    }

    .course-actions {
      display: flex;
      gap: 16px;
    }
  }
}

.course-content {
  display: flex;
  gap: 30px;

  .content-main {
    flex: 1;
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }

  .content-side {
    width: 300px;
    flex-shrink: 0;
  }
}

.intro-content {
  line-height: 1.8;
  color: #606266;
  font-size: 14px;

  :deep(img) {
    max-width: 100%;
  }
}

.catalog-list {
  .chapter-item {
    margin-bottom: 12px;
    border: 1px solid #EBEEF5;
    border-radius: 8px;
    overflow: hidden;

    .chapter-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16px;
      background: #F5F7FA;
      cursor: pointer;
      transition: background-color 0.3s;

      &:hover {
        background: #EBEEF5;
      }

      .chapter-info {
        display: flex;
        align-items: center;
        gap: 10px;

        .chapter-title {
          font-weight: 600;
          color: #303133;
        }

        .chapter-duration {
          color: #909399;
          font-size: 13px;
        }
      }

      .arrow {
        color: #909399;
      }
    }

    .chapter-items {
      .item-row {
        display: flex;
        align-items: center;
        gap: 10px;
        padding: 12px 16px;
        border-top: 1px solid #EBEEF5;
        cursor: pointer;
        transition: background-color 0.3s;

        &:hover {
          background: #F5F7FA;
        }

        .item-title {
          flex: 1;
          color: #606266;
          font-size: 14px;
        }

        .item-duration {
          color: #909399;
          font-size: 13px;
        }
      }
    }
  }
}

.review-list {
  .review-item {
    padding: 20px 0;
    border-bottom: 1px solid #EBEEF5;

    &:last-child {
      border-bottom: none;
    }

    .review-header {
      display: flex;
      align-items: center;
      margin-bottom: 12px;

      .review-info {
        flex: 1;
        margin-left: 12px;

        .user-name {
          display: block;
          color: #303133;
          font-weight: 600;
          margin-bottom: 4px;
        }
      }

      .review-time {
        color: #909399;
        font-size: 13px;
      }
    }

    .review-content {
      color: #606266;
      font-size: 14px;
      line-height: 1.6;
      padding-left: 52px;
    }
  }
}

.side-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  h3 {
    font-size: 18px;
    color: #303133;
    margin-bottom: 20px;
  }

  .teacher-info {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 16px;

    .teacher-detail {
      .teacher-name {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 4px;
      }

      .teacher-title {
        font-size: 13px;
        color: #909399;
      }
    }
  }

  .teacher-bio {
    font-size: 13px;
    color: #606266;
    line-height: 1.6;
  }
}
</style>
