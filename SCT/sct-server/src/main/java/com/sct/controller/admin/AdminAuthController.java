package com.sct.controller.admin;

import com.sct.dto.AdminLoginDTO;
import com.sct.result.Result;
import com.sct.service.AuthService;
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

    @RequestMapping("/login")
    public Result<String> login(@RequestBody AdminLoginDTO adminLoginDTO) {
        return Result.success(authService.login(adminLoginDTO));
    }
}
