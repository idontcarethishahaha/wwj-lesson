<template>
  <div class="main-layout">
    <el-header class="app-header">
      <div class="header-inner">
        <div class="header-left">
          <router-link to="/" class="logo">
            <img class="logo-icon" src="@/assets/my-lesson.ico" alt="logo">
            <span class="logo-text">在线课堂</span>
          </router-link>
          <el-menu
              :default-active="route.path"
              mode="horizontal"
              :ellipsis="false"
              class="nav-menu"
              router
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/courses">全部课程</el-menu-item>
            <el-menu-item index="/seckill">秒杀活动</el-menu-item>
            <el-menu-item index="/notices">通知公告</el-menu-item>
          </el-menu>
        </div>
        <div class="header-right">
          <el-input
              v-model="searchKeyword"
              placeholder="搜索课程"
              prefix-icon="Search"
              size="small"
              class="search-input"
              @keyup.enter="handleSearch"
          />
          <template v-if="userStore.isLoggedIn">
            <el-badge :value="cartStore.items.length" :hidden="cartStore.items.length === 0" class="cart-badge">
              <el-button :icon="ShoppingCart" circle size="small" @click="$router.push('/cart')" />
            </el-badge>
            <el-dropdown trigger="click" @command="handleCommand">
              <span class="el-dropdown-link">
                <!-- ✅ 正确头像 -->
                <el-avatar :size="28" :src="userStore.avatar ? MINIO_AVATAR(userStore.avatar) : ''" />
                <span>{{ userStore.nickname }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                  <el-dropdown-item command="coupons">我的优惠券</el-dropdown-item>
                  <el-dropdown-item command="learning">学习记录</el-dropdown-item>
                  <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="primary" size="small" @click="$router.push('/login')">登录</el-button>
            <el-button size="small" @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </el-header>
    <el-main class="app-main">
      <router-view />
    </el-main>
    <el-footer class="app-footer">
      <div class="footer-inner">
        <p>&copy; 2026 在线课堂. All rights reserved.</p>
      </div>
    </el-footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ShoppingCart } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import { MINIO_AVATAR } from "@/const/index.js"; // ✅ 引入

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const searchKeyword = ref('')

onMounted(() => {
  if (userStore.isLoggedIn) {
    cartStore.fetchCart()
  }
})

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/courses', query: { keyword: searchKeyword.value } })
  }
}

function handleCommand(command: string) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'orders':
      router.push('/orders')
      break
    case 'coupons':
      router.push('/coupons/mine')
      break
    case 'learning':
      router.push('/profile/learning')
      break
    case 'logout':
      userStore.logout()
      router.push('/')
      break
  }
}
</script>

<style scoped lang="scss">
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0;
  height: 60px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #409EFF, #337ecc);
  color: #fff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 18px;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.nav-menu {
  border-bottom: none !important;
  background: transparent;

  .el-menu-item {
    font-size: 14px;
    height: 60px;
    line-height: 60px;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 180px;
}

.cart-badge {
  :deep(.el-badge__content) {
    top: 8px;
    right: 4px;
  }
}

.app-main {
  flex: 1;
  padding: 0;
  background: #f5f7fa;
}

.app-footer {
  background: #fff;
  border-top: 1px solid #e4e7ed;
  padding: 0;
  height: 60px;
}

.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 13px;
}
</style>