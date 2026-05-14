import request from '@/utils/request'

export function getProductList(params) {
  return request.get('/shop/product/list', { params })
}

export function getProductDetail(productId) {
  return request.get('/shop/product/detail', { params: { productId } })
}

export function addProduct(data) {
  return request.post('/shop/product/add', data)
}

export function updateProduct(data) {
  return request.put('/shop/product/update', data)
}

export function deleteProduct(productId) {
  return request.delete('/shop/product/delete', { params: { productId } })
}

export function updateProductStatus(data) {
  return request.put('/shop/product/status', data)
}

export function updateProductStock(data) {
  return request.put('/shop/product/stock', data)
}
