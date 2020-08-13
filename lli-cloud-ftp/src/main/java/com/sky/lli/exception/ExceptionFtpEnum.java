package com.sky.lli.exception;

import lombok.Getter;

/**
 * 描述：枚举异常
 *
 * @author klaus
 * @date 2017/11/20
 */
public enum ExceptionFtpEnum implements IExceptionEnum {

    //region 系统类异常信息
    SYS_FAILURE_EXCEPTION("LLI-20000", "系统异常");
    //endregion

    @Getter
    private String code;
    @Getter
    private String message;

    ExceptionFtpEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
