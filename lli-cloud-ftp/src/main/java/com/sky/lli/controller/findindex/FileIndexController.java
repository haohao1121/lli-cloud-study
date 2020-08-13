package com.sky.lli.controller.findindex;

import com.sky.lli.exception.ExceptionFtpEnum;
import com.sky.lli.model.FileIndex;
import com.sky.lli.service.fileindex.IFileIndexService;
import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
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
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * 描述：文件上传Controller
 *
 * @author nicai
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
            return ResultResponseUtils.error(ExceptionFtpEnum.SYS_FAILURE_EXCEPTION);
        }

        //查询文件索引服务
        FileIndex fileIndexByUniqueNo = fileIndexService.getFileIndexByUniqueNo(fileUniqueNo);
        if (fileIndexByUniqueNo == null) {
            log.info("根据唯一号查询文件信息为空fileIndexByUniqueNo:{}", fileUniqueNo);
            return ResultResponseUtils.error(ExceptionFtpEnum.SYS_FAILURE_EXCEPTION);
        }

        //处理成功
        stopWatch.stop();
        log.info("下载文件FileIndex：{},处理时间:{}ms", fileIndexByUniqueNo, stopWatch.getTotalTimeMillis());
        return ResultResponseUtils.success(toJSONString(fileIndexByUniqueNo));
    }

    @PostMapping(MODIFY_FILE_NAME)
    public ResponseResult<Object> modifyFileName(@RequestBody FileIndex fileIndex) {
        if (isBlank(fileIndex.getFileUniqueId())) {
            return ResultResponseUtils.error(ExceptionFtpEnum.SYS_FAILURE_EXCEPTION);
        }
        FileIndex fileIndexByUniqueNo = this.fileIndexService.getFileIndexByUniqueNo(fileIndex.getFileUniqueId());
        if (isNull(fileIndexByUniqueNo)) {
            return ResultResponseUtils.error(ExceptionFtpEnum.SYS_FAILURE_EXCEPTION);
        }
        if (isBlank(fileIndex.getFileName())) {
            return ResultResponseUtils.error(ExceptionFtpEnum.SYS_FAILURE_EXCEPTION);
        }
        this.fileIndexService.modifyFileName(fileIndex);
        return ResultResponseUtils.success();
    }
}
