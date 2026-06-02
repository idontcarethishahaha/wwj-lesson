<template>
  <div class="not-found-page">
    <div class="not-found-container">
      <div class="not-found-code">404</div>
      <h2 class="not-found-title">页面不存在</h2>
      <p class="not-found-desc">抱歉，您访问的页面不存在或已被移除</p>
      <el-button type="primary" size="large" @click="$router.push('/')">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.not-found-page {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.not-found-container {
  text-align: center;
}

.not-found-code {
  font-size: 120px;
  font-weight: 700;
  color: #409EFF;
  line-height: 1;
  margin-bottom: 16px;
  opacity: 0.3;
}

.not-found-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
}

.not-found-desc {
  font-size: 14px;
  color: #909399;
  margin: 0 0 32px;
}
</style>