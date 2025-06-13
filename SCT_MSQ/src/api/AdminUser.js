import request from '@/common/request.js'

// 获取管理员列表
export const getAdminList = (params) => request.get("/admin/user/page", { params })

// 新增管理员
export const addAdmin = (adminData) => request.post("/admin/user/save", adminData)

// 更新管理员
export const updateAdmin = (adminData) => request.put("/admin/user/update", adminData)

// 删除管理员
export const deleteAdmin = (adminId) => request.delete("/admin/user/delete/" + adminId) 