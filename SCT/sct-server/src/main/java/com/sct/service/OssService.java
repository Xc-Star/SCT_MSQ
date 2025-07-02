package com.sct.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Title: OssService
 * @Author Xc_Star
 * @Package com.sct.service
 * @Date 2025/6/26 00:11
 */
public interface OssService {

    /**
     * 上传图片
     * @param file 图片
     * @return 图片URL
     */
    String saveOneImage(MultipartFile file);

    /**
     * 上传文件
     * @param file 文件
     * @return 文件URL
     */
    String saveOneFile(MultipartFile file);
}
