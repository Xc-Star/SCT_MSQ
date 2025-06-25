package com.sct.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Title: OssService
 * @Author Xc_Star
 * @Package com.sct.service
 * @Date 2025/6/26 00:11
 */
public interface OssService {

    String saveOneImage(MultipartFile file);
}
