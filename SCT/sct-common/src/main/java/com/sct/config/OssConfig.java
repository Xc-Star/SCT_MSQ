package com.sct.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author SCT
 */
@Configuration
@Slf4j
@Data
public class OssConfig {

    /**
     * type 上传类型(0:本地 1：S3)
     */
    @Value("${oss.type}")
    Integer type;

}
