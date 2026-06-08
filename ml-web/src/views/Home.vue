<template>
  <div class="home">
    <div class="banner-section">
      <el-carousel height="380px" indicator-position="dots" arrow="hover">
        <el-carousel-item v-for="banner in banners" :key="banner.id">
          <a :href="banner.url" target="_blank" class="banner-link">
            <img :src="MINIO_BANNER(banner.url)" :alt="banner.title" class="banner-image" />
            <div class="banner-overlay">
              <h3>{{ banner.title }}</h3>
            </div>
          </a>
        </el-carousel-item>
      </el-carousel>
    </div>

    <div class="home-content">
      <div class="content-main">
        <div class="section">
          <h3 class="section-title">推荐课程</h3>
          <div class="card-list" v-if="recommendCourses.length > 0">
            <el-card
              v-for="course in recommendCourses"
              :key="course.id"
              class="course-card"
              shadow="hover"
              @click="$router.push(`/courses/${course.id}`)"
            >
              <img :src="MINIO_COURSE_COVER(course.cover)" :alt="course.title" class="course-cover" />
              <div class="course-info">
                <h4 class="course-title">{{ course.title }}</h4>
                <div class="course-price">
                  ¥{{ course.price }}
                  <span class="original-price">¥{{ course.originalPrice }}</span>
                </div>
                <div class="course-meta">
                  <span>{{ course.teacherName }}</span>
                  <span>{{ course.studentCount }} 人学习</span>
                </div>
              </div>
            </el-card>
          </div>
          <el-empty v-else description="暂无推荐课程" />
        </div>

        <div class="section">
          <h3 class="section-title">秒杀活动</h3>
          <div class="card-list" v-if="seckillList.length > 0">
            <el-card
              v-for="(item, index) in seckillList"
              :key="item.id"
              class="course-card seckill-card"
              shadow="hover"
              @click="$router.push(`/seckill/${item.id}`)"
            >
              <div class="seckill-cover" :class="`cover-${index % 4}`">
                <div class="cover-content">
                  <span class="seckill-badge">秒杀</span>
                  <span class="cover-text">{{ item.title }}</span>
                </div>
              </div>
              <div class="course-info">
                <h4 class="course-title">{{ item.title }}</h4>
                <div class="course-meta">
                  <span class="seckill-stock">剩余 {{ (item.seckillDetails?.[0]?.stock || item.stock || 0) }} 件</span>
                </div>
              </div>
            </el-card>
          </div>
          <el-empty v-else description="暂无秒杀活动" />
        </div>
      </div>

      <div class="content-side">
        <div class="section">
          <h3 class="section-title">通知公告</h3>
          <div class="notice-list" v-if="notices.length > 0">
            <div
              v-for="notice in notices"
              :key="notice.id"
              class="notice-item"
            >
              <div class="notice-title">{{ notice.content }}</div>
              <div class="notice-time">{{ notice.createTime }}</div>
            </div>
          </div>
          <el-empty v-else description="暂无公告" :image-size="60" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { smsApi, cmsApi } from '@/api'
import { MINIO_COURSE_COVER, MINIO_BANNER } from '@/const'
import type { Banner, Course, SeckillActivity, Notice } from '@/types'

const banners = ref<Banner[]>([])
const recommendCourses = ref<Course[]>([])
const seckillList = ref<SeckillActivity[]>([])
const notices = ref<Notice[]>([])

async function fetchBanners() {
  try {
    const res = await smsApi.getBanners()
    banners.value = res.data
  } catch {
    banners.value = []
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

async function fetchSeckillList() {
  try {
    const res = await smsApi.getSeckillList()
    seckillList.value = res.data
  } catch {
    seckillList.value = []
  }
}

async function fetchNotices() {
  try {
    const res = await smsApi.getNotices({ pageNum: 1, pageSize: 5 })
    notices.value = res.data.records
  } catch {
    notices.value = []
  }
}

onMounted(() => {
  fetchBanners()
  fetchRecommendCourses()
  fetchSeckillList()
  fetchNotices()
})
</script>

<style scoped lang="scss">
.home {
  min-height: 100%;
}

.banner-section {
  margin-bottom: 24px;

  .banner-link {
    display: block;
    width: 100%;
    height: 100%;
    position: relative;
  }

  .banner-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .banner-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 20px;
    background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
    color: #fff;

    h3 {
      font-size: 22px;
      font-weight: 600;
    }
  }
}

.home-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 40px;
  display: flex;
  gap: 24px;
}

.content-main {
  flex: 1;
  min-width: 0;
}

.content-side {
  width: 320px;
  flex-shrink: 0;
}

.section {
  margin-bottom: 32px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-left: 12px;
  border-left: 3px solid #409EFF;
}

.card-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.course-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  :deep(.el-card__body) {
    padding: 0;
  }
}

.course-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
  display: block;
}

.course-info {
  padding: 12px 16px 16px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #303133;
}

.course-price {
  color: #F56C6C;
  font-size: 18px;
  font-weight: 600;

  .original-price {
    color: #909399;
    font-size: 13px;
    text-decoration: line-through;
    margin-left: 8px;
    font-weight: 400;
  }
}

.seckill-price {
  color: #E6A23C;
}

.course-meta {
  font-size: 13px;
  color: #909399;
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
}

.seckill-stock {
  color: #F56C6C;
}

.seckill-cover {
  width: 100%;
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  color: #fff;
  
  &.cover-0 {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }
  
  &.cover-1 {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }
  
  &.cover-2 {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }
  
  &.cover-3 {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  }
}

.cover-content {
  text-align: center;
  padding: 20px;
}

.seckill-badge {
  display: inline-block;
  background: rgba(255, 255, 255, 0.3);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  margin-bottom: 10px;
}

.cover-text {
  display: block;
  font-size: 15px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notice-item {
  padding: 12px 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #409EFF;
    box-shadow: 0 2px 8px rgba(64, 158, 255, 0.12);
  }
}

.notice-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-time {
  font-size: 12px;
  color: #909399;
}
</style>