package com.sky.lli.util;

import ch.qos.logback.core.util.FileSize;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.UploadFileRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * 方法说明: oss相关配置
 *
 * @author klaus
 * @date 2020/8/14
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OssHelper {
    /**
     * 上传文件到OSS
     *
     * @param ossFileKey       文件唯一号
     * @param file             文件对象
     * @param enableCheckpoint 是否支持断点续传
     * @param bucketName       存储空间
     * @return 是否成功
     */
    public static Boolean uploadFileToAliOss(String endPoint,
                                             String accessKeyId,
                                             String accessKeySecret,
                                             String bucketName,
                                             String ossFileKey,
                                             File file,
                                             boolean enableCheckpoint) {
        //上传成功标志
        boolean uploadSuccess = false;

        OSSClient ossClient = new OSSClient(endPoint,
                accessKeyId,
                accessKeySecret);

        //处理单文件上传
        UploadFileRequest uploadFileRequest;
        //是否启用断点续传功能
        if (enableCheckpoint) {
            uploadFileRequest = new UploadFileRequest(bucketName,
                    ossFileKey, file.getAbsolutePath(), FileSize.MB_COEFFICIENT, 5);
        } else {
            uploadFileRequest = new UploadFileRequest(bucketName, ossFileKey);
            uploadFileRequest.setUploadFile(file.getAbsolutePath());
        }

        try {
            String eTag = ossClient.uploadFile(uploadFileRequest).getMultipartUploadResult().getETag();
            if (!StringUtils.isEmpty(eTag)) {
                uploadSuccess = true;
            }
        } catch (Exception e) {
            log.error("上传到OSS错误", e);
        } catch (Throwable throwable) {
            log.error("上传OSS错误", throwable);
        } finally {
            ossClient.shutdown();
        }
        return uploadSuccess;
    }
}
