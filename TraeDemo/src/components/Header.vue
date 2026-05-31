<template>
  <header class="header">
    <div class="container">
      <div class="header-left">
        <router-link to="/" class="logo">
          <img src="@/assets/logo.png" alt="在线课堂" @error="handleImageError" />
          <span>在线课堂</span>
        </router-link>
        <nav class="nav">
          <router-link to="/home" class="nav-item">首页</router-link>
          <router-link to="/course/category" class="nav-item">课程</router-link>
          <router-link to="/marketing/seckill" class="nav-item">秒杀</router-link>
          <router-link to="/marketing/coupon" class="nav-item">优惠券</router-link>
        </nav>
      </div>
      <div class="header-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程"
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
        <template v-if="userStore.isLoggedIn">
          <el-dropdown @command="handleUserCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.avatar">
                {{ userStore.nickname?.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userStore.nickname }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="order">
                  <el-icon><List /></el-icon>我的订单
                </el-dropdown-item>
                <el-dropdown-item command="coupon">
                  <el-icon><Ticket /></el-icon>我的优惠券
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="$router.push('/login')">登录</el-button>
          <el-button @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Search, User, List, Ticket, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const searchKeyword = ref('')

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/course/list', query: { keyword: searchKeyword.value } })
  }
}

const handleUserCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'order':
      router.push('/order/list')
      break
    case 'coupon':
      router.push('/user/coupon')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/home')
      break
  }
}

const handleImageError = (e) => {
  e.target.style.display = 'none'
}
</script>

<style lang="scss" scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  z-index: 1000;

  .container {
    width: 1200px;
    height: 100%;
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

.header-left {
  display: flex;
  align-items: center;

  .logo {
    display: flex;
    align-items: center;
    margin-right: 40px;

    img {
      width: 36px;
      height: 36px;
      margin-right: 8px;
    }

    span {
      font-size: 18px;
      font-weight: 600;
      color: #409EFF;
    }
  }

  .nav {
    display: flex;
    gap: 32px;

    .nav-item {
      font-size: 15px;
      color: #606266;
      transition: color 0.3s;

      &:hover,
      &.router-link-active {
        color: #409EFF;
      }
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;

  .search-input {
    width: 200px;

    :deep(.el-input__wrapper) {
      border-radius: 20px 0 0 20px;
    }

    :deep(.el-input-group__append) {
      border-radius: 0 20px 20px 0;
    }
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 4px;
    transition: background-color 0.3s;

    &:hover {
      background-color: #f5f7fa;
    }

    .username {
      font-size: 14px;
      color: #606266;
    }
  }
}
</style>
