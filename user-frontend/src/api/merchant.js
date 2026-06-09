import request from '../utils/request'

/**
 * 查询商家详情
 * @param {number} id - 商家 ID
 * @returns {Promise}
 */
export const getMerchantDetail = (id) => request.get(`/merchant/${id}`)

/**
 * 查询商家商品列表
 * @param {number} id - 商家 ID
 * @param {object} params - 查询参数 { sort, pageNum, pageSize }
 * @returns {Promise}
 */
export const getMerchantProducts = (id, params) => request.get(`/merchant/${id}/products`, { params })

/**
 * 查询推荐商家列表
 * @param {number} limit - 数量
 * @returns {Promise}
 */
export const getRecommendMerchants = (limit = 3) => request.get('/merchant/recommend', { params: { limit } })
