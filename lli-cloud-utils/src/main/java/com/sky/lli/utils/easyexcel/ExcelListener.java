package com.sky.lli.utils.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/07/10
 */

@Slf4j
@Getter
public class ExcelListener<T> extends AnalysisEventListener<T> {

    private List<T> dataList = new ArrayList<>();

    @Override
    public void invoke(T data, AnalysisContext analysisContext) {
        Map<Integer, Cell> cellMap = analysisContext.readRowHolder().getCellMap();
        if (MapUtils.isNotEmpty(cellMap)) {
            log.info("解析到一条数据:{}", JSON.toJSONString(data));
            dataList.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("所有数据解析完成！总量:{}", dataList.size());
    }
}
