import request from '@/common/request.js'

export const getMsqList = () => request.get("/admin/msq/list")