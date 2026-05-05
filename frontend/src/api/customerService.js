import request from '../utils/request'

/**
 * 发送客服消息
 * @param {string} message - 用户消息内容
 * @param {string} userId - 用户 ID
 * @returns {Promise}
 */
export const processMessage = (message, userId) => request.post('/shop/customer-service/process', { message, user_id: userId })
