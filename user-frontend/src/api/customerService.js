import request from '../utils/request'

/**
 * 发送客服消息
 * @param {string} message - 用户消息内容
 * @param {string} userId - 用户 ID
 * @param {string} [orderNo] - 订单号（可选）
 * @param {string} [image] - base64 编码的图片（可选）
 * @returns {Promise}
 */
export const processMessage = (message, userId, orderNo, image) => {
  const body = { message, user_id: userId }
  if (orderNo) body.order_no = orderNo
  if (image) body.image = image
  return request.post('/shop/customer-service/process', body)
}

/**
 * 获取聊天历史记录
 * @param {string} userId - 用户 ID
 * @returns {Promise}
 */
export const getChatHistory = (userId) => {
  return request.get(`/shop/customer-service/history/${userId}`)
}

/**
 * 清空聊天历史记录
 * @param {string} userId - 用户 ID
 * @returns {Promise}
 */
export const clearChatHistory = (userId) => {
  return request.delete(`/shop/customer-service/history/${userId}`)
}
