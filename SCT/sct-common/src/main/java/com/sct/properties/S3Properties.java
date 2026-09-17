package com.sct.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * S3存储配置。
 */
@Component
@ConfigurationProperties(prefix = "sct.s3")
public class S3Properties {

    /**
     * S3 API地址。使用AWS官方S3时可以留空。
     */
    private String endpoint;

    /**
     * S3区域。
     */
    private String region = "us-east-1";

    /**
     * 访问密钥。
     */
    private String accessKeyId;

    /**
     * 私密访问密钥。
     */
    private String secretAccessKey;

    /**
     * 存储桶名称。
     */
    private String bucketName;

    /**
     * 文件对外访问地址，可配置CDN或自定义域名。
     */
    private String publicUrl;

    /**
     * 对象前缀。
     */
    private String objectPrefix;

    /**
     * 是否使用路径风格访问。MinIO、R2等S3兼容服务通常需要开启。
     */
    private boolean pathStyleAccess = true;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public String getObjectPrefix() {
        return objectPrefix;
    }

    public void setObjectPrefix(String objectPrefix) {
        this.objectPrefix = objectPrefix;
    }

    public boolean isPathStyleAccess() {
        return pathStyleAccess;
    }

    public void setPathStyleAccess(boolean pathStyleAccess) {
        this.pathStyleAccess = pathStyleAccess;
    }
}
