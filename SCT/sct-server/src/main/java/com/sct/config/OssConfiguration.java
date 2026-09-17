package com.sct.config;

import com.sct.properties.S3Properties;
import com.sct.utils.S3Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，用于创建S3工具对象。
 */
@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public S3Util s3Util(S3Properties s3Properties) {
        log.info("开始创建S3文件上传工具类对象: endpoint={}, bucket={}, region={}",
                s3Properties.getEndpoint(), s3Properties.getBucketName(), s3Properties.getRegion());
        return new S3Util(s3Properties);
    }
}
