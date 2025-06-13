package com.sct.controller.admin;

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

    @GetMapping("/page")
    public Result<PageResult<AdminUser>> page(PageParam pageParam) {
        if (BaseContext.getCurrentId() != 0) {
            throw new BaseException("权限不足");
        }
        return Result.success(adminUserService.pageQuery(pageParam));
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody AdminUser adminUser) {
        if (BaseContext.getCurrentId() != 0) {
            throw new BaseException("权限不足");
        }
        if (adminUser.getId() == 0) {
            throw new BaseException("超级管理员账号不可修改");
        }
        adminUserService.update(adminUser);
        return Result.success();
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody AdminUser adminUser) {
        if (BaseContext.getCurrentId() != 0) {
            throw new BaseException("权限不足");
        }
        adminUserService.save(adminUser);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        if (BaseContext.getCurrentId() != 0) {
            throw new BaseException("权限不足");
        }
        if (id == 0) {
            throw new BaseException("超级管理员账号不可删除");
        }
        adminUserService.delete(id);
        return Result.success();
    }
}
