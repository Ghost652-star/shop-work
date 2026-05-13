import request from '@/utils/request'

export function getAfterSaleList(params) {
  return request.get('/admin/after-sale/list', { params })
}

export function handleAfterSale(data) {
  return request.put('/admin/after-sale/handle', data)
}
