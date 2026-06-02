<template>
  <div class="exchange-page">
    <div class="page-container">
      <el-card class="exchange-card">
        <template #header>
          <span class="title">口令兑换</span>
        </template>

        <div class="exchange-content">
          <el-alert
            title="输入兑换口令即可领取专属优惠券"
            type="info"
            :closable="false"
            show-icon
            class="exchange-tip"
          />

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            class="exchange-form"
            @submit.prevent="handleExchange"
          >
            <el-form-item prop="code">
              <el-input
                v-model="form.code"
                placeholder="请输入兑换口令"
                size="large"
                clearable
                maxlength="50"
              >
                <template #prefix>
                  <el-icon><Key /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="exchanging"
                class="submit-btn"
                @click="handleExchange"
              >
                立即兑换
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Key } from '@element-plus/icons-vue'
import { smsApi } from '@/api'
import type { FormInstance } from 'element-plus'

const formRef = ref<FormInstance>()
const exchanging = ref(false)

const form = reactive({
  code: ''
})

const rules = {
  code: [
    { required: true, message: '请输入兑换口令', trigger: 'blur' },
    { min: 2, message: '口令至少2个字符', trigger: 'blur' }
  ]
}

async function handleExchange() {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  exchanging.value = true
  try {
    await smsApi.exchangeCoupon({ code: form.code.trim() })
    ElMessage.success('兑换成功')
    form.code = ''
  } catch {
    ElMessage.error('兑换失败，请检查口令是否正确')
  } finally {
    exchanging.value = false
  }
}
</script>

<style scoped lang="scss">
.exchange-page {
  padding: 24px 0;
}

.page-container {
  max-width: 500px;
  margin: 0 auto;
}

.exchange-card {
  .title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
  }
}

.exchange-content {
  padding: 8px 0;
}

.exchange-tip {
  margin-bottom: 24px;
}

.exchange-form {
  .el-input {
    :deep(.el-input__prefix) {
      font-size: 16px;
    }
  }

  .submit-btn {
    width: 100%;
  }
}
</style>