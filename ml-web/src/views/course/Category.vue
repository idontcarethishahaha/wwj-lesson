<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ currentCategory ? currentCategory.name : '课程分类' }}</h2>
    </div>

    <div class="category-tabs">
      <el-tag
        v-for="cat in categories"
        :key="cat.id"
        :type="cat.id === currentId ? 'primary' : 'info'"
        :effect="cat.id === currentId ? 'dark' : 'plain'"
        size="large"
        class="category-tag"
        @click="switchCategory(cat.id)"
      >
        {{ cat.name }}
      </el-tag>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <template v-else>
      <div v-if="courseList.length === 0" class="empty-state">
        <el-empty description="该分类暂无课程" />
      </div>

      <div v-else class="card-list">
        <el-card
          v-for="course in courseList"
          :key="course.id"
          class="course-card"
          shadow="hover"
          @click="$router.push(`/courses/${course.id}`)"
        >
          <img :src="MINIO_COURSE_COVER(course.cover)" :alt="course.title" class="course-cover">
          <div class="course-title">{{ course.title }}</div>
          <div class="course-price">
            &yen;{{ course.price.toFixed(2) }}
            <span class="original-price">&yen;{{ course.originalPrice.toFixed(2) }}</span>
          </div>
          <div class="course-meta">
            <span>
              <el-icon><User /></el-icon>
              {{ course.teacherName }}
            </span>
            <span>
              <el-icon><Avatar /></el-icon>
              {{ course.studentCount }} 人学习
            </span>
          </div>
        </el-card>
      </div>

      <div v-if="total > 0" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          background
          @current-change="handlePageChange"
        />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, Avatar } from '@element-plus/icons-vue'
import { cmsApi } from '@/api'
import { MINIO_COURSE_COVER } from '@/const'
import type { Category, Course } from '@/types'

const route = useRoute()
const router = useRouter()

const categories = ref<Category[]>([])
const courseList = ref<Course[]>([])
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const loading = ref(false)

const currentId = computed(() => Number(route.params.id))
const currentCategory = computed(() => categories.value.find(c => c.id === currentId.value))

async function fetchCategories() {
  try {
    const res = await cmsApi.getCategories()
    categories.value = res.data
  } catch {
    categories.value = []
  }
}

async function fetchCourses() {
  loading.value = true
  try {
    const res = await cmsApi.getCourseList({
      categoryId: currentId.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    })
    courseList.value = res.data.records
    total.value = res.data.total
  } catch {
    courseList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function switchCategory(id: number) {
  router.push(`/courses/category/${id}`)
}

function handlePageChange(val: number) {
  pageNum.value = val
  fetchCourses()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

watch(
  () => route.params.id,
  () => {
    pageNum.value = 1
    fetchCourses()
  }
)

onMounted(() => {
  fetchCategories()
  fetchCourses()
})
</script>

<style scoped lang="scss">
.category-tabs {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  .category-tag {
    cursor: pointer;
    font-size: 14px;
    padding: 0 16px;
    height: 36px;
    line-height: 36px;
    border-radius: 18px;
    transition: all 0.2s;

    &:hover {
      transform: translateY(-1px);
    }
  }
}

.loading-container {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
}

.empty-state {
  background: #fff;
  border-radius: 8px;
  padding: 60px 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 20px 0;
}
</style>