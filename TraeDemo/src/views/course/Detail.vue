<template>
  <div class="page-container">
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <template v-else-if="course">
      <div class="course-hero">
        <div class="hero-cover">
          <img :src="MINIO_COURSE_COVER(course.cover)" :alt="course.title">
        </div>
        <div class="hero-info">
          <h1 class="hero-title">{{ course.title }}</h1>
          <p class="hero-desc">{{ course.description }}</p>
          <div class="hero-price">
            <span class="current-price">&yen;{{ course.price.toFixed(2) }}</span>
            <!-- <span class="original-price">&yen;{{ course.originalPrice.toFixed(2) }}</span> -->
          </div>
          <div class="hero-meta">
            <span>
              <el-icon><User /></el-icon>
              讲师：{{ course.author }}
            </span>
            <span>
              <el-icon><Avatar /></el-icon>
              {{ course.studentCount }} 人学习
            </span>
            <span>
              <el-icon><VideoCamera /></el-icon>
              {{ course.episodeCount }} 集
            </span>
          </div>
          <div class="hero-actions">
            <el-button type="danger" size="large" :icon="ShoppingCart" @click="handleAddToCart">
              加入购物车
            </el-button>
          </div>
        </div>
      </div>

      <div class="course-content">
        <div class="content-main">
          <div class="section-card">
            <h3 class="section-title">课程目录</h3>
            <div v-if="seasons.length === 0" class="empty-section">
              <el-empty description="暂无课程目录" :image-size="80" />
            </div>
            <el-collapse v-else v-model="activeSeason" accordion>
              <el-collapse-item
                v-for="season in course.seasons"
                :key="season.id"
                :title="season.title"
                :name="season.id"
              >
                <div
                  v-for="episode in season.episodes"
                  :key="episode.id"
                  class="episode-item"
                  :class="{ free: episode.free }"
                  @click="goToVideo(episode.id)"
                >
                  <div class="episode-info">
                    <span class="episode-title">{{ episode.title }}</span>
                    <el-tag v-if="episode.free" size="small" type="success">免费</el-tag>
                  </div>
                  <div class="episode-duration">
                    <el-icon><VideoCamera /></el-icon>
                    {{ formatDuration(episode.duration) }}
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>

        </div>

        <div class="content-side">
          <div class="section-card">
            <h3 class="section-title">推荐课程</h3>
            <div v-if="recommendCourses.length === 0" class="empty-section">
              <el-empty description="暂无推荐" :image-size="60" />
            </div>
            <div v-else class="recommend-list">
              <div
                v-for="item in recommendCourses"
                :key="item.id"
                class="recommend-item"
                @click="$router.push(`/courses/${item.id}`)"
              >
                <img :src="item.cover" :alt="item.title" class="recommend-cover">
                <div class="recommend-info">
                  <div class="recommend-title">{{ item.title }}</div>
                  <div class="recommend-price">&yen;{{ item.price.toFixed(2) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>

    <div v-else class="empty-state">
      <el-empty description="课程不存在" />
    </div>

    <el-dialog v-model="reportDialogVisible" title="举报评论" width="400px">
      <el-input
        v-model="reportReason"
        type="textarea"
        :rows="3"
        placeholder="请输入举报原因..."
        maxlength="200"
        show-word-limit
      />
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="reportSubmitting" @click="handleSubmitReport">
          提交举报
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Avatar, VideoCamera, ShoppingCart } from '@element-plus/icons-vue'
import { cmsApi, omsApi } from '@/api'
import { MINIO_COURSE_COVER } from '@/const'
import type { Course, Season } from '@/types'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const courseId = Number(route.params.id)
const loading = ref(false)
const course = ref<Course | null>(null)
const seasons = ref<Season[]>([])
const activeSeason = ref<number | undefined>(undefined)

const recommendCourses = ref<Course[]>([])

function formatDuration(seconds: number): string {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  if (h > 0) {
    return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
  }
  return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
}

async function fetchCourseDetail() {
  loading.value = true
  try {
    const res = await cmsApi.getCourseDetail(courseId)
    course.value = res.data
  } catch {
    course.value = null
  } finally {
    loading.value = false
  }
}

async function fetchSeasons() {
  try {
    const res = await cmsApi.getSeasons(courseId)
    seasons.value = res.data
    if (res.data.length > 0) {
      activeSeason.value = res.data[0].id
    }
  } catch {
    seasons.value = []
  }
}

async function fetchRecommendCourses() {
  try {
    const res = await cmsApi.getRecommendCourses()
    recommendCourses.value = res.data
  } catch {
    recommendCourses.value = []
  }
}

async function handleAddToCart() {
  try {
    const fkUserId = userStore.userInfo?.id || 0
    await omsApi.addToCart({ fkUserId, fkCourseId: courseId })
    ElMessage.success('已加入购物车')
  } catch {
    ElMessage.error('加入购物车失败')
  }
}

function goToVideo(episodeId: number) {
  router.push(`/courses/${courseId}/video/${episodeId}`)
}

onMounted(() => {
  fetchCourseDetail()
  fetchSeasons()
  fetchRecommendCourses()
})
</script>

<style scoped lang="scss">
.loading-container {
  background: #fff;
  border-radius: 12px;
  padding: 60px;
}

.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 80px 0;
}

