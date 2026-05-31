<template>
  <div class="not-found-page">
    <div class="content">
      <h1 class="code">404</h1>
      <p class="message">抱歉，您访问的页面不存在</p>
      <el-button type="primary" size="large" @click="goHome">
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/home')
}
</script>

<style lang="scss" scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .content {
    text-align: center;
    color: #fff;

    .code {
      font-size: 120px;
      font-weight: 700;
      margin-bottom: 16px;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
    }

    .message {
      font-size: 24px;
      margin-bottom: 32px;
    }
  }
}
</style>
