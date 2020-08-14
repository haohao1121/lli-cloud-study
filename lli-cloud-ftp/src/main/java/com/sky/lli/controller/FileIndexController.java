package com.sky.lli.controller;

import com.sky.lli.model.FileIndex;
import com.sky.lli.service.fileindex.IFileIndexService;
import com.sky.lli.util.restful.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static com.alibaba.fastjson.JSON.toJSONString;
import static com.sky.lli.enums.ExceptionEnum.*;
import static com.sky.lli.util.restful.ResultResponseUtils.error;
import static com.sky.lli.util.restful.ResultResponseUtils.success;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 描述：文件上传Controller
 *
 * @author klaus
 */
@RestController
@RequestMapping("/download")
@Slf4j
public class FileIndexController {
    /**
     * 获取文件信息
     */
    private static final String GET_INFO = "getInfo";
    /**
     * 修改文件名称
     */
    private static final String MODIFY_FILE_NAME = "modifyFileName";

    /**
     * 文件索引信息
     */
    @Resource
    private IFileIndexService fileIndexService;

    /**
     * 标准MD5名称长度
     */
    private static final int COMMON_MD5_LENGTH = 32;

    /**
     * 描述：下载文件
     *
     * @param fileUniqueNo 文件唯一号
     *
     * @return ResponseResult
     */
    @GetMapping(GET_INFO)
    public ResponseResult<Object> getInfo(String fileUniqueNo) {
        //处理开始时间
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //检查参数是否满足
        if (!StringUtils.isNotBlank(fileUniqueNo) || fileUniqueNo.length() <= COMMON_MD5_LENGTH) {
            log.info("传递参数为空");
            return error(DOWNLOAD_UNIQUENO_PARMA_IS_NULL);
        }

        //查询文件索引服务
        FileIndex fileIndexByUniqueNo = fileIndexService.getFileIndexByUniqueNo(fileUniqueNo);
        if (fileIndexByUniqueNo == null) {
            log.info("根据唯一号查询文件信息为空fileIndexByUniqueNo:{}", fileUniqueNo);
            return error(DOWNLOAD_FILE_IS_NOT_EXIST);
        }

        //处理成功
        stopWatch.stop();
        log.info("下载文件FileIndex：{},处理时间:{}ms", fileIndexByUniqueNo, stopWatch.getTotalTimeMillis());
        return success(toJSONString(fileIndexByUniqueNo));
    }

    /**
     * 方法说明: 修改文件名称
     *
     * @param fileIndex 文件信息
     *
     * @date 2020-08-14
     */
    @PostMapping(MODIFY_FILE_NAME)
    public ResponseResult<Object> modifyFileName(@RequestBody FileIndex fileIndex) {
        if (isBlank(fileIndex.getFileUniqueId())) {
            return error(FILE_UNIQUE_ID_NULL);
        }
        FileIndex fileIndexByUniqueNo = this.fileIndexService.getFileIndexByUniqueNo(fileIndex.getFileUniqueId());
        if (isNull(fileIndexByUniqueNo)) {
            return error(DOWNLOAD_FILE_IS_NOT_EXIST);
        }
        if (isBlank(fileIndex.getFileName())) {
            return error(DOWNLOAD_UNIQUENO_PARMA_IS_NULL);
        }
        this.fileIndexService.modifyFileName(fileIndex);
        return success();
    }
}
