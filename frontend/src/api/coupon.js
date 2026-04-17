import request from '../utils/request'

/**
 * 查询优惠券列表
 * @returns {Promise}
 */
export const getCouponList = () => request.get('/coupon/list')