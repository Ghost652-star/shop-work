import request from '../utils/request'

export const createAfterSale = (data) => request.post('/after-sale', data)

export const getAfterSaleList = (userId) => request.get(`/after-sale/list?userId=${userId}`)

export const getAfterSaleDetail = (id) => request.get(`/after-sale/detail?id=${id}`)

export const cancelAfterSale = (id, userId) => request.put(`/after-sale/cancel?id=${id}&userId=${userId}`)
