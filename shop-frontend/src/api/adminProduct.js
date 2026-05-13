import request from '@/utils/request'

export function getProductList(params) {
  return request.get('/admin/product/list', { params })
}

export function updateProductStatus(data) {
  return request.put('/admin/product/status', data)
}

export function updateProductStock(data) {
  return request.put('/admin/product/stock', data)
}
