<script setup>
import MyNav from "@/components/MyNav.vue";
import MyForm from "@/components/MyForm.vue";
import {ref, reactive} from "vue";
import {insertApi} from "@/api/index.js";
import {RULE} from "@/const/index.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

// 定义导航项
const navItems = [
  {label: '用户管理', icon: 'Avatar'},
  {label: 'xx列表', icon: 'HomeFilled', url: '/xx'},
  {label: '添加新xx', icon: 'Plus'},
]
// 定义表单项
const formItems = [
  {label: '名称', prop: 'xx', required: true, span: 12},
  {label: 'xx', prop: 'xx', required: true, span: 12, type: 'number'},
  {label: 'xx', prop: 'xx', required: true, span: 24, type: 'textarea'},
  {label: 'xx', prop: 'xx', required: true, span: 24, type: 'textarea'},
]
// 表单值
const formValues = ref({capacity: 0});
// 表单项规则: 对表单输入值进行格式校验提醒
const rules = {
  title: RULE.TITLE, address: RULE.ADDRESS, info: RULE.INFO
}
// 添加成功的响应函数
function insertSuccess() {
  ElMessage.success("添加成功!");
  // 延迟1000ms后自动跳转到房间列表页
  setTimeout(() => router.push('/xx'), 1000);
}
// 定义模块名
const args = {module: 'xx'}
</script>

<template>
  <my-nav v-bind:items="navItems"/>
  <el-card class="insert-card" header="添加新xx">
    <my-form type="insert"
             :params="formValues"
             :api="insertApi"
             :rules="rules"
             :args="args"
             :callback="insertSuccess"
             :items="formItems"></my-form>
  </el-card>
</template>

<style scoped>
  .insert-card {
    width: 60%;
    margin: 65px auto;
  }
</style>