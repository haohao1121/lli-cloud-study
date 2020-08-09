package com.sky.lli.exception;

import com.sky.lli.util.restful.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLSyntaxErrorException;

import static com.sky.lli.util.restful.ResultResponseUtils.error;

/**
 * 描述：全局异常处理
 * CLASSPATH: com.sky.lli.exception.GlobalExceptionHandler
 *
 * @author klaus
 * @date 2017/11/21
 */
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 本方法处理 Exception 抛出异常的情况
     *
     * @param e 传递的最顶级的异常
     * @return 返回响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult<Object> handle(Exception e) {

        //自定义异常处理
        if (e instanceof EnumException) {
            EnumException enumException = (EnumException) e;
            IExceptionEnum responseEnum = enumException.getResponseEnum();
            log.error("异常提示如下，CODE:{}，额外 Message:{}", responseEnum.getCode(), responseEnum.getMessage());
            return error(responseEnum, responseEnum.getMessage() + enumException.getSuffix());
        }

        log.error("异常如下行所示：", e);
        //处理方法不支持异常
        if (e instanceof HttpRequestMethodNotSupportedException) {
            return error(ExceptionEnum.SYS_METHOD_NOT_ALLOWED);
        }
        //JSON数据格式错误
        if (e instanceof HttpMessageNotReadableException) {
            return error(ExceptionEnum.SYS_JSON_DATA_ERROR);
        }
        //请求数据类型不正确
        if (e instanceof HttpMediaTypeNotSupportedException) {
            return error(ExceptionEnum.SYS_UNSUPPORTED_MEDIA_TYPE);
        }
        //数据库表字段缺失
        if (e instanceof SQLSyntaxErrorException) {
            return error(ExceptionEnum.SYS_DATABASE_FIELD_NOT_EXIST);
        }
        //数据库调用失败
        if (e instanceof DataAccessException) {
            return error(ExceptionEnum.SYS_DATABASE_FAILURE);
        }

        //最终返回
        return error(ExceptionEnum.SYS_FAILURE_EXCEPTION);
    }

}
