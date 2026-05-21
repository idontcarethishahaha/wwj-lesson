import { createApp } from 'vue'
import './style.scss'
import App from './App.vue'
// 在主配置文件中引用路由
import router from "./router";
// 引用 vuex 实例
import store from "./vuex"
// 引用ElementPlus相关
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/display.css';
import zhCn from 'element-plus/es/locale/lang/zh-cn';
// ElementPlus图标库: 导入全部Icons图标
import * as ElementPlusIcons from '@element-plus/icons-vue';
// ElementPlus暗黑模式: 核心CSS
import 'element-plus/theme-chalk/dark/css-vars.css';
// import vuex from "./vuex"; //引入暗黑主题样式

const app = createApp(App);
app.use(router);
app.use(store);
app.use(ElementPlus, {locale: zhCn});
app.mount("#app");

//全局注册ElementPlus图标库
for (const [key, component] of Object.entries(ElementPlusIcons)) {
    app.component(key, component)
}
