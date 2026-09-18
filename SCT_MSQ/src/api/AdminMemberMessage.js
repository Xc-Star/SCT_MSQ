import request from '@/common/request.js'

/**
 * 后台留言分页查询
 * @param {object} params { pageNo, pageSize, keyword, status }
 */
export const fetchMessagePage = (params) => request.get('/admin/message/page', { params })

/**
 * 后台手动新增留言
 * @param {object} data { playerId, qq?, content, top?, status? }
 */
export const saveMessage = (data) => request.post('/admin/message', data)

/**
 * 修改置顶 / 显示状态
 * @param {object} data { id, top?, status? }
 */
export const updateMessage = (data) => request.put('/admin/message', data)

/**
 * 删除留言（逻辑删除）
 * @param {number} id
 */
export const removeMessage = (id) => request.delete(`/admin/message/${id}`)
