import request from '../utils/request'

/**
 * 用户登录
 * @param {Object} loginData - 登录数据
 * @param {string} loginData.username - 用户名
 * @param {string} loginData.password - 密码
 * @returns {Promise}
 */
export const login = (loginData) => request.post('/user/login', loginData)

/**
 * 用户注册
 * @param {Object} registerData - 注册数据
 * @param {string} registerData.username - 用户名
 * @param {string} registerData.password - 密码
 * @param {string} registerData.phone - 手机号
 * @returns {Promise}
 */
export const register = (registerData) => request.post('/user/register', registerData)

/**
 * 获取用户信息
 * @param {number} userId - 用户 ID
 * @returns {Promise}
 */
export const getUserInfo = (userId) => request.get(`/user/info?userId=${userId}`)

/**
 * 用户退出登录
 * @returns {Promise}
 */
export const logout = () => request.post('/user/logout')
