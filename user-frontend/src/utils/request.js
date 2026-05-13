import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'


// 创建axios实例对象
const request = axios.create({
  baseURL: '/api',
  timeout: 600000
})

// axios的响应 response 拦截器
request.interceptors.response.use(
  (response) => { // 成功回调
    return response.data
  },
  (error) => { // 失败回调
    if (error.response.status === 401) { // 全等
      // 提示信息
      ElMessage.error('登录出错，请重新登录');
      // 跳转到登录页面
      router.push('/login');
    }
    else {
      ElMessage.error('操作失败,请检查问题');
    }
    return Promise.reject(error)
  }
)

// axios的请求request拦截器
request.interceptors.request.use(
  (config) => { // 成功回调
    const loginUser = JSON.parse(localStorage.getItem('loginUser'));
    if (loginUser && loginUser.token) {
      config.headers.token = loginUser.token;
    }
    return config;
  },
  (error) => { // 失败回调
    return Promise.reject(error)
  }
)

export default request
