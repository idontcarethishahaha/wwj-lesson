<template>
  <div class="category-page page-container">
    <div class="container">
      <div class="page-header">
        <h1>课程分类</h1>
        <p>选择您感兴趣的课程类别</p>
      </div>

      <div class="category-grid">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-card"
          @click="goToList(category.id)"
        >
          <div class="category-icon" :style="{ background: category.color }">
            <el-icon :size="32"><component :is="category.icon" /></el-icon>
          </div>
          <div class="category-info">
            <h3>{{ category.name }}</h3>
            <p>{{ category.description }}</p>
            <span class="course-count">{{ category.courseCount }} 门课程</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { courseApi } from '@/api/course'
import { Study, Code, Chart, Book, Brush, Music } from '@element-plus/icons-vue'

const router = useRouter()

const categories = ref([
  { id: 1, name: '编程开发', description: 'Python、Java、C++等编程语言课程', color: '#409EFF', icon: Code, courseCount: 0 },
  { id: 2, name: '人工智能', description: '机器学习、深度学习、数据科学', color: '#67C23A', icon: Study, courseCount: 0 },
  { id: 3, name: '数据分析', description: 'Excel、SQL、数据可视化', color: '#E6A23C', icon: Chart, courseCount: 0 },
  { id: 4, name: '设计创意', description: 'UI设计、平面设计、影视后期', color: '#F56C6C', icon: Brush, courseCount: 0 },
  { id: 5, name: '语言学习', description: '英语、日语、韩语等外语课程', color: '#909399', icon: Book, courseCount: 0 },
  { id: 6, name: '音乐艺术', description: '乐器演奏、音乐制作、艺术鉴赏', color: '#00BCD4', icon: Music, courseCount: 0 }
])

const goToList = (categoryId) => {
  router.push({ path: '/course/list', query: { categoryId } })
}

const loadCategories = async () => {
  try {
    const res = await courseApi.getCategories()
    if (res.data && res.data.length > 0) {
      categories.value = res.data.map((cat, index) => ({
        ...cat,
        icon: [Study, Code, Chart, Book, Brush, Music][index % 6],
        color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#00BCD4'][index % 6]
      }))
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style lang="scss" scoped>
.category-page {
  padding: 40px 0;

  .page-header {
    text-align: center;
    margin-bottom: 40px;

    h1 {
      font-size: 28px;
      color: #303133;
      margin-bottom: 8px;
    }

    p {
      color: #909399;
      font-size: 14px;
    }
  }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;

  .category-card {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    display: flex;
    align-items: center;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
    }

    .category-icon {
      width: 64px;
      height: 64px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      margin-right: 20px;
      flex-shrink: 0;
    }

    .category-info {
      flex: 1;

      h3 {
        font-size: 18px;
        color: #303133;
        margin-bottom: 6px;
      }

      p {
        font-size: 13px;
        color: #909399;
        margin-bottom: 8px;
      }

      .course-count {
        font-size: 12px;
        color: #409EFF;
      }
    }
  }
}
</style>
