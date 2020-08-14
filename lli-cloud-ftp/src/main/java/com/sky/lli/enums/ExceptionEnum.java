package com.sky.lli.enums;

import com.sky.lli.exception.IExceptionEnum;
import lombok.Getter;

/**
 * 描述：枚举异常
 *
 * @author klaus
 * @date 2017/11/20
 */
public enum ExceptionEnum implements IExceptionEnum {

    //region 系统类异常信息
    //------------------------------上传文件-------------------------------
    UPLOAD_PARAM_MAP_IS_NULL("LLI-00009", "请求参数全部为空"),
    UPLOAD_PARAM_FILE_UPLOADER_ENUM_IS_NULL("LLI-0001O", "模块名为空"),
    UPLOAD_PARAM_FILE_UPLOADER_ENUM_IS_INVALID("LLI-00010", "模块名未识别"),
    UPLOAD_PARAM_FILE_INPUT_STREAM_IS_NULL("LLI-00011", "上传文件流为空"),
    UPLOAD_PARAM_FILE_INPUT_STREAM_TOO_MUCH("LLI-00011", "上传的文件过多"),
    UPLOAD_PARAM_FILE_NAME_IS_NULL("LLI-00012", "文件名为空"),

    //------------------------------下载文件-------------------------------
    DOWNLOAD_UNIQUENO_PARMA_IS_NULL("LLI-00013", "文件名为空"),
    DOWNLOAD_FILE_IS_NOT_EXIST("LLI-00013", "文件不存在"),

    //------------------------------临时文件下载-------------------------------
    TEMP_DOWNLOAD_FILE_IS_NOT_EXIST("LLI-05001", "需要下载的临时文件不存在"),
    TEMP_DOWNLOAD_SUB_TEMP_PATH_EXCEPTION("LLI-05002", "获取临时文件相对路径异常"),
    TEMP_DOWNLOAD_ZIP_EXCEPTION("LLI-05003", "临时文件压缩异常"),
    TEMP_DOWNLOAD_CREATE_EXCEPTION("LLI-05004", "临时文件拷贝异常"),

    //------------------------------批量下载永久文件-------------------------------
    BATCH_DOWNLOAD_UNIQUENO_PARMA_IS_NULL("LLI-06001", "需要下载的文件唯一号为空"),
    BATCH_DOWNLOAD_CREATE_SPACE("LLI-06002", "生成下载空间目录异常"),
    BATCH_DOWNLOAD_ZIP_FAIL("LLI-06003", "批量下载完全失败"),

    FILE_UNIQUE_ID_NULL("LLI-09001", "文件唯一号为空"),
    UNSUPPORTED_MEDIA_TYPE("CPYY-00006", "请求数据类型不正确"),
    SYS_FAILURE_EXCEPTION("LLI-20000", "系统异常");
    //endregion

    @Getter
    private String code;
    @Getter
    private String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }}
