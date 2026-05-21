import {createRouter, createWebHashHistory} from "vue-router";
import Login from "@/views/Login.vue";
import Main from "@/views/Main.vue";
import Dashboard from "@/views/Dashboard.vue";
import Personal from "@/views/personal/Personal.vue";
import PersonalUpdate from "@/views/personal/PersonalUpdate.vue";
import PersonalUpdatePhone from "@/views/personal/PersonalUpdatePhone.vue";
import User from "@/views/ums/user/User.vue";
import Category from "@/views/cms/category/Category.vue";
import CategoryInsert from "@/views/cms/category/CategoryInsert.vue";
import CategoryUpdate from "@/views/cms/category/CategoryUpdate.vue";
import Course from "@/views/cms/course/Course.vue";
import CourseInsert from "@/views/cms/course/CourseInsert.vue";
import CourseUpdate from "@/views/cms/course/CourseUpdate.vue";

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {path: '/',name:'login',component: Login},
        {path: '/main',name: 'Main',component: Main,
            children:[
                {path: '/dashboard',name: 'Dashboard',component: Dashboard},
                {path: '/personal',name: 'Personal',component: Personal},
                {path: '/personalUpdate',name: 'PersonalUpdate',component: PersonalUpdate},
                {path: '/personalUpdatePhone',name: 'PersonalUpdatePhone',component: PersonalUpdatePhone},
                {path: '/user',name: 'User',component: User},
                {path: '/category',name: 'Category',component: Category},
                {path: '/categoryInsert',name: CategoryInsert,component: CategoryInsert},
                {path: '/categoryUpdate',name: CategoryUpdate,component: CategoryUpdate},
                {path: '/course',name: Course,component: Course},
                {path: '/courseInsert',name: CourseInsert,component: CourseInsert},
                {path: '/courseUpdate',name: CourseUpdate,component: CourseUpdate},
            ]
        }
    ]
});

/*
 * 路由前置守卫：每次转发路由前执行的函数
 * param to: 来源地址
 * param from: 目标地址
 * next: 放行函数
 */
router.beforeEach((to, from, next) => {
    // 如果访问的登录页或者携带了Token就放行
    if (to.path === '/' || sessionStorage.getItem('token')) {
        next();
    } else {
        // 没有权限则自动跳转至登录页
        next('/');
    }
});

export default router