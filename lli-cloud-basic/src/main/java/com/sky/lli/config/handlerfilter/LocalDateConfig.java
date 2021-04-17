package com.sky.lli.config.handlerfilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * localDateTime 全局处理配置
 *
 * @author lihao (15215401693@163.com)
 * @date 2020/11/20
 */

@Configuration
public class LocalDateConfig {

    /**
     * 年月日
     */
    private static final String LOCAL_DATE = "yyyy-MM-dd";
    /**
     * 年月日 时分秒
     */
    private static final String LOCAL_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(LOCAL_DATE)));
        javaTimeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME)));
        //localDate按照 "yyyy-MM-dd"的格式进行序列化、反序列化

        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(
                DateTimeFormatter.ofPattern(LOCAL_DATE_TIME)));
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME)));
        //localDateTime按照 "yyyy-MM-dd HH:mm:ss"的格式进行序列化、反序列化

        objectMapper.registerModule(javaTimeModule);

        return objectMapper;
    }
}
