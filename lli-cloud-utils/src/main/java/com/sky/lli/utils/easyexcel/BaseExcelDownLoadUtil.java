package com.sky.lli.utils.easyexcel;

import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.fastjson.JSON;
import com.sky.lli.utils.exception.ExceptionEnum;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author lihao
 * @date 2020/06/19
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class BaseExcelDownLoadUtil {

    public static void prepare(HttpServletResponse response, String filename) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        // 进行url编码
        try {
            if (StringUtils.isEmpty(filename)) {
                filename = URLEncoder.encode("文件名", StandardCharsets.UTF_8.toString());
            } else {
                filename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
            }
        } catch (UnsupportedEncodingException e) {
            log.error("文件编码错误", e);
        }
        response.setHeader("Content-disposition", "attachment;filename=" + filename + ".xlsx");
    }

    public static void fail(HttpServletResponse response) throws IOException {
        // 重置response
        response.reset();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.getWriter().write(JSON.toJSONString(ExceptionEnum.SYS_FAILURE_EXCEPTION));
    }

    public static void write(HttpServletResponse response, ExcelWriterSheetBuilder builder, String filename,
                             List<List<String>> excelHeadTittle, List<?> excelDataList) throws IOException {
        try {
            BaseExcelDownLoadUtil.prepare(response, filename);
            builder.head(excelHeadTittle);
            builder.doWrite(excelDataList);
        } catch (Exception e) {
            log.error("excel写入出错", e);
            BaseExcelDownLoadUtil.fail(response);
        }
    }

    public static void write(HttpServletResponse response, ExcelWriterSheetBuilder builder, String filename,
                             Supplier<List<List<String>>> headTitleSupplier, Supplier<List<?>> excelDataSupplier)
            throws IOException {
        try {
            BaseExcelDownLoadUtil.prepare(response, filename);
            builder.head(headTitleSupplier.get());
            builder.doWrite(excelDataSupplier.get());
        } catch (Exception e) {
            log.error("excel写入出错", e);
            BaseExcelDownLoadUtil.fail(response);
        }
    }

    public static List<List<String>> simpleHeadTitle(String[] headTitle) {
        List<List<String>> headTittleList = new ArrayList<>(headTitle.length);
        for (String value : headTitle) {
            headTittleList.add(Collections.singletonList(value));
        }
        return headTittleList;
    }

    public static List<List<String>> simpleHeadTitle(List<String> headTitle) {
        return simpleHeadTitle(headTitle.toArray(new String[]{}));
    }
}
