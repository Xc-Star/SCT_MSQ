import request from '@/common/request.js'

export const getOneMsqVO = (msqId) => request.get("/topic/getOne/" + msqId)

export const getMsqVO = (type) => request.get("/topic/getType/" + type)

export const submitMsq = (msqSubmitDTO) => request.post("/topic/submit", msqSubmitDTO)

export const getMsqResult = (id) => request.get("/msq/getMsqResult/" + id)