package com.sct.service.impl;

import com.sct.config.OssConfig;
import com.sct.config.SctConfig;
import com.sct.service.OssService;
import com.sct.utils.FileUploadUtils;
import com.sct.utils.MimeTypeUtils;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Title: OssServiceImpl
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2025/6/26 00:11
 */

@Service
public class OssServiceImpl implements OssService {

    @Resource
    private OssConfig ossConfig;

    @Override
    public String saveOneImage(MultipartFile file) {
        return saveOne(file, MimeTypeUtils.IMAGE_EXTENSION);
    }

    @SneakyThrows
    public String saveOne(MultipartFile file, String[] fileType) {
        Integer type = ossConfig.getType();
        switch (type) {
            case 0:
                // 本地存储
                return FileUploadUtils.upload(SctConfig.getUploadPath(), file, fileType);
            default:
                throw new IllegalArgumentException("不支持的存储类型: " + type);
        }

    }
}
