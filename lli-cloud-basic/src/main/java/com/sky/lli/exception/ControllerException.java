package com.sky.lli.exception;


/**
 * 描述： 自定义控制层异常
 *
 * @author klaus
 * @date 2020-01-01
 */
public class ControllerException extends EnumException {

    private static final long serialVersionUID = 1L;

    public ControllerException(IExceptionEnum responseEnum, String suffix) {
        super(responseEnum, suffix);
    }

    public ControllerException(IExceptionEnum responseEnum) {
        super(responseEnum);
    }


}
