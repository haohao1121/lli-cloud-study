package com.sky.lli.exception;

/**
 * 描述：服务层自定义异常
 *
 * @author klaus
 * @date 2017/11/20
 */
public class ServiceException extends EnumException {

    private static final long serialVersionUID = 1L;

    public ServiceException(IExceptionEnum responseEnum, String suffix) {
        super(responseEnum, suffix);
    }

    public ServiceException(IExceptionEnum responseEnum) {
        super(responseEnum);
    }

}
