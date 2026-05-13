import request from '@/utils/request'

export function getOrderList(params) {
  return request.get('/admin/order/list', { params })
}

export function getOrderDetail(orderId) {
  return request.get('/admin/order/detail', { params: { orderId } })
}

export function markShipped(orderId) {
  return request.put('/admin/order/ship', { orderId })
}
