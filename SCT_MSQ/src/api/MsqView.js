import request from '@/common/request.js'

export const getMsqVO = (msqId) => request.get("/topic/get/" + msqId)

export const adminGetMsqVO = (msqId) => request.get("/admin/topic/get/" + msqId)