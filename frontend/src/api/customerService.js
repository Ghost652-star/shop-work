import request from '../utils/request'

/**
 * 发送消息到客服AI处理接口
 * @param {string} message - 用户消息
 * @param {string|number} userId - 用户ID
 * @returns {Promise}
 */
export const processMessage = (message, userId) => {
  return request.post('/shop/customer-service/process', {
    message,
    userId: String(userId)
  })
}
