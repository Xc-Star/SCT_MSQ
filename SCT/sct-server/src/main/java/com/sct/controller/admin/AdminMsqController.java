package com.sct.controller.admin;

import com.sct.dto.MsqResultPageDTO;
import com.sct.dto.MsqUpdateStatusDTO;
import com.sct.entity.Msq;
import com.sct.entity.MsqResult;
import com.sct.result.PageResult;
import com.sct.result.Result;
import com.sct.service.MsqService;
import org.springframework.web.bind.annotation.*;

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
        List<Msq> list = msqService.list();
        return Result.success(list);
    }

    @PostMapping("/save")
    public Result<?> saveMsq(@RequestBody Msq msq) {
        msqService.saveMsq(msq);
        return Result.success();
    }

    @GetMapping("/get/{id}")
    public Result<Msq> get(@PathVariable Long id) {
        return Result.success(msqService.getById(id));
    }

    @PutMapping("/update")
    public Result<?> updateMsq(@RequestBody Msq msq) {
        msqService.updateMsq(msq);
        return Result.success();
    }

    @PutMapping("/updateStatus")
    public Result<?> updateStatus(@RequestBody MsqUpdateStatusDTO dto) {
        msqService.updateStatus(dto.getId(), dto.getStatus());
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        msqService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/pageResult")
    public Result<PageResult<MsqResult>> pageResult(MsqResultPageDTO msqResultPageDTO) {
        return Result.success(msqService.getResultPage(msqResultPageDTO));
    }
}
