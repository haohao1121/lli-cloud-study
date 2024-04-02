package com.sky.lli.utils.express;

import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.util.stream.Collectors;

/**
 * 描述: QLExpress脚本引擎
 * 详细文档链接: https://github.com/alibaba/QLExpress
 *
 * @author lli
 * @date 2024/4/2
 */
@Slf4j
public class ExpressRunnerUtil {


    /**
     * 执行脚本
     *
     * @param contextData 表达式参数数据
     * @param express     表达式
     * @return 结果
     */
    public static Object runExpress(JSONObject contextData, String express) {
        try {
            ExpressRunner runner = new ExpressRunner();
            runner.setShortCircuit(false);
            DefaultContext<String, Object> context = buildExpressContext(contextData);
            return runner.execute(express, context, null, true, false);
        } catch (Exception e) {
            log.error("ExpressRunnerUtil runExpress error", e);
            throw new RuntimeException("ExpressRunnerUtil runExpress error:" + e.getMessage(), e);
        }
    }

    /**
     * context转换
     *
     * @param data context json数据
     * @return DefaultContext
     */
    public static DefaultContext<String, Object> buildExpressContext(JSONObject data) {
        if (MapUtils.isEmpty(data)) {
            return new DefaultContext<>();
        }
        //context转换
        return data.keySet().stream().collect(Collectors.toMap(key -> key, data::get, (a, b) -> b, DefaultContext::new));
    }
}
