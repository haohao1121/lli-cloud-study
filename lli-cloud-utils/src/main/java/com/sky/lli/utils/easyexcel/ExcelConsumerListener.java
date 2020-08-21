package com.sky.lli.utils.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/07/10
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExcelConsumerListener {

    private static final Integer BATCH_COUNT = 100;

    /**
     * 方法说明: easyExcel读取数据监听器
     *
     * @param consumer   业务处理接口
     * @param batchCount 分批处理阙值
     * @param <T>        枚举
     */
    public static <T> AnalysisEventListener<T> getExcelListener(Consumer<List<T>> consumer, int batchCount) {
        return new AnalysisEventListener<T>() {

            private LinkedList<T> list = new LinkedList<>();

            @Override
            public void invoke(T t, AnalysisContext analysisContext) {
                log.info("解析到一条数据:{}", JSON.toJSONString(t));
                list.add(t);
                if (list.size() == batchCount) {
                    consumer.accept(list);
                    list.clear();
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                if (!list.isEmpty()) {
                    consumer.accept(list);
                }
            }
        };
    }

    /**
     * 方法说明: easyExcel读取数据监听器,分批处理阙值默认100
     *
     * @param consumer 业务处理接口
     * @param <T>      枚举
     */
    public static <T> AnalysisEventListener<T> getExcelListener(Consumer<List<T>> consumer) {
        return getExcelListener(consumer, BATCH_COUNT);
    }
}
