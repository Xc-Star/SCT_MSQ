import request from '@/common/request.js'

export const getMsqList = () => request.get("/admin/msq/list")

export const addMsq = (msqData) => request.post("/admin/msq/save", msqData)

export const getMsq = (msqId) => request.get("/admin/msq/get/" + msqId)

export const deleteMsq = (msqId) => request.delete("/admin/msq/delete/" + msqId)

export const updateMsq = (msqData) => request.put("/admin/msq/update", msqData)

export const updateStatus = (statusData) => request.put("/admin/msq/updateStatus", statusData)

export const adminUpdateMsq = (MsqUpdateDTO) => request.put("/admin/topic/update", MsqUpdateDTO)

export const adminGetMsqVO = (msqId) => request.get("/admin/topic/get/" + msqId)

export const adminGetResultPage = (params) => request.get("/admin/msq/pageResult", { params })