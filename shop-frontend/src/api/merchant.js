import request from '@/utils/request'

export function getMerchantInfo() {
  return request.get('/admin/merchant/info')
}
