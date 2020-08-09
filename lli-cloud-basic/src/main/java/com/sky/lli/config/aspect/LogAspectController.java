package com.sky.lli.config.aspect;

import cn.hutool.core.util.IdUtil;
import com.sky.lli.config.handlerfilter.MDCLogsNames;
import com.sky.lli.util.restful.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;

import static java.util.Objects.isNull;

/**
 * 描述：controller日志切面
 *
 * @author klaus
 * @date 2020-01-01
 */

@Slf4j
@Order(5)
@Aspect
@Component
public class LogAspectController {

    /**
     * 请求Token
     */
    private static final String TOKEN = "Token";
    /**
     * MDC请求链路唯一标识
     */
    private static final String MDC_TAG = "MdcTag";

    /**
     * 环绕通知，决定真实的方法是否执行，而且必须有返回值。同时在所拦截方法的前后执行一段逻辑。
     *
     * @param pjp 连接点
     * @return 执行方法的返回值
     * @throws Throwable 抛出异常
     */
    @Around("execution(public * com.sky.lli.controller..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 请求计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 初始化MDC
        HashMap<String, String> mdcParam = getMdcInitMap(pjp);
        MDC.setContextMap(mdcParam);
        Object result;
        try {
            result = pjp.proceed();
        } catch (Throwable throwable) {
            MDC.put(MDCLogsNames.REQUEST_RESULT_CODE.getMdczh(), "ERROR");
            MDC.put(MDCLogsNames.REQUEST_RESULT_MESSAGE.getMdczh(), "抛出错误");
            MDC.put(MDCLogsNames.COST_TIME.getMdczh(), String.valueOf(stopWatch.getTotalTimeMillis()));
            log.error("请求异常", throwable);
            MDC.clear();
            throw throwable;
        }

        // 正常记录返回
        stopWatch.stop();
        if (result instanceof ResponseResult) {
            markSuccessInMDC(stopWatch, (ResponseResult) result);
        }

        // 清空MDC
        MDC.clear();
        return result;
    }

    /**
     * 标记请求成功
     *
     * @param stopWatch 计时器
     * @param result    返回信息
     */
    private void markSuccessInMDC(StopWatch stopWatch, ResponseResult result) {
        MDC.put(MDCLogsNames.REQUEST_RESULT_CODE.getMdczh(), result.getCode());
        MDC.put(MDCLogsNames.REQUEST_RESULT_MESSAGE.getMdczh(), result.getMessage());
        MDC.put(MDCLogsNames.COST_TIME.getMdczh(), String.valueOf(stopWatch.getTotalTimeMillis()));
    }

    /**
     * 初始化MDC信息
     *
     * @param pjp 切面
     * @return 初始化的信息
     */
    private HashMap<String, String> getMdcInitMap(ProceedingJoinPoint pjp) {
        HashMap<String, String> mdcParam = new HashMap<>();
        // 1.本次请求的UUID
        mdcParam.put(MDCLogsNames.REQUEST_UUID.getMdczh(), IdUtil.fastSimpleUUID());
        // 2.controller的方法名称
        mdcParam.put(MDCLogsNames.CONTROLLER_METHOD_NAME.getMdczh(),
                pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName());
        // 3.获取请求参数列表
        Object[] args = pjp.getArgs();
        if (args != null && args.length > 0) {
            mdcParam.put(MDCLogsNames.REQUEST_PARAM.getMdczh(), Arrays.toString(args));
        }
        // 4.获取Token
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        if (!isNull(request.getHeader(TOKEN))) {
            mdcParam.put(MDCLogsNames.REQUEST_TOKEN.getMdczh(), request.getHeader(TOKEN));
        }
        if (!isNull(request.getHeader(MDC_TAG))) {
            mdcParam.put(MDCLogsNames.REQUEST_UUID.getMdczh(), request.getHeader(MDC_TAG));
        }
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        assert response != null;
        // 返回MDC唯一标识,便于问题排查
        response.setHeader(MDC_TAG, mdcParam.get(MDCLogsNames.REQUEST_UUID.getMdczh()));
        // 5.获取请求路径
        mdcParam.put(MDCLogsNames.REQUEST_URL.getMdczh(), request.getRequestURL().toString());
        return mdcParam;
    }
}
