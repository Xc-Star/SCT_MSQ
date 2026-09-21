import request from '@/common/request.js'

export const getMsqList = (config = {}) => request.get("/admin/msq/list", config)

export const addMsq = (msqData) => request.post("/admin/msq/save", msqData)

export const getMsq = (msqId) => request.get("/admin/msq/get/" + msqId)

export const deleteMsq = (msqId) => request.delete("/admin/msq/delete/" + msqId)

export const updateMsq = (msqData) => request.put("/admin/msq/update", msqData)

export const updateStatus = (statusData) => request.put("/admin/msq/updateStatus", statusData)

export const adminUpdateMsq = (MsqUpdateDTO) => request.put("/admin/topic/update", MsqUpdateDTO)

export const adminGetMsqVO = (msqId, config = {}) => request.get("/admin/topic/get/" + msqId, config)

export const adminGetResultPage = (params, config = {}) => request.get("/admin/msq/pageResult", { ...config, params })

export const getReviewInfo = (msqResultId) => request.get("/admin/topic/getReviewInfo/" + msqResultId)

export const updateResultStatus = (updateResultStatusDTO) => request.put("/admin/msq/updateResultStatus", updateResultStatusDTO)

export const deleteMsqResult = (msqResultId) => request.delete("/admin/msq/deleteMsqResult/" + msqResultId)