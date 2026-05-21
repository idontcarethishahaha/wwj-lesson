import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// 引用path模块，方便拼接路径
import path from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src') //给src目录定义一个别名 @
    }
  },
  server: {
    host: "localhost", //127.0.0.1
    port: "24108"
  }
})