import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/variables.css'

// 清理 localStorage 中的无效数据（之前登录 bug 可能写入了 "undefined"）
const userId = localStorage.getItem('userId')
if (!userId || userId === 'undefined' || userId === 'NaN' || isNaN(Number(userId))) {
  localStorage.removeItem('userId')
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userNickname')
  localStorage.removeItem('loginUser')
}

const app = createApp(App)

app.use(router)
app.use(ElementPlus)

app.mount('#app')
