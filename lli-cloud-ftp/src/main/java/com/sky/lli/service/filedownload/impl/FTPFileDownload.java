package com.sky.lli.service.filedownload.impl;

import com.sky.lli.config.properties.FileStorageProperties;
import com.sky.lli.config.properties.FtpProperties;
import com.sky.lli.service.filedownload.IFileDownLoad;
import com.sky.lli.util.FtpHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * FTP实现的文件下载
 *
 * @author lihao
 */
@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "ftp")
@Slf4j
public class FTPFileDownload implements IFileDownLoad {

    /**
     * 文件临时目录
     */
    @Resource
    private FileStorageProperties fileStorageProperties;

    /**
     * ftp配置
     */
    @Resource
    private FtpProperties ftpProperties;

    /**
     * 文件下载的方法FTP实现
     *
     * @param filePath FTP中的路径
     * @param uniqueNo FTP中的文件名称
     *
     * @return 是否下载成功
     */
    @Override
    public boolean downloadFile(String uniqueNo, String filePath) {
        try {
            FtpHelper.ftpUploaderBuild(ftpProperties.getHost(), ftpProperties.getPort(), ftpProperties.getUserName(),
                                       ftpProperties.getUserPassword())
                            .download(filePath, uniqueNo, fileStorageProperties.getTempPath());
            return true;
        } catch (IOException e) {
            log.error("Ftp下载失败", e);
            return false;
        }
    }
}
