import request from '@/utils/request'

export function getSalesTrend() {
  return request.get('/shop/dashboard/sales-trend')
}

export function getOrderStatus() {
  return request.get('/shop/dashboard/order-status')
}

export function getTopProducts() {
  return request.get('/shop/dashboard/top-products')
}
