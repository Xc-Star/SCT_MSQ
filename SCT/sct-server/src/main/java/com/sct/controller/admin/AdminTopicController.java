package com.sct.controller.admin;

import com.sct.entity.MsqResult;
import com.sct.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Title: AdminTopicController
 * @Author Xc_Star
 * @Package com.sct.controller.admin
 * @Date 2025/6/6 22:28
 */

@RestController
@RequestMapping("/admin/topic")
public class AdminTopicController {

    @GetMapping("/list")
    public Result<MsqResult> list() {
        return Result.success(new MsqResult());
    }
}
