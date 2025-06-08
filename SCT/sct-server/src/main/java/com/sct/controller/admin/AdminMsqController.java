package com.sct.controller.admin;

import com.sct.entity.Msq;
import com.sct.result.Result;
import com.sct.service.MsqService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Title: AdminMsqController
 * @Author Xc_Star
 * @Package com.sct.controller.admin
 * @Date 2025/6/6 22:28
 */

@RestController
@RequestMapping("/admin/msq")
public class AdminMsqController {

    @Resource
    private MsqService msqService;

    @GetMapping("/list")
    public Result<List<Msq>> list() {
        return Result.success(msqService.list());
    }
}
