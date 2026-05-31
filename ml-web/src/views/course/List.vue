<template>
  <div class="page-container">
    <div class="search-section">
      <el-input
        v-model="keyword"
        placeholder="搜索课程"
        prefix-icon="Search"
        size="large"
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
        @clear="handleSearch"
      />
    </div>

    <div class="filter-section">
      <div class="filter-row">
        <span class="filter-label">分类：</span>
        <el-radio-group v-model="categoryId" @change="handleCategoryChange">
          <el-radio-button :value="undefined">全部</el-radio-button>
          <el-radio-button v-for="cat in categories" :key="cat.id" :value="cat.id">
            {{ cat.title }}
          </el-radio-button>
        </el-radio-group>
      </div>
      <div class="filter-row">
        <span class="filter-label">排序：</span>
        <el-radio-group v-model="sort" @change="handleSortChange">
          <el-radio-button value="">默认</el-radio-button>
          <el-radio-button value="price_asc">价格升序</el-radio-button>
          <el-radio-button value="price_desc">价格降序</el-radio-button>
          <el-radio-button value="popularity">人气排序</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <template v-else>
      <div v-if="courseList.length === 0" class="empty-state">
        <el-empty description="暂无课程" />
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
           <!--  <span class="original-price">&yen;{{ course.originalPrice.toFixed(2) }}</span> -->
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
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { User, Avatar } from '@element-plus/icons-vue'
import { cmsApi } from '@/api'
import { MINIO_COURSE_COVER } from '@/const'
import type { Category, Course } from '@/types'

const route = useRoute()
const router = useRouter()

const keyword = ref((route.query.keyword as string) || '')
const categoryId = ref<number | undefined>(undefined)
const sort = ref('')
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const loading = ref(false)

const categories = ref<Category[]>([])
const courseList = ref<Course[]>([])

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
    const params: {
      fkCategoryId?: number
      keyword?: string
      sort?: string
      pageNum: number
      pageSize: number
    } = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
    }
    if (categoryId.value !== undefined) {
      params.fkCategoryId = categoryId.value
    }
    if (keyword.value.trim()) {
      params.keyword = keyword.value.trim()
    }
    if (sort.value) {
      params.sort = sort.value
    }

    const res = await cmsApi.getCourseList(params)
    courseList.value = res.data.records
    // 输出courseList长度
    console.log('courseList长度:', courseList.value.length)
    total.value = res.data.totalRow
  } catch {
    courseList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  router.replace({ query: { ...route.query, keyword: keyword.value || undefined } })
  fetchCourses()
}

function handleCategoryChange() {
  pageNum.value = 1
  fetchCourses()
}

function handleSortChange() {
  pageNum.value = 1
  fetchCourses()
}

function handlePageChange(val: number) {
  pageNum.value = val
  fetchCourses()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

watch(
  () => route.query,
  (newQuery) => {
    const q = newQuery.keyword as string | undefined
    if (q !== undefined && q !== keyword.value) {
      keyword.value = q || ''
      fetchCourses()
    }
  }
)

onMounted(() => {
  fetchCategories()
  fetchCourses()
})
</script>

<style scoped lang="scss">
.search-section {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;

  .search-input {
    max-width: 600px;
    width: 100%;
  }
}

.filter-section {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 20px;

  .filter-row {
    display: flex;
    align-items: center;
    gap: 12px;

    & + .filter-row {
      margin-top: 12px;
      padding-top: 12px;
      border-top: 1px solid #f0f0f0;
    }
  }

  .filter-label {
    font-size: 14px;
    color: #606266;
    white-space: nowrap;
    flex-shrink: 0;
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