import request from '@/utils/request'

export function getMerchantInfo() {
  return request.get('/shop/merchant/info')
}
