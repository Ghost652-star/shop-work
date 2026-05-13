import request from '@/utils/request'

export function getSalesTrend() {
  return request.get('/admin/dashboard/sales-trend')
}

export function getOrderStatus() {
  return request.get('/admin/dashboard/order-status')
}

export function getTopProducts() {
  return request.get('/admin/dashboard/top-products')
}
