import request from '@/utils/request'

export function getOrderList(params) {
  return request.get('/shop/order/list', { params })
}

export function getOrderDetail(orderId) {
  return request.get('/shop/order/detail', { params: { orderId } })
}

export function markShipped(orderId) {
  return request.put('/shop/order/ship', { orderId })
}
