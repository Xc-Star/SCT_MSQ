import request from '@/common/request.js'

/**
 * 成员留言列表（公开）
 * @param {number} [limit] 只取前 N 条，不传返回全部
 */
export const getMemberMessageList = (limit) =>
  request.get('/message/list', { params: limit ? { limit } : {} })
