package com.sky.lli.controller;

import com.luhuiguo.fastdfs.service.FastFileStorageClient;
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
    /**
     * 根据路径下载文件
     */
    private static final String DOWNLOAD_BY_PATH = "downloadByPath";


    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    private FileIndexService fileIndexService;


    @GetMapping(DOWNLOAD_BY_ID)
    public ResponseResult<Object> downloadById(@RequestParam("uniqNo") String uniqNo,
                                               HttpServletResponse response) {
        log.info(" download fileIndex by id :{}", uniqNo);

        //获取文件信息
        FileIndex fileIndex = this.fileIndexService.findFileByUniqNo(uniqNo);

        return ResultResponseUtils.success(this.fileIndexService.findFileByUniqNo(uniqNo));
    }


    @GetMapping(DOWNLOAD_BY_PATH)
    public ResponseResult<Object> downloadByPath(@RequestParam("filePath") String filePath,
                                                 HttpServletResponse response) {
        log.info(" download fileIndex by filePath :{}", filePath);


        return ResultResponseUtils.success();
    }

    public void download(String fileUrl, HttpServletResponse response) {

        /*
         * 方法说明:
         * nginx反向代理增加请求头Content-disposition及attachment
         * add_header Content-Disposition "attachment;filename=$arg_attname";
         */

        try {
            byte[] data = this.fastFileStorageClient.downloadFile(null, fileUrl);

            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("test.7z", "UTF-8"));

            // 写出
            ServletOutputStream outputStream = response.getOutputStream();
            IOUtils.write(data, outputStream);
        } catch (Exception e) {
            log.error("test fail-", e);
        }
    }


}
