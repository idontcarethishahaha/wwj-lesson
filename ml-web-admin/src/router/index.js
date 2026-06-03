import {createRouter, createWebHashHistory} from "vue-router";
import Login from "@/views/Login.vue";
import Main from "@/views/Main.vue";
import Dashboard from "@/views/Dashboard.vue";
import Personal from "@/views/personal/Personal.vue";
import PersonalUpdate from "@/views/personal/PersonalUpdate.vue";
import PersonalUpdatePhone from "@/views/personal/PersonalUpdatePhone.vue";
import User from "@/views/ums/user/User.vue";
import UserInsert from "@/views/ums/user/UsertInsert.vue";
import UserUpdate from "../views/ums/user/UserUpdate.vue";
import UserUpdateRoles from "../views/ums/user/UserUpdateRoles.vue";
import Category from "@/views/cms/category/Category.vue";
import CategoryInsert from "@/views/cms/category/CategoryInsert.vue";
import CategoryUpdate from "@/views/cms/category/CategoryUpdate.vue";
import Course from "@/views/cms/course/Course.vue";
import CourseInsert from "@/views/cms/course/CourseInsert.vue";
import CourseUpdate from "@/views/cms/course/CourseUpdate.vue";
import Season from "@/views/cms/course/season/Season.vue";
import SeasonInsert from "@/views/cms/course/season/SeasonInsert.vue";
import SeasonUpdate from "@/views/cms/course/season/SeasonUpdate.vue";
import Episode from "@/views/cms/course/episode/Episode.vue";
import EpisodeInsert from "@/views/cms/course/episode/EpisodeInsert.vue";
import EpisodeUpdate from "@/views/cms/course/episode/EpisodeUpdate.vue";
import Notice from "@/views/sms/notice/Notice.vue";
import NoticeInsert from "@/views/sms/notice/NoticeInsert.vue";
import NoticeUpdate from "@/views/sms/notice/NoticeUpdate.vue";
import Banner from "../views/sms/banner/Banner.vue";
import BannerInsert from "../views/sms/banner/BannerInsert.vue";
import BannerUpdate from "../views/sms/banner/BannerUpdate.vue";
import Article from "../views/sms/article/Article.vue";
import ArticleInsert from "../views/sms/article/ArticleInsert.vue";
import ArticleUpdate from "../views/sms/article/ArticleUpdate.vue";
import Seckill from "../views/sms/seckill/Seckill.vue";
import SeckillInsert from "../views/sms/seckill/SeckillInsert.vue";
import SeckillUpdate from "../views/sms/seckill/SeckillUpdate.vue";
import SeckillDetail from "../views/sms/seckill/detail/SeckillDetail.vue";
import SeckillDetailInsert from "../views/sms/seckill/detail/SeckillDetailInsert.vue";
import SeckillDetailUpdate from "../views/sms/seckill/detail/SeckillDetailUpdate.vue";
import Coupons from "../views/sms/coupons/Coupons.vue";
import CouponsInsert from "../views/sms/coupons/CouponsInsert.vue";
import CouponsUpdate from "../views/sms/coupons/CouponsUpdate.vue";
import Cart from "../views/oms/cart/Cart.vue";
import CartInsert from "../views/oms/cart/CartInsert.vue";
import CartUpdate from "../views/oms/cart/CartUpdate.vue";
import Order from "../views/oms/order/Order.vue";
import OrderInsert from "../views/oms/order/OrderInsert.vue";
import OrderUpdate from "../views/oms/order/OrderUpdate.vue";
import OrderDetail from "../views/oms/order/detail/OrderDetail.vue";
import OrderDetailInsert from "../views/oms/order/detail/OrderDetailInsert.vue";
import OrderDetailUpdate from "../views/oms/order/detail/OrderDetailUpdate.vue";
import SubComment from "@/views/cms/comment/sub/SubComment.vue";
import Comment from "@/views/cms/comment/Comment.vue";
import Role from "@/views/ums/role/Role.vue";
import RoleInsert from "@/views/ums/role/RoleInsert.vue";
import RoleUpdate from "@/views/ums/role/RoleUpdate.vue";
import RoleUpdateMenus from "@/views/ums/role/RoleUpdateMenus.vue";
import MenuInsert from "@/views/ums/menu/MenuInsert.vue";
import MenuUpdate from "../views/ums/menu/MenuUpdate.vue";
import SubMenu from "../views/ums/menu/sub/SubMenu.vue";
import SubMenuInsert from "../views/ums/menu/sub/SubMenuInsert.vue";
import SubMenuUpdate from "../views/ums/menu/sub/SubMenuUpdate.vue";
import Menu from "@/views/ums/menu/Menu.vue";


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
                {path: '/userInsert',name:'UserInsert',component:UserInsert},
                {path: '/UserUpdate', name: 'UserUpdate', component: UserUpdate},
                {path: '/UserUpdateRoles', name: 'UserUpdateRoles', component: UserUpdateRoles},
                {path: '/category',name: 'Category',component: Category},
                {path: '/categoryInsert',name: CategoryInsert,component: CategoryInsert},
                {path: '/categoryUpdate',name: CategoryUpdate,component: CategoryUpdate},
                {path: '/course',name: Course,component: Course},
                {path: '/courseInsert',name: CourseInsert,component: CourseInsert},
                {path: '/courseUpdate',name: CourseUpdate,component: CourseUpdate},
                {path: '/season',name: Season,component: Season},
                {path: '/seasonInsert',name: SeasonInsert,component: SeasonInsert},
                {path: '/seasonUpdate',name: SeasonUpdate,component: SeasonUpdate},
                {path: '/episode',name: Episode,component: Episode},
                {path: '/episodeInsert',name: EpisodeInsert,component: EpisodeInsert},
                {path: '/episodeUpdate',name: EpisodeUpdate,component: EpisodeUpdate},
                {path: '/Notice', name: 'Notice', component: Notice},
                {path: '/NoticeInsert', name: 'NoticeInsert', component: NoticeInsert},
                {path: '/NoticeUpdate', name: 'NoticeUpdate', component: NoticeUpdate},
                {path: '/Banner', name: 'Banner', component: Banner},
                {path: '/BannerInsert', name: 'BannerInsert', component: BannerInsert},
                {path: '/BannerUpdate', name: 'BannerUpdate', component: BannerUpdate},
                {path: '/Article', name: 'Article', component: Article},
                {path: '/ArticleInsert', name: 'ArticleInsert', component: ArticleInsert},
                {path: '/ArticleUpdate', name: 'ArticleUpdate', component: ArticleUpdate},
                {path: '/Seckill', name: 'Seckill', component: Seckill},
                {path: '/SeckillInsert', name: 'SeckillInsert', component: SeckillInsert},
                {path: '/SeckillUpdate', name: 'SeckillUpdate', component: SeckillUpdate},
                {path: '/SeckillDetail', name: 'SeckillDetail', component: SeckillDetail},
                {path: '/SeckillDetailInsert', name: 'SeckillDetailInsert', component: SeckillDetailInsert},
                {path: '/SeckillDetailUpdate', name: 'SeckillDetailUpdate', component: SeckillDetailUpdate},
                {path: '/Coupons', name: 'Coupons', component: Coupons},
                {path: '/CouponsInsert', name: 'CouponsInsert', component: CouponsInsert},
                {path: '/CouponsUpdate', name: 'CouponsUpdate', component: CouponsUpdate},
                {path: '/Cart', name: 'Cart', component: Cart},
                {path: '/CartInsert', name: 'CartInsert', component: CartInsert},
                {path: '/CartUpdate', name: 'CartUpdate', component: CartUpdate},
                {path: '/Order', name: 'Order', component: Order},
                {path: '/OrderInsert', name: 'OrderInsert', component: OrderInsert},
                {path: '/OrderUpdate', name: 'OrderUpdate', component: OrderUpdate},
                {path: '/OrderDetail', name: 'OrderDetail', component: OrderDetail},
                {path: '/OrderDetailInsert', name: 'OrderDetailInsert', component: OrderDetailInsert},
                {path: '/OrderDetailUpdate', name: 'OrderDetailUpdate', component: OrderDetailUpdate},
                {path: '/Comment', name: 'Comment', component: Comment},
                {path: '/SubComment', name: 'SubComment', component: SubComment},
                {path: '/Role', name: 'Role', component: Role},
                {path: '/RoleInsert', name: 'RoleInsert', component: RoleInsert},
                {path: '/RoleUpdate', name: 'RoleUpdate', component: RoleUpdate},
                {path: '/RoleUpdateMenus', name: 'RoleUpdateMenus', component: RoleUpdateMenus},
                {path: '/Menu', name: 'Menu', component: Menu},
                {path: '/MenuInsert', name: 'MenuInsert', component: MenuInsert},
                {path: '/MenuUpdate', name: 'MenuUpdate', component: MenuUpdate},
                {path: '/SubMenu', name: 'SubMenu', component: SubMenu},
                {path: '/SubMenuInsert', name: 'SubMenuInsert', component: SubMenuInsert},
                {path: '/SubMenuUpdate', name: 'SubMenuUpdate', component: SubMenuUpdate},
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