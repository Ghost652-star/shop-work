import request from '../utils/request'

/**
 * 查询优惠券列表
 * @returns {Promise}
 */
export const getCouponList = () => request.get('/coupon/list')

/**
 * 领取优惠券
 * @param {Object} data - 领取数据
 * @param {number} data.userId - 用户ID
 * @param {number} data.couponId - 优惠券ID
 * @returns {Promise}
 */
export const receiveCoupon = (data) => request.post('/userCoupon/receive', data)

/**
 * 查询用户优惠券列表
 * @param {number} userId - 用户ID
 * @returns {Promise}
 */
export const getUserCouponList = (userId) => request.get(`/userCoupon/list?userId=${userId}`)