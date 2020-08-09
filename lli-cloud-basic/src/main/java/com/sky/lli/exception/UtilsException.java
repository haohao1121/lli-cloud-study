package com.sky.lli.exception;

/**
 * 描述：工具类中抛出的异常
 *
 * @author klaus
 * @date 2020-01-01
 */
public class UtilsException extends EnumException {
    private static final long serialVersionUID = 1L;

    /**
     * 包含自定义以及枚举异常
     *
     * @param responseEnum 异常枚举
     * @param suffix       自定义
     */
    public UtilsException(IExceptionEnum responseEnum, String suffix) {
        super(responseEnum, suffix);
    }

    /**
     * 只包含异常枚举
     *
     * @param responseEnum 异常枚举
     */
    public UtilsException(IExceptionEnum responseEnum) {
        super(responseEnum);
    }
}