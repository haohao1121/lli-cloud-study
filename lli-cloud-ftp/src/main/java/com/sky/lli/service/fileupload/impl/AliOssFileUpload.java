package com.sky.lli.service.fileupload.impl;

import com.sky.lli.config.properties.FileStorageProperties;
import com.sky.lli.config.properties.OssProperties;
import com.sky.lli.model.FileIndex;
import com.sky.lli.service.fileupload.IFileUpload;
import com.sky.lli.util.OssHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;

/**
 * 创建时间：2018/四月/17
 *
 * @author klaus
 * 类名：AliOssFileUpload
 * 描述：阿里对象存储实现文件上传
 */
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "alioos")
@Slf4j
public class AliOssFileUpload implements IFileUpload {

    @Resource
    private FileStorageProperties fileStorageProperties;

    @Resource
    private OssProperties ossProperties;

    /**
     * 所有文件上传的父类方法(本方法不支持断点续传功能，请勿上传大文件)
     *
     * @param multipartFile 文件对象
     * @param fileIndex     文件索引信息
     *
     * @return 是否上传成功
     */
    @Override
    public boolean fileUpload(MultipartFile multipartFile, FileIndex fileIndex) {
        String fileUniqueId = fileIndex.getFileUniqueId();
        //检查缓存目录
        File file = new File(fileStorageProperties.getTempPath() + fileUniqueId);
        try {
            FileUtils.forceMkdirParent(file);
            //缓存文件,减少FTP访问或者OSS
            multipartFile.transferTo(file.getAbsoluteFile());
            return OssHelper.uploadFileToAliOss(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(),
                                                ossProperties.getAccessKeySecret(), ossProperties.getBucketName(),
                                                fileUniqueId, file, false);
        } catch (Exception e) {
            log.error("transferTo保存文件错误", e);
        }
        return false;
    }

    /**
     * 所有文件上传的父类方法(支持断点续传功能，用来上传大文件)）
     *
     * @param file         文件对象
     * @param filePath     文件存储路径
     * @param fileUniqueNo 文件唯一号
     *
     * @return 是否上传成功
     */
    @Override
    public boolean fileUpload(File file, String filePath, String fileUniqueNo) {
        //上传文件到OSS
        return OssHelper.uploadFileToAliOss(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(),
                                            ossProperties.getAccessKeySecret(), ossProperties.getBucketName(),
                                            fileUniqueNo, file, true);
    }

    /**
     * 所有文件上传的父类方法(本方法不支持断点续传功能，请勿上传大文件)
     *
     * @param multipartFile 文件对象
     * @param fileIndex     文件索引信息
     *
     * @return 文件在oss上的保存路径
     */
    @Override
    public String comFileUpload(MultipartFile multipartFile, FileIndex fileIndex) {
        // 构建OSS的fileKey(时间戳+文件ID+文件原名称)
        String ossFileKey =
                        DateFormatUtils.format(new Date(), "yyyyMMdd") + File.separator + fileIndex.getFileUniqueId()
                                        + fileIndex.getFileName();

        //检查缓存目录
        File file = new File(fileStorageProperties.getTempPath() + ossFileKey);
        try {
            FileUtils.forceMkdirParent(file);
            //缓存文件
            multipartFile.transferTo(file.getAbsoluteFile());
            //默认为非断点续传
            log.info("上传OSS的key为{}", ossFileKey);
            Boolean uploadSuccess = OssHelper
                            .uploadFileToAliOss(ossProperties.getEndpoint(), ossProperties.getAccessKeyId(),
                                                ossProperties.getAccessKeySecret(), ossProperties.getComBucketName(),
                                                ossFileKey, file, false);
            //是否上传成功
            if (uploadSuccess) {
                return ossProperties.getPublicUrlPrefix() + File.separator + ossFileKey;
            }
        } catch (Exception e) {
            log.error("transferTo保存文件错误", e);
        }
        //上传失败返回空
        return null;
    }
}
