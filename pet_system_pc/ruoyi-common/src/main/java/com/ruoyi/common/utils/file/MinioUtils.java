package com.ruoyi.common.utils.file;

import com.ruoyi.common.config.MinioConfig;
import com.ruoyi.common.utils.IOUtils;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * Minio工具类
 */
@Slf4j
@Component
public class MinioUtils {

    /**
     * 上传文件
     *
     * @param bucketName 桶名称
     * @param fileName   文件名
     * @param file       文件资源
     * @return 返回文件的URL
     */
    public static String uploadFile(String bucketName,
                                    String fileName,
                                    MultipartFile file) throws IOException {
        String url = fileName;
        // 获取MinioClient对象
        MinioClient minioClient = SpringUtils.getBean(MinioClient.class);
        try {
            // 上传文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            // 获取文件URL
//            url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
//                    .bucket(bucketName)
//                    .object(fileName)
//                    .method(Method.GET)
//                    .build());
//            url = url.substring(0, url.indexOf("?"));
            return ServletUtils.urlDecode(url);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * 获取文件流
     *
     * @return 二进制流
     */
    @SneakyThrows(Exception.class)
    public void getObject(String fileName, HttpServletResponse response) {
        try {
            MinioClient minioClient = SpringUtils.getBean(MinioClient.class);
            MinioConfig minioConfig = SpringUtils.getBean(MinioConfig.class);
            InputStream fileInputStream = minioClient.getObject(GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(fileName).build());
            IOUtils.copy(fileInputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("下载失败: {}", e.getMessage());
            e.printStackTrace();
        }
    }

}
