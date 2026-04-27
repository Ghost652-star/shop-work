import request from '../utils/request'

/**
 * 添加商品到购物车
 * @param {Object} data - 添加数据
 * @param {number} data.userId - 用户 ID
 * @param {number} data.productId - 商品 ID
 * @param {number} data.quantity - 数量
 * @returns {Promise}
 */
export const addToCart = (data) => request.post('/cart/add', data)

/**
 * 查询购物车列表
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export const getCartList = (userId) => request.get(`/cart/list?userId=${userId}`)

/**
 * 更新购物车商品数量
 * @param {Object} data - 更新数据
 * @param {number} data.id - 购物车记录 ID
 * @param {number} data.quantity - 数量
 * @returns {Promise}
 */
export const updateQuantity = (data) => request.put('/cart/update', data)

/**
 * 删除购物车商品
 * @param {number} id - 购物车记录 ID
 * @returns {Promise}
 */
export const deleteCart = (id) => request.delete(`/cart/delete?id=${id}`)

/**
 * 批量删除购物车商品
 * @param {Array} ids - 购物车记录 ID 列表
 * @returns {Promise}
 */
export const batchDelete = (ids) => request.delete('/cart/batch-delete', { data: { ids } })

/**
 * 全选/取消全选
 * @param {Object} data - 全选数据
 * @param {number} data.userId - 用户 ID
 * @param {number} data.isChecked - 是否选中：0-未选中，1-选中
 * @returns {Promise}
 */
export const checkAll = (data) => request.put('/cart/check-all', data)

/**
 * 获取购物车商品总数
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export const getCartCount = (userId) => request.get(`/cart/count?userId=${userId}`)

/**
 * 更新购物车商品选中状态
 * @param {Object} data - 更新数据
 * @param {number} data.id - 购物车记录 ID
 * @param {number} data.isChecked - 是否选中：0-未选中，1-选中
 * @returns {Promise}
 */
export const updateChecked = (data) => request.put('/cart/check', data)
