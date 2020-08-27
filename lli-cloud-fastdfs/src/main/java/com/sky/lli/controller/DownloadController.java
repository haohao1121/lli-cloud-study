package com.sky.lli.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.luhuiguo.fastdfs.domain.MetaData;
import com.luhuiguo.fastdfs.domain.StorePath;
import com.luhuiguo.fastdfs.service.FastFileStorageClient;
import com.sky.lli.exception.ControllerException;
import com.sky.lli.exception.ExceptionEnum;
import com.sky.lli.model.FileIndex;
import com.sky.lli.service.FileIndexService;
import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Set;

/**
 * 说明:
 *
 * @author klaus
 * @date 2020/8/22
 */

@Slf4j
@RestController
@RequestMapping("/download")
public class DownloadController {

    /**
     * 根据文件唯一号下载文件
     */
    private static final String DOWNLOAD_BY_ID = "downloadById";
    /**
     * 根据路径下载文件
     */
    private static final String DOWNLOAD_BY_PATH = "downloadByPath";

    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    private FileIndexService fileIndexService;

    /**
     * 方法说明: 根据文件唯一号下载文件
     *
     * @param uniqNo   文件唯一号
     * @param response response
     *
     * @date 2020-08-27
     * @author lihao
     */
    @GetMapping(DOWNLOAD_BY_ID)
    public ResponseResult<Object> downloadById(@RequestParam("uniqNo") String uniqNo, HttpServletResponse response) {
        log.info(" download fileIndex by id :{}", uniqNo);

        //获取文件信息
        FileIndex fileIndex = this.fileIndexService.findFileByUniqNo(uniqNo);
        if (null == fileIndex) {
            throw new ControllerException(ExceptionEnum.SYS_DATABASE_NULL_FAILURE);
        }

        //下载文件
        download(fileIndex);

        return ResultResponseUtils.success(this.fileIndexService.findFileByUniqNo(uniqNo));
    }

    /**
     * 方法说明: 根据文件全路径下载文件(二进制流方式下载)
     *
     * @param filePath 文件全路径
     * @param response response
     *
     * @date 2020-08-27
     * @author lihao
     */
    @GetMapping(DOWNLOAD_BY_PATH)
    public ResponseResult<Object> downloadByPath(@RequestParam("filePath") String filePath,
                                                 HttpServletResponse response) {
        log.info(" download fileIndex by filePath :{}", filePath);

        download(filePath, response);

        return ResultResponseUtils.success();
    }

    /**
     * 方法说明: 根据文件路径已流方式下载文件
     *
     * @param fileUrl  文件全路径
     * @param response response
     *
     * @date 2020-08-27
     * @author lihao
     */
    private void download(String fileUrl, HttpServletResponse response) {

        /*
         * 方法说明:
         * nginx反向代理增加请求头Content-disposition及attachment
         * add_header Content-Disposition "attachment;filename=$arg_attname";
         */

        try {
            //获取文件group和path信息
            StorePath storePath = StorePath.praseFromUrl(fileUrl);
            //从服务器获取文件
            byte[] data = this.fastFileStorageClient.downloadFile(storePath.getGroup(), storePath.getPath());
            //获取文件名称
            Set<MetaData> metadataSet = this.fastFileStorageClient
                            .getMetadata(storePath.getGroup(), storePath.getPath());
            String fileName = "undefined";
            if (CollectionUtil.isNotEmpty(metadataSet)) {
                for (MetaData metaData : metadataSet) {
                    if ("fileName".equalsIgnoreCase(metaData.getName())) {
                        fileName = metaData.getValue();
                    }
                }
            }

            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

            // 写出
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.write(data, outputStream);
        } catch (Exception e) {
            log.error("test fail-", e);
            throw new ControllerException(ExceptionEnum.SYS_FAILURE_EXCEPTION);
        }
    }

    /**
     * 方法说明: 文件下载到指定路径
     *
     * @param fileIndex 文件信息
     *
     * @date 2020-08-27
     * @author lihao
     */
    private void download(FileIndex fileIndex) {
        File file = new File("/Users/lihao/Desktop/" + fileIndex.getFileName());
        //获取文件group和path信息
        StorePath storePath = StorePath.praseFromUrl(fileIndex.getFullPath());
        //从服务器获取文件
        byte[] data = this.fastFileStorageClient.downloadFile(storePath.getGroup(), storePath.getPath());
        try {
            IOUtils.write(data, new FileOutputStream(file));
        } catch (IOException e) {
            log.error("test fail-", e);
            throw new ControllerException(ExceptionEnum.SYS_FAILURE_EXCEPTION);
        }
    }

}
