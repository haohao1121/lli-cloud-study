package com.sky.lli.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 描述：MDCLogsNames
 *
 * @author klaus
 * @date 2020-01-01
 */
@AllArgsConstructor
@Getter
public enum MDCLogsNames {
    /**
     * 请求参数
     */
    REQUEST_UUID("MdcTag"),
    /**
     * 请求参数
     */
    REQUEST_PARAM("请求参数"),
    /**
     * 请求TOKEN
     */
    REQUEST_TOKEN("请求TOKEN"),
    /**
     * 请求路径
     */
    REQUEST_URL("请求路径"),
    /**
     * 请求URI
     */
    REQUEST_URI("请求URI"),
    /**
     * 请求主机名称
     */
    REQUEST_HOST_NAME("客户端主机名"),
    /**
     * 客户端IP
     */
    REQUEST_CLIENT_IP("客户端IP"),
    /**
     * 请求方法
     */
    REQUEST_METHOD("请求方法"),
    /**
     * Controller方法名称
     */
    CONTROLLER_METHOD_NAME("Controller方法名称"),
    /**
     * 请求响应状态码
     */
    REQUEST_RESULT_CODE("请求响应状态码"),
    /**
     * 请求响应信息
     */
    REQUEST_RESULT_MESSAGE("请求响应信息"),
    /**
     * 花费时间
     */
    COST_TIME("花费时间");

    /**
     * 名称
     */
    private String mdczh;
}
