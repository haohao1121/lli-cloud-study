package com.sky.lli.service.fileupload;

import com.sky.lli.model.FileIndex;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件上传抽象，所有实现的文件上传都实现此类
 *
 * @author klaus
 */
public interface IFileUpload {

    /**
     * 所有文件上传的父类方法
     *
     * @param fileMap   文件对象
     * @param fileIndex 文件索引信息
     * @return 是否上传成功
     */
    boolean fileUpload(MultipartFile fileMap, FileIndex fileIndex);

    /**
     * 所有文件上传的父类方法
     *
     * @param file         文件对象
     * @param filePath     文件存储路径
     * @param fileUniqueNo 文件唯一号
     * @return 是否上传成功
     */
    boolean fileUpload(File file, String filePath, String fileUniqueNo);

    /**
     * 所有文件上传的父类方法
     *
     * @param fileMap   文件对象
     * @param fileIndex 文件索引信息
     * @return 文件在oss上的存储路径
     */
    String comFileUpload(MultipartFile fileMap, FileIndex fileIndex);
}
