import request from '../utils/request'

/**
 * 发送客服消息
 * @param {string} message - 用户消息内容
 * @param {string} userId - 用户 ID
 * @param {string} [orderNo] - 订单号（可选，用于订单咨询）
 * @returns {Promise}
 */
export const processMessage = (message, userId, orderNo) => {
  const body = { message, user_id: userId }
  if (orderNo) body.order_no = orderNo
  return request.post('/shop/customer-service/process', body)
}
