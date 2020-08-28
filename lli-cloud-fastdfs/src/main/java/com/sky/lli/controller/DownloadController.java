package com.sky.lli.controller;

import com.luhuiguo.fastdfs.domain.StorePath;
import com.luhuiguo.fastdfs.service.FastFileStorageClient;
import com.sky.lli.exception.ControllerException;
import com.sky.lli.exception.ExceptionEnum;
import com.sky.lli.model.FileIndex;
import com.sky.lli.service.FileIndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;

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

    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    private FileIndexService fileIndexService;

    /**
     * 方法说明: 根据文件唯一号下载文件(二进制流方式下载)
     *
     * @param uniqNo   文件唯一号
     * @param response response
     *
     * @date 2020-08-27
     * @author lihao
     */
    @GetMapping(DOWNLOAD_BY_ID)
    public void downloadById(@RequestParam("uniqNo") String uniqNo, HttpServletResponse response) {
        log.info(" download fileIndex by id :{}", uniqNo);

        //获取文件信息
        FileIndex fileIndex = this.fileIndexService.findFileByUniqNo(uniqNo);
        if (null == fileIndex) {
            throw new ControllerException(ExceptionEnum.SYS_DATABASE_NULL_FAILURE);
        }
        //下载文件
        download(fileIndex, response);
    }

    /**
     * 方法说明: 根据文件路径已流方式下载文件
     * nginx反向代理增加请求头Content-disposition及attachment
     * add_header Content-Disposition "attachment;filename=$arg_attname";
     *
     * @param fileIndex 文件信息
     * @param response  response
     *
     * @date 2020-08-27
     * @author lihao
     */
    private void download(FileIndex fileIndex, HttpServletResponse response) {

        try {
            //获取文件group和path信息
            StorePath storePath = StorePath.praseFromUrl(fileIndex.getFullPath());
            //从服务器获取文件
            byte[] data = this.fastFileStorageClient.downloadFile(storePath.getGroup(), storePath.getPath());

            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition",
                               "attachment;filename=" + URLEncoder.encode(fileIndex.getFileName(), "UTF-8"));

            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.write(data, outputStream);
            //关闭流
            outputStream.close();
        } catch (Exception e) {
            log.error("test fail-", e);
            throw new ControllerException(ExceptionEnum.SYS_FAILURE_EXCEPTION);
        }
    }
}
