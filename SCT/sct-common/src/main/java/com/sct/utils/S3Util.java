package com.sct.utils;

import com.sct.properties.S3Properties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * S3文件上传工具。
 */
public class S3Util implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(S3Util.class);

    private final S3Properties properties;
    private final S3Client s3Client;

    public S3Util(S3Properties properties) {
        this.properties = Objects.requireNonNull(properties, "S3配置不能为空");

        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(resolveRegion()))
                .credentialsProvider(createCredentialsProvider());
        if (hasText(properties.getEndpoint())) {
            builder.endpointOverride(URI.create(normalizeEndpoint(properties.getEndpoint())));
        }
        if (properties.isPathStyleAccess()) {
            builder.forcePathStyle(true);
        }
        this.s3Client = builder.build();
    }

    /**
     * 上传文件并返回完整的公开访问地址。
     *
     * @param bytes       文件内容
     * @param objectName  对象名称
     * @param contentType 文件类型
     * @return 完整文件URL
     */
    public String upload(byte[] bytes, String objectName, String contentType) {
        if (bytes == null) {
            throw new IllegalArgumentException("文件内容不能为空");
        }

        String bucketName = requireBucketName();
        String objectKey = buildObjectKey(objectName);

        PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentLength((long) bytes.length);
        if (hasText(contentType)) {
            requestBuilder.contentType(contentType);
        }

        s3Client.putObject(requestBuilder.build(), RequestBody.fromBytes(bytes));

        String url = buildObjectUrl(bucketName, objectKey);
        log.info("文件上传到S3: {}", url);
        return url;
    }

    /**
     * 上传文件并返回完整的公开访问地址。
     *
     * @param bytes      文件内容
     * @param objectName 对象名称
     * @return 完整文件URL
     */
    public String upload(byte[] bytes, String objectName) {
        return upload(bytes, objectName, null);
    }

    private AwsCredentialsProvider createCredentialsProvider() {
        boolean hasAccessKey = hasText(properties.getAccessKeyId());
        boolean hasSecretKey = hasText(properties.getSecretAccessKey());
        if (hasAccessKey != hasSecretKey) {
            throw new IllegalArgumentException("S3访问密钥配置不完整");
        }
        if (hasAccessKey) {
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.getAccessKeyId(), properties.getSecretAccessKey()));
        }
        return DefaultCredentialsProvider.create();
    }

    private String resolveRegion() {
        return hasText(properties.getRegion()) ? properties.getRegion().trim() : "us-east-1";
    }

    private String requireBucketName() {
        if (!hasText(properties.getBucketName())) {
            throw new IllegalStateException("未配置S3存储桶名称: sct.s3.bucket-name");
        }
        return properties.getBucketName().trim();
    }

    private String buildObjectKey(String objectName) {
        if (!hasText(objectName)) {
            throw new IllegalArgumentException("S3对象名称不能为空");
        }

        String key = objectName.trim().replace('\\', '/');
        while (key.startsWith("/")) {
            key = key.substring(1);
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("S3对象名称不能为空");
        }

        String prefix = trimSlashes(properties.getObjectPrefix());
        return prefix.isEmpty() ? key : prefix + "/" + key;
    }

    private String buildObjectUrl(String bucketName, String objectKey) {
        String publicUrl = trimTrailingSlash(properties.getPublicUrl());
        if (!publicUrl.isEmpty()) {
            return normalizeEndpoint(publicUrl) + "/" + encodePath(objectKey);
        }

        String endpoint = properties.getEndpoint();
        if (!hasText(endpoint)) {
            String host = "s3." + resolveRegion() + ".amazonaws.com";
            if ("us-east-1".equals(resolveRegion())) {
                host = "s3.amazonaws.com";
            }
            if (properties.isPathStyleAccess()) {
                return "https://" + host + "/" + encodePath(bucketName) + "/" + encodePath(objectKey);
            }
            return "https://" + bucketName + "." + host + "/" + encodePath(objectKey);
        }

        URI endpointUri = URI.create(normalizeEndpoint(endpoint));
        String authority = endpointUri.getRawAuthority();
        String endpointPath = trimSlashes(endpointUri.getRawPath());
        String path = endpointPath;
        if (properties.isPathStyleAccess()) {
            path = appendPath(path, encodePath(bucketName));
            path = appendPath(path, encodePath(objectKey));
            return endpointUri.getScheme() + "://" + authority + "/" + path;
        }

        path = appendPath(path, encodePath(objectKey));
        return endpointUri.getScheme() + "://" + bucketName + "." + authority + "/" + path;
    }

    private static String appendPath(String base, String path) {
        return base.isEmpty() ? path : base + "/" + path;
    }

    private static String encodePath(String path) {
        return Arrays.stream(path.split("/", -1))
                .map(S3Util::encodePathSegment)
                .collect(Collectors.joining("/"));
    }

    private static String encodePathSegment(String segment) {
        try {
            return URLEncoder.encode(segment, StandardCharsets.UTF_8.name())
                    .replace("+", "%20")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("无法编码S3对象名称", e);
        }
    }

    private static String normalizeEndpoint(String endpoint) {
        String value = endpoint.trim();
        if (value.matches("^[a-zA-Z][a-zA-Z\\d+.-]*://.*$")) {
            return trimTrailingSlash(value);
        }
        return "https://" + trimTrailingSlash(value);
    }

    private static String trimSlashes(String value) {
        if (!hasText(value)) {
            return "";
        }
        String result = value.trim();
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private static String trimTrailingSlash(String value) {
        if (!hasText(value)) {
            return "";
        }
        String result = value.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @Override
    public void close() {
        s3Client.close();
    }
}
