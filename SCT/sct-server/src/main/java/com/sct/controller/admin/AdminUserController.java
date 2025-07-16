package com.sct.controller.admin;

import com.sct.annotation.SuperPermission;
import com.sct.context.BaseContext;
import com.sct.entity.AdminUser;
import com.sct.entity.common.PageParam;
import com.sct.exception.BaseException;
import com.sct.result.PageResult;
import com.sct.result.Result;
import com.sct.service.AdminUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Title: AdminUserController
 * @Author Xc_Star
 * @Package com.sct.controller.admin
 * @Date 2025/6/13 00:42
 */

@RestController
@RequestMapping("/admin/user")
public class AdminUserController {

    @Resource
    private AdminUserService adminUserService;

    @SuperPermission
    @GetMapping("/page")
    public Result<PageResult<AdminUser>> page(PageParam pageParam) {
        return Result.success(adminUserService.pageQuery(pageParam));
    }

    @SuperPermission
    @PutMapping("/update")
    public Result<?> update(@RequestBody AdminUser adminUser) {
        if (adminUserService.isSuperAdmin(adminUser.getId())) {
            throw new BaseException("超级管理员账号不可修改");
        }
        adminUserService.update(adminUser);
        return Result.success();
    }

    @SuperPermission
    @PostMapping("/save")
    public Result<?> save(@RequestBody AdminUser adminUser) {
        adminUserService.save(adminUser);
        return Result.success();
    }

    @SuperPermission
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (adminUserService.isSuperAdmin(id)) {
            throw new BaseException("超级管理员账号不可删除");
        }
        adminUserService.delete(id);
        return Result.success();
    }
}
