package com.sky.lli.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * 日期工具类
 *
 * @author klaus
 * @date 2020/06/22
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocalDateUtils {

    //实体类常用注解 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //或者配置全局 LocalDateTime 处理器

    /**
     * LocalDate 日期转换成年月数字形式,2020-07-30  -> 202007
     *
     * @param date LocalDate日期
     * @return data(int)
     */
    public static int truncateToYearMonth(LocalDate date) {
        return Integer.parseInt(DateTimeFormatter.ofPattern("yyyyMM").format(date));
    }

    /**
     * LocalDateTime 日期转换成年月数字形式,2020-07-30 20:00:20 -> 202007
     *
     * @param dateTime LocalDateTime
     * @return data(int)
     */
    public static int truncateToYearMonth(LocalDateTime dateTime) {
        return truncateToYearMonth(dateTime.toLocalDate());
    }

    /**
     * LocalDate 日期转换成年月数字形式,2020-07-30   -> 20200730
     *
     * @param date LocalDate日期
     * @return data(int)
     */
    public static int truncateToYearMonthDay(LocalDate date) {
        return Integer.parseInt(DateTimeFormatter.ofPattern("yyyyMMdd").format(date));
    }

    /**
     * LocalDateTime 日期转换成年月数字形式,2020-07-30 20:00:20  -> 20200730
     *
     * @param dateTime LocalDate日期
     * @return data(int)
     */
    public static int truncateToYearMonthDay(LocalDateTime dateTime) {
        return truncateToYearMonthDay(dateTime.toLocalDate());
    }

    /**
     * 获取下个月年月数字格式
     *
     * @return nextMonth (int)
     */
    public static int nextMonth() {
        return LocalDateUtils.truncateToYearMonth(LocalDateTime.now().plusMonths(1));
    }

    /**
     * 获取当前月年月数字格式
     *
     * @return currentMonth (int)
     */
    public static int currentMonth() {
        return LocalDateUtils.truncateToYearMonth(LocalDateTime.now());
    }

    /**
     * 获取月份第一天
     */
    public static LocalDate firstDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取月份第一天
     */
    public static LocalDate firstDayOfMonth(LocalDateTime dateTime) {
        return firstDayOfMonth(dateTime.toLocalDate());
    }

    /**
     * 获取月份最后一天
     */
    public static LocalDate lastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取月份最后一天
     */
    public static LocalDate lastDayOfMonth(LocalDateTime dateTime) {
        return lastDayOfMonth(dateTime.toLocalDate());
    }
}
