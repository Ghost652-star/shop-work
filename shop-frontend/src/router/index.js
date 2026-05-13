import { createRouter, createWebHistory } from 'vue-router'
import ShopLayout from '../components/ShopLayout.vue'

const routes = [
  {
    path: '/',
    component: ShopLayout,
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: 'product',
        name: 'ProductManage',
        component: () => import('../views/ProductManage.vue')
      },
      {
        path: 'order',
        name: 'OrderManage',
        component: () => import('../views/OrderManage.vue')
      },
      {
        path: 'after-sale',
        name: 'AfterSaleManage',
        component: () => import('../views/AfterSaleManage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
