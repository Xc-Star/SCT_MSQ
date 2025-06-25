package com.sct.controller;

import com.sct.result.Result;
import com.sct.service.OssService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Title: FIleUploadController
 * @Author Xc_Star
 * @Package com.sct.controller
 * @Date 2025/6/26 01:17
 */


@RestController
@RequestMapping("/upload")
public class FIleUploadController {

    @Resource
    private OssService ossService;

    @PostMapping("/image")
    public Result<?> upload(@RequestParam("file") MultipartFile file) {
        return Result.success(ossService.saveOneImage(file));
    }
}
