package com.sct.controller.user;

import com.sct.entity.Config;
import com.sct.result.Result;
import com.sct.service.ConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title: ConfigController
 * @Author Xc_Star
 * @Package com.sct.controller.user
 * @Date 2025/7/2 00:15
 */

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Resource
    private ConfigService configService;

    @GetMapping
    public Result<List<Config>> list() {
        return Result.success(configService.list());
    }
}
