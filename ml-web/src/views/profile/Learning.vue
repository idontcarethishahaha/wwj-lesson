<template>
  <div class="learning-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <el-icon :size="20"><Reading /></el-icon>
          <span>学习记录</span>
        </div>
      </template>

      <div v-loading="loading" class="learning-content">
        <el-empty v-if="!loading && records.length === 0" description="暂无学习记录" />

        <div v-else class="record-list">
          <div v-for="record in records" :key="record.id" class="record-item" @click="handleGoCourse(record)">
            <div class="record-cover">
              <el-image :src="record.courseCover" fit="cover">
                <template #error>
                  <div class="image-placeholder">
                    <el-icon :size="24"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
            </div>
            <div class="record-info">
              <h3 class="record-title">{{ record.courseTitle }}</h3>
              <p class="record-progress">
                <span>学习进度</span>
                <el-progress :percentage="record.progress || 0" :stroke-width="8" />
              </p>
              <p class="record-meta">
                <span class="meta-item">
                  <el-icon><VideoCamera /></el-icon>
                  上次学到: {{ record.lastEpisode || '未开始' }}
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ record.updateTime || record.createTime }}
                </span>
              </p>
            </div>
            <div class="record-action">
              <el-button type="primary" text>继续学习</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Reading, Picture, VideoCamera, Clock } from '@element-plus/icons-vue'
import { rmsApi } from '@/api'

const router = useRouter()
const loading = ref(false)
const records = ref<any[]>([])

async function fetchRecords() {
  loading.value = true
  try {
    const res = await rmsApi.getLearningRecord()
    records.value = res.data || []
  } catch {
    ElMessage.error('获取学习记录失败')
  } finally {
    loading.value = false
  }
}

function handleGoCourse(record: any) {
  if (record.courseId) {
    router.push(`/courses/${record.courseId}`)
  }
}

onMounted(() => {
  fetchRecords()
})
</script>

<style scoped lang="scss">
.learning-page {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.learning-content {
  min-height: 200px;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.record-item {
  display: flex;
  gap: 20px;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
  }
}

.record-cover {
  width: 180px;
  height: 120px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;

  .el-image {
    width: 100%;
    height: 100%;
  }

  .image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    color: #c0c4cc;
  }
}

.record-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.record-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.record-progress {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #909399;

  .el-progress {
    flex: 1;
  }
}

.record-meta {
  margin: 0;
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #909399;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;

  .el-icon {
    font-size: 14px;
  }
}

.record-action {
  display: flex;
  align-items: center;
  flex-shrink: 0;
}
</style>