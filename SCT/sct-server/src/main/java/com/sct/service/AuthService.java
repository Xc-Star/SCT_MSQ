package com.sct.service;

import com.sct.dto.AdminLoginDTO;

/**
 * @Title: AuthService
 * @Author Xc_Star
 * @Package com.sct.service
 * @Date 2025/6/8 03:30
 */
public interface AuthService {

    /**
     * 管理员登录
     * @param adminLoginDTO 管理员登录信息
     * @return token
     */
    String login(AdminLoginDTO adminLoginDTO);
}
