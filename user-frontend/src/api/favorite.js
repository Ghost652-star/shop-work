import request from '../utils/request'

/**
 * 收藏商品
 * @param {Object} favoriteData - 收藏数据 { userId, productId }
 * @returns {Promise}
 */
export const addFavorite = (favoriteData) => request.post('/favorite', favoriteData)

/**
 * 取消收藏
 * @param {number} userId - 用户 ID
 * @param {number} productId - 商品 ID
 * @returns {Promise}
 */
export const removeFavorite = (userId, productId) =>
  request.delete(`/favorite?userId=${userId}&productId=${productId}`)

/**
 * 查询用户的收藏列表
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export const getFavoriteList = (userId) => request.get(`/favorite/list?userId=${userId}`)

/**
 * 检查是否已收藏
 * @param {number} userId - 用户 ID
 * @param {number} productId - 商品 ID
 * @returns {Promise}
 */
export const isFavorite = (userId, productId) =>
  request.get(`/favorite/check?userId=${userId}&productId=${productId}`)
