import request from '../utils/request'

/**
 * 发表评论
 * @param {Object} commentData - 评论数据 {userId, productId, orderId, rating, content, images}
 * @returns {Promise}
 */
export const addComment = (commentData) => request.post('/comment', commentData)

/**
 * 删除评论
 * @param {number} id - 评论 ID
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export const deleteComment = (id, userId) => request.delete(`/comment?id=${id}&userId=${userId}`)

/**
 * 获取商品评论列表
 * @param {number} productId - 商品 ID
 * @returns {Promise}
 */
export const getCommentList = (productId) => request.get(`/comment/list?productId=${productId}`)

/**
 * 获取用户评论列表
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export const getUserComments = (userId) => request.get(`/comment/user?userId=${userId}`)

/**
 * 获取商品评论统计
 * @param {number} productId - 商品 ID
 * @returns {Promise}
 */
export const getCommentStats = (productId) => request.get(`/comment/stats?productId=${productId}`)
