import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Personal from '../views/Personal.vue'
import ProductDetail from '../views/ProductDetail.vue'
import OrderDetail from '../views/OrderDetail.vue'
import CouponSeckill from '../views/CouponSeckill.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/personal',
    name: 'Personal',
    component: Personal
  },
  {
    path: '/product',
    name: 'ProductDetail',
    component: ProductDetail
  },
  {
    path: '/order/detail',
    name: 'OrderDetail',
    component: OrderDetail
  },
  {
    path: '/coupon-seckill',
    name: 'CouponSeckill',
    component: CouponSeckill
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
