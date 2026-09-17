package com.sct.service.impl;

import com.sct.config.OssConfig;
import com.sct.config.SctConfig;
import com.sct.service.OssService;
import com.sct.utils.FileUploadUtils;
import com.sct.utils.MimeTypeUtils;
import com.sct.utils.S3Util;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

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

    @Resource
    private S3Util s3Util;

    @Override
    public String saveOneImage(MultipartFile file) {
        return saveOne(file, MimeTypeUtils.IMAGE_EXTENSION, SctConfig.getTopicImagePath());
    }

    @Override
    public String saveOneFile(MultipartFile file) {
        return saveOne(file, MimeTypeUtils.MSQ_REQUEST_FILE_TYPE, SctConfig.getRequestPath());
    }

    @SneakyThrows
    public String saveOne(MultipartFile file, String[] fileType, String path) {
        Integer type = ossConfig.getType();
        if (type == null) {
            throw new IllegalStateException("未配置文件存储类型: oss.type");
        }
        switch (type) {
            case 0:
                // 本地存储
                return FileUploadUtils.upload(path, file, fileType);
            case 1:
                // S3存储
                FileUploadUtils.assertAllowed(file, fileType);
                String fileName = FileUploadUtils.extractFilename(file);
                String objectName = buildS3ObjectName(path, fileName);
                return s3Util.upload(file.getBytes(), objectName, file.getContentType());
            default:
                throw new IllegalArgumentException("不支持的存储类型: " + type);
        }

    }

    private String buildS3ObjectName(String path, String fileName) {
        String prefix = path;
        String profile = SctConfig.getProfile();
        if (profile != null && prefix.startsWith(profile)) {
            prefix = prefix.substring(profile.length());
        }
        prefix = prefix.replace(File.separatorChar, '/').replace('\\', '/');
        while (prefix.startsWith("/")) {
            prefix = prefix.substring(1);
        }
        while (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }
        return prefix.isEmpty() ? fileName : prefix + "/" + fileName;
    }
}
