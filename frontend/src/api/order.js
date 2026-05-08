import request from '../utils/request'

/**
 * 订单 API 接口
 */

/**
 * 创建订单
 * @param {Object} orderData - 订单数据
 * @param {number} orderData.userId - 用户 ID
 * @param {number} orderData.addressId - 地址 ID
 * @param {Array<number>} orderData.couponIds - 优惠券 ID 列表
 * @param {string} orderData.remark - 订单备注
 * @param {Array<number>} orderData.cartItemIds - 购物车记录 ID 列表
 * @returns {Promise}
 */
export const createOrder = (orderData) => request.post('/order/create', orderData)

/**
 * 查询订单列表
 * @param {number} userId - 用户 ID
 * @param {number} [status] - 订单状态
 * @returns {Promise}
 */
export const getOrderList = (userId, status) => {
  let url = `/order/list?userId=${userId}`
  if (status !== undefined) {
    url += `&status=${status}`
  }
  return request.get(url)
}

/**
 * 查询订单详情
 * @param {number} orderId - 订单 ID
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export const getOrderDetail = (orderId, userId) => {
  return request.get(`/order/detail?orderId=${orderId}&userId=${userId}`)
}

/**
 * 取消订单
 * @param {number} orderId - 订单 ID
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export const cancelOrder = (orderId, userId) => {
  return request.put(`/order/cancel?orderId=${orderId}&userId=${userId}`)
}

/**
 * 支付订单
 * @param {Object} payData - 支付数据
 * @param {number} payData.orderId - 订单 ID
 * @param {number} payData.userId - 用户 ID
 * @param {string} payData.paymentType - 支付方式
 * @returns {Promise}
 */
export const payOrder = (payData) => request.post('/order/pay', payData)

/**
 * 获取可用优惠券
 * @param {Object} data - 请求数据
 * @param {number} data.userId - 用户 ID
 * @param {Array} data.items - 订单商品项
 * @returns {Promise}
 */
export const getAvailableCoupons = (data) => {
  return request.post('/order/coupons/available', data)
}

/**
 * 确认收货
 * @param {number} orderId - 订单 ID
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export const confirmOrder = (orderId, userId) => {
  return request.put(`/order/confirm?orderId=${orderId}&userId=${userId}`)
}
