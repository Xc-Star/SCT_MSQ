import request from '@/common/request.js'

export const fetchMemberList = (params) => request.get("/admin/member/page", { params })

export const removeMember = (params) => request.delete("/admin/member/remove", { params })