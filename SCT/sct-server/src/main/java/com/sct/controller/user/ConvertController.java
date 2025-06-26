package com.sct.controller.user;

import com.sct.controller.user.convert.ConvertEntity;
import com.sct.service.ConvertService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Title: ConvertController
 * @Author Xc_Star
 * @Package com.sct.controller.user
 * @Date 2025/6/26 23:33
 */

@RestController
@RequestMapping("/convert")
public class ConvertController {

    @Resource
    private ConvertService convertService;

    @PostMapping
    public ResponseEntity<byte[]> convertMaterialList(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        List<ConvertEntity> converts = convertService.parseConvertList(file);

        byte[] excelBytes = convertService.generateExcelFromTemplate(converts, response);

        String filename = file.getOriginalFilename().replace(".txt", ".xlsx");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}
