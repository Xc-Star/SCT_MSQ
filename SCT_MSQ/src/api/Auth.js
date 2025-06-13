import request from '@/common/request.js'

export const login = (loginData) => request.post("/admin/auth/login", loginData)

export const changePassword = (updatePasswordDTO) => request.post("/admin/auth/changePassword", updatePasswordDTO)