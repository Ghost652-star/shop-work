import request from '@/utils/request'

export function getAfterSaleList(params) {
  return request.get('/shop/after-sale/list', { params })
}

export function handleAfterSale(data) {
  return request.put('/shop/after-sale/handle', data)
}
