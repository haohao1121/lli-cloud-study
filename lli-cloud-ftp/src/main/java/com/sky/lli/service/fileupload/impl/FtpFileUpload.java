package com.sky.lli.service.fileupload.impl;

import com.sky.lli.config.properties.FileStorageProperties;
import com.sky.lli.config.properties.FtpProperties;
import com.sky.lli.config.properties.OssProperties;
import com.sky.lli.model.FileIndex;
import com.sky.lli.service.fileupload.IFileUpload;
import com.sky.lli.util.FtpHelper;
import com.sky.lli.util.OssHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * 创建时间：2018/四月/17
 *
 * @author klaus
 * 类名：FtpFileUpload
 * 描述：FTP实现文件上传
 */
@Service
@Slf4j
@ConditionalOnProperty(name = "storage.type", havingValue = "ftp")
public class FtpFileUpload implements IFileUpload {

    @Resource
    private FileStorageProperties fileStorageProperties;
    @Resource
    private OssProperties ossProperties;
    @Resource
    private FtpProperties ftpProperties;


    /**
     * 所有文件上传的父类方法
     *
     * @param file         文件对象
     * @param filePath     文件存储路径
     * @param fileUniqueNo 文件唯一号
     * @return 是否上传成功
     */
    @Override
    public boolean fileUpload(File file, String filePath, String fileUniqueNo) {
        //参数必须正常
        if (!file.exists()
                || !StringUtils.isNotBlank(filePath)
                || !StringUtils.isNotBlank(fileUniqueNo)) {
            return false;
        }

        //在FTP中实现上传
        try {
            FtpHelper.ftpUploaderBuild(ftpProperties.getHost(),
                    ftpProperties.getPort(), ftpProperties.getUserName(), ftpProperties.getUserPassword())
                    .upload(fileUniqueNo, new FileInputStream(file), filePath, false);
        } catch (IOException e) {
            log.error("FTP实现中，单文件上传异常,", e);
            return false;
        }
        return true;
    }

    /**
     * 所有文件上传的父类方法
     *
     * @param multipartFile 文件对象
     * @param fileIndex     文件索引信息
     * @return 文件在oss上的存储路径
     */
    @Override
    public String comFileUpload(MultipartFile multipartFile,
                                FileIndex fileIndex) {
        // 构建OSS的fileKey(时间戳+文件ID+文件原名称)
        try {
            String ossFileKey = DateFormatUtils.format(new Date(), "yyyyMMdd") + File.separator + fileIndex.getFileUniqueId() + fileIndex.getFileName();
            //检查缓存目录
            File file = new File(fileStorageProperties.getTempPath() + ossFileKey);

            FileUtils.forceMkdirParent(file);
            //缓存文件
            multipartFile.transferTo(file.getAbsoluteFile());
            //默认为非断点续传
            log.info("上传OSS的key为{}", ossFileKey);
            boolean uploadSuccess = OssHelper.uploadFileToAliOss(
                    ossProperties.getEndpoint(),
                    ossProperties.getAccessKeyId(),
                    ossProperties.getAccessKeySecret(),
                    ossProperties.getComBucketName(),
                    ossFileKey,
                    file,
                    false);
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

    /**
     * 文件上传方法
     *
     * @param multipartFile 文件对象
     * @param fileIndex     文件索引信息
     * @return 是否上传成功
     */
    @Override
    public boolean fileUpload(MultipartFile multipartFile, FileIndex fileIndex) {
        //检查缓存目录
        File file = new File(fileStorageProperties.getTempPath() + fileIndex.getFileUniqueId());

        //处理单文件上传
        try {
            boolean b = file.setWritable(true, false);
            if (b) {
                log.info("获取写入权限");
            }
            FileUtils.forceMkdirParent(file);
            //缓存文件,减少FTP访问或者OSS
            multipartFile.transferTo(file.getAbsoluteFile());
            FtpHelper.ftpUploaderBuild(ftpProperties.getHost(),
                    ftpProperties.getPort(), ftpProperties.getUserName(), ftpProperties.getUserPassword())
                    .upload(fileIndex.getFileUniqueId(), new FileInputStream(file),
                            fileIndex.getFilePath(), false);
        } catch (IOException e) {
            log.error("上传FTP,创建文件夹异常:", e);
            return false;
        }
        return true;
    }
}
