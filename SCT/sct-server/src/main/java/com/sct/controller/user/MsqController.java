package com.sct.controller.user;

import com.sct.entity.Msq;
import com.sct.result.Result;
import com.sct.service.MsqService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Title: MsqController
 * @Author Xc_Star
 * @Package com.sct.controller.user
 * @Date 2025/6/6 23:20
 */

@RestController
@RequestMapping("/msq")
public class MsqController {

    @Resource
    private MsqService msqService;

    @GetMapping("/selectMsq/{type}")
    public Result<List<Msq>> getTypeList(@PathVariable Integer type) {
        return Result.success(msqService.getTypeMsq(type));
    }
}
