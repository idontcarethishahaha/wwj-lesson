<script setup>
import MyNav from "@/components/MyNav.vue";
import MyTable from "@/components/MyTable.vue";
import MyHead from "@/components/MyHead.vue";
import {onMounted, ref, reactive} from "vue";
import {myPage} from "@/request/index.js";
import {deleteApi, deleteBatchApi, pageApi} from "@/api/index.js";
import {ElMessage} from "element-plus";
import {MINIO_COURSE_COVER,MINIO_COURSE_SUMMARY} from "@/const/index.js";
import router from "@/router/index.js";

// 导航项列表
const navItems = [
  {label: '课程管理', icon: 'Notebook'},
  {label: '课程列表', icon: 'List'},
]
// 数据头
const headItems = [
  {type: 'ipt', span: 5, placeholder: '搜索xxx', callback: pageByTitle}
]
// 表格列信息
const tableColumns = [
  {label: '序号', prop: 'idx', type: 'tag', width: 65},
  {label: '标题', prop: 'title'},
  {label: '作者', prop: 'author', width: 120},
  {label: '类别', prop: 'category.title', width: 120},
  {label: '价格', prop: 'price', width: 120, suffix: '.00 元'},
  {label: '封面图片', prop: 'cover', width: 120, type: 'img', minio: MINIO_COURSE_COVER},
  {label: '摘要图片', prop: 'summary', width: 120, type: 'img', minio: MINIO_COURSE_SUMMARY},
  {label: '描述', prop: 'info', type: 'card', tooltip: false},
]
// 按钮列表
const buttons = [
  {label: '季次列表', type: 'success', callback: courseSeason}
]
// 表格数据 + 分页信息 + xx名称
const records = ref();
const pageInfo = reactive({pageNum: 1, pageSize: 5, total: 0, callback: page});
const title = ref();

// 分装一个分页查询函数
async function page(pageNum = pageInfo['pageNum'], pageSize = pageInfo['pageSize']) {
  let config = {
    api: pageApi,
    args: {module: 'course'},
    params: {pageNum, pageSize},
    records, pageInfo
  };
  if (title.value) {//如果用户查询时填写了xx名称，就额外再增加一个查询参数
    config.params.title = title.value
  }
  // 发送分页查询请求
  await myPage(config);
}
// 搜索xx名
function pageByTitle(val) {
  if (val || title.value) {//仅当输入框有值，或者title不为空时，发送分页查询（按xx名）
    title.value = val;
    page();
  }
}

// 添加 courseSeason函数
function courseSeason(row) {
  // 存储当前课程ID和名称
  sessionStorage.setItem('courseId', row.id);
  sessionStorage.setItem('courseTitle', row.title);
  router.push('/Seasons');// 跳转到 courseSeason
}

// 删除课程成功时，显示消息提醒
function deleteSuccess() {
  ElMessage.success('删除成功');
  page();//刷新网页
}
// 当网页挂载完成，默认执行分页查询
onMounted(() => page());
</script>

<template>
  <my-nav :items="navItems"></my-nav>
  <my-head :items="headItems"></my-head>
  <my-table :page-info="pageInfo"
            :columns="tableColumns"
            :records="records"
            :buttons="buttons"
            :delete-api="deleteApi"
            :delete-batch-api="deleteBatchApi"
            :delete-callback="deleteSuccess"
            insert-page="/CourseInsert"
            update-page="/CourseUpdate"
            module="course"></my-table>
</template>

<style scoped lang="scss">

</style>