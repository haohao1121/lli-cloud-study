package com.sky.lli.exception;

/**
 * 描述: 异常枚举接口,各业务自定义异常枚举实现当前类,并自定义异常代码范围
 *
 * @author klaus
 * @date 202-01-01
 */

public interface IExceptionEnum {
    /**
     * 获取错误码
     *
     * @return 获取错误码
     */
    String getCode();

    /**
     * 获取错误信息
     *
     * @return 获取错误信息
     */
    String getMessage();
}