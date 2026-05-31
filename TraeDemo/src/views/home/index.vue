<template>
  <div class="home-page page-container">
    <div class="container">
      <el-carousel height="400px" indicator-position="outside" class="banner-carousel">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <router-link :to="item.link || '/course/list'">
            <img :src="item.image" :alt="item.title" class="banner-image" />
          </router-link>
        </el-carousel-item>
      </el-carousel>

      <section class="section">
        <div class="section-title">热门课程</div>
        <div class="course-grid">
          <div
            v-for="course in hotCourses"
            :key="course.id"
            class="course-card"
            @click="goToCourse(course.id)"
          >
            <div class="course-cover">
              <img :src="course.cover" :alt="course.title" />
              <div class="course-price" v-if="course.price > 0">¥{{ course.price }}</div>
              <div class="course-price free" v-else>免费</div>
            </div>
            <div class="course-info">
              <h3 class="course-title">{{ course.title }}</h3>
              <p class="course-desc">{{ course.description }}</p>
              <div class="course-meta">
                <span><el-icon><User /></el-icon> {{ course.studentCount }}人学习</span>
                <span><el-icon><Star /></el-icon> {{ course.rating }}</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="section-header">
          <div class="section-title">秒杀专区</div>
          <router-link to="/marketing/seckill" class="more-link">
            更多 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="seckill-grid">
          <div
            v-for="item in seckillItems"
            :key="item.id"
            class="seckill-card"
            @click="goToSeckill(item.id)"
          >
            <img :src="item.image" :alt="item.title" />
            <div class="seckill-info">
              <h4>{{ item.title }}</h4>
              <div class="seckill-price">
                <span class="current-price">¥{{ item.seckillPrice }}</span>
                <span class="original-price">¥{{ item.originalPrice }}</span>
              </div>
              <el-progress
                :percentage="item.percentage"
                :stroke-width="8"
                :show-text="false"
                color="#F56C6C"
              />
              <span class="stock-text">仅剩{{ item.stock }}件</span>
            </div>
          </div>
        </div>
      </section>

      <section class="section">
        <div class="section-header">
          <div class="section-title">系统通知</div>
          <router-link to="/marketing/notice" class="more-link">
            更多 <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
        <div class="notice-list">
          <div
            v-for="notice in notices"
            :key="notice.id"
            class="notice-item"
            @click="goToNotice(notice.id)"
          >
            <el-icon class="notice-icon"><Bell /></el-icon>
            <span class="notice-title">{{ notice.title }}</span>
            <span class="notice-time">{{ formatTime(notice.createTime) }}</span>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { marketingApi } from '@/api/marketing'
import { courseApi } from '@/api/course'
import { User, Star, ArrowRight, Bell } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

const banners = ref([])
const hotCourses = ref([])
const seckillItems = ref([])
const notices = ref([])

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getMonth() + 1}-${date.getDate()}`
}

const goToCourse = (id) => {
  router.push(`/course/detail/${id}`)
}

const goToSeckill = (id) => {
  router.push(`/marketing/seckill/${id}`)
}

const goToNotice = (id) => {
  router.push(`/notice/detail/${id}`)
}

const loadData = async () => {
  try {
    const [bannerRes, courseRes, seckillRes, noticeRes] = await Promise.all([
      marketingApi.getBanners(),
      courseApi.getCourseList({ page: 1, limit: 8, sort: 'popular' }),
      marketingApi.getSeckillList(),
      marketingApi.getNotices({ page: 1, limit: 5 })
    ])

    banners.value = bannerRes.data || []
    hotCourses.value = courseRes.data?.records || []
    seckillItems.value = seckillRes.data?.records || []
    notices.value = noticeRes.data?.records || []
  } catch (error) {
    console.error('加载首页数据失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.home-page {
  padding-bottom: 40px;
}

.banner-carousel {
  margin: 20px 0;

  .banner-image {
    width: 100%;
    height: 400px;
    object-fit: cover;
    border-radius: 8px;
  }
}

.section {
  margin-top: 40px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .more-link {
    color: #909399;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 4px;

    &:hover {
      color: #409EFF;
    }
  }
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;

  .course-card {
    background: #fff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    }

    .course-cover {
      position: relative;
      height: 160px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .course-price {
        position: absolute;
        bottom: 8px;
        right: 8px;
        background: #F56C6C;
        color: #fff;
        padding: 4px 12px;
        border-radius: 4px;
        font-size: 14px;
        font-weight: 600;

        &.free {
          background: #67C23A;
        }
      }
    }

    .course-info {
      padding: 16px;

      .course-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .course-desc {
        font-size: 13px;
        color: #909399;
        margin-bottom: 12px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .course-meta {
        display: flex;
        justify-content: space-between;
        font-size: 13px;
        color: #606266;

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }
}

.seckill-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;

  .seckill-card {
    background: #fff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    }

    img {
      width: 100%;
      height: 140px;
      object-fit: cover;
    }

    .seckill-info {
      padding: 12px;

      h4 {
        font-size: 14px;
        color: #303133;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .seckill-price {
        margin-bottom: 8px;

        .current-price {
          color: #F56C6C;
          font-size: 18px;
          font-weight: 600;
          margin-right: 8px;
        }

        .original-price {
          color: #909399;
          font-size: 12px;
          text-decoration: line-through;
        }
      }

      .stock-text {
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
        display: block;
      }
    }
  }
}

.notice-list {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  .notice-item {
    display: flex;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid #EBEEF5;
    cursor: pointer;
    transition: background-color 0.3s;

    &:last-child {
      border-bottom: none;
    }

    &:hover {
      background-color: #F5F7FA;
    }

    .notice-icon {
      color: #409EFF;
      margin-right: 12px;
    }

    .notice-title {
      flex: 1;
      color: #303133;
      font-size: 14px;
    }

    .notice-time {
      color: #909399;
      font-size: 13px;
    }
  }
}
</style>
