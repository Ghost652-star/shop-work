import request from '../utils/request'

/**
 * 查询分类列表
 * @returns {Promise}
 */
export const getCategoryList = () => request.get('/category/list')

/**
 * 查询商品列表
 * @returns {Promise}
 */
export const getProductList = () => request.get('/product/list')

/**
 * 查询商品详情
 * @param {number} id - 商品 ID
 * @returns {Promise}
 */
export const getProductDetail = (id) => request.get(`/product/${id}`)

/**
 * 查询热销榜单
 * @returns {Promise}
 */
export const getHotSales = () => request.get('/product/hot-sales')