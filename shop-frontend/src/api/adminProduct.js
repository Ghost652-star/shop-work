import request from '@/utils/request'

export function getProductList(params) {
  return request.get('/shop/product/list', { params })
}

export function updateProductStatus(data) {
  return request.put('/shop/product/status', data)
}

export function updateProductStock(data) {
  return request.put('/shop/product/stock', data)
}
