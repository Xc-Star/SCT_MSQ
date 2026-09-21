import request from '@/common/request.js'

/**
 * 后台展览内容分页查询
 * @param {object} params { pageNo, pageSize, keyword, category, status }
 */
export const fetchExhibitionPage = (params, config = {}) => request.get('/admin/exhibition/page', { ...config, params })

/**
 * 后台新增展览内容
 * @param {object} data { title, category, cover?, images, content, top?, status? }
 */
export const saveExhibition = (data) => request.post('/admin/exhibition', data)

/**
 * 后台修改展览内容
 * @param {object} data { id, title?, category?, cover?, images?, content?, top?, status? }
 */
export const updateExhibition = (data) => request.put('/admin/exhibition', data)

/**
 * 删除展览内容（逻辑删除）
 * @param {number} id
 */
export const removeExhibition = (id) => request.delete(`/admin/exhibition/${id}`)
