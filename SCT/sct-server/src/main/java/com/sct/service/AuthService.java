package com.sct.service;

import com.sct.dto.AdminLoginDTO;
import com.sct.dto.UpdatePasswordDTO;
import com.sct.vo.LoginVO;

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
    LoginVO login(AdminLoginDTO adminLoginDTO);

    /**
     * 修改密码
     * @param updatePasswordDTO 修改密码信息
     */
    void updatePassword(UpdatePasswordDTO updatePasswordDTO);
}
