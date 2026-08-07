package com.ruoyi.common.config;

import io.minio.MinioClient;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        System.out.println("Minio endpoint: " + endpoint);
        System.out.println("Minio accessKey: " + accessKey);
        System.out.println("Minio secretKey: " + secretKey);
        System.out.println("Minio bucketName: " + bucketName);
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        try {
            // 检查存储桶是否存在，不存在则创建
            System.out.println("Checking if bucket exists: " + bucketName);
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                System.out.println("Creating bucket: " + bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                System.out.println("Bucket created successfully: " + bucketName);
            } else {
                System.out.println("Bucket already exists: " + bucketName);
            }
        } catch (Exception e) {
            System.err.println("Error creating bucket: " + e.getMessage());
            e.printStackTrace();
        }
        return minioClient;
    }
}
