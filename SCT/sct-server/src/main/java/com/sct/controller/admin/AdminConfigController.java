package com.sct.controller.admin;

import com.sct.annotation.SuperPermission;
import com.sct.context.BaseContext;
import com.sct.entity.Config;
import com.sct.exception.BaseException;
import com.sct.result.Result;
import com.sct.service.ConfigService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title: AdminConfigController
 * @Author Xc_Star
 * @Package com.sct.controller.admin
 * @Date 2025/6/30 03:28
 */

@RestController
@RequestMapping("/admin/config")
public class AdminConfigController {

    @Resource
    private ConfigService configService;

    @SuperPermission
    @GetMapping
    public Result<List<Config>> list() {
        return Result.success(configService.list());
    }

    @SuperPermission
    @PutMapping
    public Result<?> update(@RequestBody Config config) {
        configService.update(config);
        return Result.success();
    }
}
