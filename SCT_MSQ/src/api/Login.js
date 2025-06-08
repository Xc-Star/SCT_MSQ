import request from '@/common/request.js'

export const login = (loginData) => request.post("/admin/auth/login", loginData)