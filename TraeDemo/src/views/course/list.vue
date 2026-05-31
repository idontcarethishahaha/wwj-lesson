<template>
  <div class="course-list-page page-container">
    <div class="container">
      <div class="filter-bar">
        <div class="filter-left">
          <el-select v-model="filterForm.categoryId" placeholder="全部分类" clearable @change="handleFilter">
            <el-option label="全部" :value="null" />
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
          <el-select v-model="filterForm.sort" placeholder="排序方式" @change="handleFilter">
            <el-option label="综合排序" value="default" />
            <el-option label="最新发布" value="latest" />
            <el-option label="最受欢迎" value="popular" />
            <el-option label="价格从低到高" value="price_asc" />
            <el-option label="价格从高到低" value="price_desc" />
          </el-select>
        </div>
        <div class="filter-right">
          <el-input
            v-model="filterForm.keyword"
            placeholder="搜索课程"
            :prefix-icon="Search"
            clearable
            @keyup.enter="handleFilter"
          />
        </div>
      </div>

      <div class="course-count" v-if="total > 0">
        共找到 <span class="count">{{ total }}</span> 门课程
      </div>

      <div class="course-grid" v-loading="loading">
        <div
          v-for="course in courseList"
          :key="course.id"
          class="course-card"
          @click="goToDetail(course.id)"
        >
          <div class="course-cover">
            <img :src="course.cover" :alt="course.title" />
            <div class="course-tag" v-if="course.isHot">热门</div>
            <div class="course-tag new" v-if="course.isNew">新课</div>
            <div class="course-price" v-if="course.price > 0">¥{{ course.price }}</div>
            <div class="course-price free" v-else>免费</div>
          </div>
          <div class="course-info">
            <h3 class="course-title">{{ course.title }}</h3>
            <p class="course-desc">{{ course.description }}</p>
            <div class="course-meta">
              <span class="teacher">
                <el-avatar :size="20" :src="course.teacherAvatar">
                  {{ course.teacherName?.charAt(0) }}
                </el-avatar>
                {{ course.teacherName }}
              </span>
              <span class="students">
                <el-icon><User /></el-icon>
                {{ course.studentCount }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && courseList.length === 0" description="暂无相关课程" />

      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="filterForm.page"
          v-model:page-size="filterForm.limit"
          :total="total"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { courseApi } from '@/api/course'
import { Search, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const courseList = ref([])
const total = ref(0)
const categories = ref([])

const filterForm = reactive({
  categoryId: null,
  keyword: '',
  sort: 'default',
  page: 1,
  limit: 12
})

const loadCategories = async () => {
  try {
    const res = await courseApi.getCategories()
    categories.value = res.data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadCourses = async () => {
  loading.value = true
  try {
    const res = await courseApi.getCourseList(filterForm)
    courseList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    ElMessage.error('加载课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  filterForm.page = 1
  loadCourses()
}

const handleSizeChange = (val) => {
  filterForm.limit = val
  loadCourses()
}

const handlePageChange = (val) => {
  filterForm.page = val
  loadCourses()
}

const goToDetail = (id) => {
  router.push(`/course/detail/${id}`)
}

watch(
  () => route.query,
  (query) => {
    if (query.categoryId) {
      filterForm.categoryId = Number(query.categoryId)
    }
    if (query.keyword) {
      filterForm.keyword = query.keyword
    }
    loadCourses()
  },
  { immediate: true }
)

onMounted(() => {
  loadCategories()
  if (!route.query.categoryId && !route.query.keyword) {
    loadCourses()
  }
})
</script>

<style lang="scss" scoped>
.course-list-page {
  padding: 40px 0;

  .filter-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

    .filter-left {
      display: flex;
      gap: 16px;
    }

    .filter-right {
      width: 280px;

      :deep(.el-input__wrapper) {
        border-radius: 20px;
      }
    }
  }

  .course-count {
    margin-bottom: 20px;
    color: #909399;
    font-size: 14px;

    .count {
      color: #409EFF;
      font-weight: 600;
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

      .course-tag {
        position: absolute;
        top: 8px;
        left: 8px;
        background: #F56C6C;
        color: #fff;
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 12px;

        &.new {
          background: #67C23A;
        }
      }

      .course-price {
        position: absolute;
        bottom: 8px;
        right: 8px;
        background: rgba(0, 0, 0, 0.7);
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
        height: 40px;
        overflow: hidden;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .course-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .teacher {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 13px;
          color: #606266;
        }

        .students {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 13px;
          color: #909399;
        }
      }
    }
  }
}

.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
</style>
