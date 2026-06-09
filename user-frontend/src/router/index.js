import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Personal from '../views/Personal.vue'
import ProductDetail from '../views/ProductDetail.vue'
import MerchantDetail from '../views/MerchantDetail.vue'
import OrderDetail from '../views/OrderDetail.vue'
import CouponSeckill from '../views/CouponSeckill.vue'
import Payment from '../views/Payment.vue'
import OrderConfirm from '../views/OrderConfirm.vue'
import CustomerService from '../views/CustomerService.vue'
import ShopList from '../views/ShopList.vue'
import SearchResult from '../views/SearchResult.vue'
import AfterSaleApply from '../views/AfterSaleApply.vue'
import AfterSaleDetail from '../views/AfterSaleDetail.vue'

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
    path: '/orders',
    redirect: { path: '/personal', query: { tab: 'orders' } }
  },
  {
    path: '/product/:id',
    name: 'ProductDetail',
    component: ProductDetail
  },
  {
    path: '/merchant/:id',
    name: 'MerchantDetail',
    component: MerchantDetail
  },
  {
    path: '/shops',
    name: 'ShopList',
    component: ShopList
  },
  {
    path: '/search',
    name: 'SearchResult',
    component: SearchResult
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
  },
  {
    path: '/payment/:batchNo',
    name: 'Payment',
    component: Payment
  },
  {
    path: '/order-confirm',
    name: 'OrderConfirm',
    component: OrderConfirm
  },
  {
    path: '/customer-service',
    name: 'CustomerService',
    component: CustomerService
  },
  {
    path: '/after-sale/apply',
    name: 'AfterSaleApply',
    component: AfterSaleApply
  },
  {
    path: '/after-sale/detail',
    name: 'AfterSaleDetail',
    component: AfterSaleDetail
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const whiteList = ['/', '/product', '/merchant', '/coupon-seckill', '/customer-service', '/shops', '/search']

router.beforeEach((to, from, next) => {
  const loginUser = JSON.parse(localStorage.getItem('loginUser'))
  if (loginUser && loginUser.token) {
    next()
  } else if (whiteList.includes(to.path) || to.path.startsWith('/merchant/') || to.path.startsWith('/product/') || to.path === '/shops' || to.path === '/search') {
    next()
  } else {
    next('/')
  }
})

export default router