.course-hero {
  display: flex;
  gap: 30px;
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;

  .hero-cover {
    flex-shrink: 0;
    width: 360px;
    height: 200px;
    border-radius: 8px;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .hero-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .hero-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
  }

  .hero-desc {
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
    margin-bottom: 16px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .hero-price {
    margin-bottom: 12px;

    .current-price {
      font-size: 28px;
      font-weight: 700;
      color: #F56C6C;
    }

    .original-price {
      font-size: 16px;
      color: #909399;
      text-decoration: line-through;
      margin-left: 12px;
    }
  }

  .hero-meta {
    display: flex;
    gap: 24px;
    font-size: 14px;
    color: #909399;
    margin-bottom: 16px;

    .el-icon {
      margin-right: 4px;
    }
  }
}

.course-content {
  display: flex;
  gap: 24px;

  .content-main {
    flex: 1;
    min-width: 0;
  }

  .content-side {
    width: 320px;
    flex-shrink: 0;
  }
}

.section-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;

  & + .section-card {
    margin-top: 20px;
  }
}

.empty-section {
  padding: 20px 0;
}

.episode-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #f5f7fa;
  }

  & + .episode-item {
    margin-top: 4px;
  }

  &.free {
    background: #f0f9eb;

    &:hover {
      background: #e6f7d8;
    }
  }

  .episode-info {
    display: flex;
    align-items: center;
    gap: 8px;

    .episode-title {
      font-size: 14px;
      color: #303133;
    }
  }

  .episode-duration {
    font-size: 13px;
    color: #909399;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.comment-form {
  margin-bottom: 24px;

  .comment-form-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 12px;
  }
}

.comment-list {
  .comment-item {
    display: flex;
    gap: 12px;
    padding: 16px 0;

    & + .comment-item {
      border-top: 1px solid #f0f0f0;
    }
  }

  .comment-body {
    flex: 1;
    min-width: 0;
  }

  .comment-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 8px;

    .comment-username {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }

    .comment-time {
      font-size: 12px;
      color: #909399;
    }
  }

  .comment-content {
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
    margin-bottom: 8px;
  }

  .comment-actions {
    display: flex;
    gap: 8px;
  }
}

.load-more {
  text-align: center;
  padding: 16px 0 0;
  border-top: 1px solid #f0f0f0;
  margin-top: 8px;
}

.recommend-list {
  .recommend-item {
    display: flex;
    gap: 12px;
    padding: 10px 0;
    cursor: pointer;
    border-radius: 8px;
    transition: background 0.2s;

    &:hover {
      background: #f5f7fa;
    }

    & + .recommend-item {
      border-top: 1px solid #f0f0f0;
    }
  }

  .recommend-cover {
    width: 80px;
    height: 50px;
    border-radius: 4px;
    object-fit: cover;
    flex-shrink: 0;
  }

  .recommend-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .recommend-title {
    font-size: 13px;
    color: #303133;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .recommend-price {
    font-size: 14px;
    font-weight: 600;
    color: #F56C6C;
  }
}

.el-collapse {
  border-top: none;
}

:deep(.el-collapse-item__header) {
  font-size: 15px;
  font-weight: 500;
  padding-left: 4px;
}

:deep(.el-collapse-item__wrap) {
  border-bottom: none;
}

:deep(.el-collapse-item__content) {
  padding-bottom: 8px;
}
</style>