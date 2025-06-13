package com.sct.controller.admin;

import com.sct.dto.AdminLoginDTO;
import com.sct.dto.UpdatePasswordDTO;
import com.sct.result.Result;
import com.sct.service.AuthService;
import com.sct.vo.LoginVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Title: AdminAuthController
 * @Author Xc_Star
 * @Package com.sct.controller.admin
 * @Date 2025/6/8 01:10
 */

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @Resource
    private AuthService authService;

    /**
     * 登录
     * @param adminLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        return Result.success(authService.login(adminLoginDTO));
    }


    @PostMapping("/changePassword")
    public Result<?> changePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        authService.changePassword(updatePasswordDTO);
        return Result.success();
    }
}
