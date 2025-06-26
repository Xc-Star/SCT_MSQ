package com.sct.service;

import com.sct.controller.user.convert.ConvertEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Title: ConvertService
 * @Author Xc_Star
 * @Package com.sct.service
 * @Date 2025/6/26 23:38
 */
public interface ConvertService {

    /**
     * 解析转换列表
     * @param file 转换列表文件
     * @return 转换列表
     */
    List<ConvertEntity> parseConvertList(MultipartFile file) throws IOException;

    /**
     * 生成转换列表Excel
     * @param converts 转换列表
     * @return 转换列表Excel
     */
    byte[] generateExcelFromTemplate(List<ConvertEntity> converts, HttpServletResponse response) throws IOException;
}
