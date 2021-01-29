package com.sky.lli.util;

import com.google.common.net.UrlEscapers;
import com.sky.lli.exception.MailExceptionEnum;
import com.sky.lli.exception.ServiceException;
import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;


/**
 * 描述：
 * CLASSPATH: com.sky.lli.util.FileDownloadUtil
 * VERSION:   1.0
 * Created by lihao
 * DATE: 2019-12-11
 */

public class FileDownloadUtil {


    /**
     * 方法说明: 下载文件
     *
     * @param filePath 文件路径
     * @param savePath 保存路径
     */
    public static String downloadFile(String filePath, String savePath) {
        //判断是http路径还是绝对路径
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }
        //下载路径不能为空
        if (StringUtils.isEmpty(savePath)) {
            throw new ServiceException(MailExceptionEnum.MAIL_IMG_INLINE_FAIL);
        }

        //文件下载或拷贝
        if (filePath.startsWith("http://") || filePath.startsWith("https://")) {
            return downloadFromUrl(filePath, savePath, getFileName(filePath));
        } else {
            return copyFromFilePath(filePath, savePath, getFileName(filePath));
        }
    }


    /**
     * 方法说明: 获取文件名称
     *
     * @param filePath 文件路径
     * @return 文件名称
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return filePath;
        }
        //获取文件名,文件路径最后一个 '/' 之后的字符串
        String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
        if (StringUtils.isEmpty(fileName)) {
            return filePath;
        }
        return fileName;
    }

    /**
     * 方法说明: 生成文件完整保存路径
     *
     * @param savePath    保存路径
     * @param newFileName 文件名
     */
    private static String getNewFilePath(String savePath, String newFileName) {
        //文件保存位置,保存目录+当前日期
        StringBuilder path = new StringBuilder(savePath);
        path.append(File.separatorChar).append(LocalDate.now()).append(File.separatorChar);
        File saveDir = new File(savePath);
        // 如果目录不存在就创建
        if (!saveDir.exists() && saveDir.isDirectory()) {
            boolean result = saveDir.mkdirs();
            if (!result) {
                throw new ServiceException(MailExceptionEnum.MAIL_FILE_MKDIR_FAIL);
            }
        }
        return path.append(newFileName).toString();
    }


    /**
     * 方法说明: 根据 http url下载文件
     *
     * @param url         http文件url
     * @param savePath    保存路径
     * @param newFileName 自定义文件名
     */
    public static String downloadFromUrl(String url, String savePath, String newFileName) {

        //文件路径
        String filePath = getNewFilePath(savePath, newFileName);
        try {
            //路径处理
            URL httpUrl = new URL(UrlEscapers.urlFragmentEscaper().escape(url));
            //指定目录生成文件
            File file = new File(filePath);
            FileUtils.copyURLToFile(httpUrl, file);
        } catch (Exception e) {
            throw new ServiceException(MailExceptionEnum.MAIL_FILE_DOWNLOAD_FAIL);
        }
        return filePath;
    }

    /**
     * 方法说明: 文件拷贝
     *
     * @param fileSrc     文件路径
     * @param savePath    保存路径
     * @param newFileName 自定义文件名
     */
    public static String copyFromFilePath(String fileSrc, String savePath, String newFileName) {

        //文件路径
        String filePath = getNewFilePath(savePath, newFileName);
        try {
            //指定目录生成文件
            File file = new File(filePath);
            FileUtils.copyFile(new File(fileSrc), file);
        } catch (Exception e) {
            throw new ServiceException(MailExceptionEnum.MAIL_FILE_DOWNLOAD_FAIL);
        }
        return filePath;
    }

}
