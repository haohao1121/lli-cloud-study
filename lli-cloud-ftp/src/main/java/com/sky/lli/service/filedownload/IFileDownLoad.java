package com.sky.lli.service.filedownload;

/**
 * 所有文件下载的父类
 *
 * @author lihao
 */
public interface IFileDownLoad {
    /**
     * 所有文件下载的父类方法
     *
     * @param uniqueNo 文件唯一号
     * @param filePath 文件路径
     *
     * @return 是否下载到临时目录成功(存放唯一号)
     */
    boolean downloadFile(String uniqueNo, String filePath);
}
