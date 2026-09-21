import request from '@/common/request.js'

export const fetchMemberList = (params, config = {}) => request.get("/admin/member/page", { ...config, params })

export const removeMember = (params) => request.delete("/admin/member/remove", { params })