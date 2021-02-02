package com.sky.lli.exception;

import lombok.Getter;

/**
 * 描述：枚举异常
 *
 * @author klaus
 * @date 2017/11/20
 */
public enum ExceptionEnum implements IExceptionEnum {

    /**
     * //region  //endregion 可以将异常信息归类
     * 异常取值范围划分:
     * 系统保留异常: 00000 -- 10000
     */

    //region 系统类异常信息
    SYS_INVOKING_ERROR("LLI-00000", "操作失败"),
    SYS_INVOKING_SUCCESS("LLI-00001", "操作成功"),
    SYS_JSON_FAILURE("LLI-00003", "Json序列化对象错误"),
    SYS_METHOD_NOT_ALLOWED("LLI-00004", "POST/GET请求方式错误"),
    SYS_JSON_DATA_ERROR("LLI-00005", "JSON数据格式错误"),
    SYS_UNSUPPORTED_MEDIA_TYPE("LLI-00006", "请求数据类型不正确"),
    SYS_REQUEST_PARAM_MISSING("LLI-00007", "参数缺失"),
    SYS_SERVICE_NOT_FOUND_ERROR("LLI-00008", "不存在此接口"),
    SYS_DATABASE_FAILURE("LLI-00009", "数据库调用失败"),
    SYS_DATABASE_NULL_FAILURE("LLI-00010", "数据不存在"),
    SYS_DATABASE_FIELD_NOT_EXIST("LLI-00011", "数据库表字段缺失"),
    SYS_REDIS_LOCK_RUNNING_ERROR("LLI-00012", "存在进行中的任务,请稍后再试"),
    SYS_REQUEST_TOO_FREQUENT("LLI-00013", "请求过于频繁"),
    SYS_FAILURE_EXCEPTION("LLI-10000", "系统异常");
    //endregion

    @Getter
    private String code;
    @Getter
    private String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
