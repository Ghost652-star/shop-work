import request from '../utils/request'

/**
 * 查询用户的所有地址列表
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export const getAddressList = (userId) => request.get(`/address/list?userId=${userId}`)

/**
 * 查询用户的默认地址
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export const getDefaultAddress = (userId) => request.get(`/address/default?userId=${userId}`)

/**
 * 根据 ID 查询地址详情
 * @param {number} id - 地址 ID
 * @returns {Promise}
 */
export const getAddressById = (id) => request.get(`/address/${id}`)

/**
 * 新增地址
 * @param {Object} addressData - 地址数据
 * @returns {Promise}
 */
export const addAddress = (addressData) => request.post('/address', addressData)

/**
 * 修改地址
 * @param {Object} addressData - 地址数据
 * @returns {Promise}
 */
export const updateAddress = (addressData) => request.put('/address', addressData)

/**
 * 设为默认地址
 * @param {number} userId - 用户 ID
 * @param {number} id - 地址 ID
 * @returns {Promise}
 */
export const setDefaultAddress = (userId, id) => request.put(`/address/default/${id}?userId=${userId}`)

/**
 * 根据 ID 删除地址
 * @param {number} id - 地址 ID
 * @returns {Promise}
 */
export const deleteAddress = (id) => request.delete(`/address/${id}`)
