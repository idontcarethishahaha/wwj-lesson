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
  {label: '课程管理', icon: 'Notebook'},
  {label: '类别列表', icon: 'Management', url: '/Category'},
  {label: '添加新类别', icon: 'Plus'},
]
// 定义表单项
const formItems = [
  {label: '名称', prop: 'title', required: true, span: 12},
  {label: '序号', prop: 'idx', required: true, span: 12, type: 'number'},
  {label: '描述', prop: 'info', required: true, span: 24, type: 'textarea'},
]
// 表单值
const formValues = ref({});
// 表单项规则: 对表单输入值进行格式校验提醒
const rules = {
  title: RULE.TITLE, info: RULE.INFO
}
// 添加成功的响应函数
function insertSuccess() {
  ElMessage.success("添加成功!");
  // 延迟1000ms后自动跳转到列类别表页
  setTimeout(() => router.push('/Category'), 1000);
}
// 定义模块名
const args = {module: 'category'}
</script>

<template>
  <my-nav v-bind:items="navItems"/>
  <el-card class="insert-card" header="添加新类别">
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