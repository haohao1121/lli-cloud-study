package com.sky.lli.service.filedownload.impl;

import ch.qos.logback.core.util.FileSize;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.DownloadFileRequest;
import com.aliyun.oss.model.DownloadFileResult;
import com.sinosoft.cpyy.config.properties.FileStorageProperties;
import com.sinosoft.cpyy.config.properties.OssProperties;
import com.sinosoft.cpyy.service.filedownload.IFileDownLoad;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * FTP实现的文件下载
 */
@Service
@ConditionalOnProperty(name = "filestorage.type", havingValue = "alioos")
@Slf4j
public class AliOSSFileDownload implements IFileDownLoad {

    /**
     * 文件存储位置配置
     */
    private final FileStorageProperties fileStorageProperties;

    /**
     * oss配置
     */
    private final OssProperties ossProperties;

    @Autowired
    public AliOSSFileDownload(FileStorageProperties fileStorageProperties, OssProperties ossProperties) {
        this.fileStorageProperties = fileStorageProperties;
        this.ossProperties = ossProperties;
    }

    /**
     * 文件下载的方法FTP实现
     *
     * @param filePath FTP中的路径
     * @param uniqueNo FTP中的文件名称
     * @return 是否下载成功
     */
    @Override
    public boolean downloadFile(String uniqueNo, String filePath) {
        return downloadFromAliOSS(uniqueNo, fileStorageProperties.getTempPath(), false);
    }

    /**
     * 从AliOSS下载文件
     *
     * @param uniqueNo         文件唯一号
     * @param filePath         文件被下载位置
     * @param enableCheckpoint 是否开启断点续传
     * @return 是否下载成功
     */
    private boolean downloadFromAliOSS(String uniqueNo, String filePath, boolean enableCheckpoint) {
        OSSClient ossClient = new OSSClient(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
        Boolean downloadSuccess = false;
        try {
            //处理下载路径
            String downloadFile;
            if (filePath.endsWith("/")) {
                downloadFile = filePath + uniqueNo;
            } else {
                downloadFile = filePath + File.separator + uniqueNo;
            }
            DownloadFileRequest downloadFileRequest;
            if (enableCheckpoint) {
                downloadFileRequest = new DownloadFileRequest(
                        ossProperties.getBucketName(), uniqueNo, downloadFile, FileSize.MB_COEFFICIENT, 5, true);
            } else {
                downloadFileRequest = new DownloadFileRequest(ossProperties.getBucketName(), uniqueNo);
                downloadFileRequest.setDownloadFile(downloadFile);
            }
            DownloadFileResult downloadResult = ossClient.downloadFile(downloadFileRequest);
            if (StringUtils.isNotBlank(downloadResult.getObjectMetadata().getETag())) {
                downloadSuccess = true;
            }
        } catch (Throwable e) {
            log.error("OSS文件下载异常", e);
        } finally {
            ossClient.shutdown();
        }
        return downloadSuccess;
    }
}
