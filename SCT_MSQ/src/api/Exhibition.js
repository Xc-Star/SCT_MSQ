import request from '@/common/request.js'

/**
 * 展览列表（公开）
 * @param {string} [category] redstone-红石 building-建筑 other-其他，不传返回全部
 * @param {number} [limit] 只取前 N 条，不传返回全部
 */
export const getExhibitionList = (category, limit) => {
  const params = {}
  if (category) params.category = category
  if (limit) params.limit = limit
  return request.get('/exhibition/list', { params })
}

/**
 * 展览详情（公开，点开弹窗时调用，带多图与正文）
 * @param {number|string} id
 */
export const getExhibitionDetail = (id) => request.get(`/exhibition/${id}`)
