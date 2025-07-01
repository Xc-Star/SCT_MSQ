import request from '@/common/request.js'

export const getConfigList = () => request.get("/admin/config")

export const updateConfig = (data) => request.put("/admin/config", data)

export const getConfig = () => request.get("/config")