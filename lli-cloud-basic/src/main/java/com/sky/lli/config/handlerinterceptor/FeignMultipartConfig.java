package com.sky.lli.config.handlerinterceptor;

import com.sky.lli.util.MultipartFileUtils;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

/**
 * 说明: FeignClient通过MultipartFile传递文件
 * 链接：https://www.jianshu.com/p/4fd4f9d59135
 * 假设网站后台（ServiceA）、文件服务（ServiceB）与报表服务（ServiceC）分别是三个不同的微服务，需要满足如下两个场景：
 * <p>
 * 用户通过ServiceA上传头像到ServiceB。
 * ServiceC将生成的Excel二进制文件上传到ServiceB。
 * <p>
 * 代码:
 * '@FeignClient(value = "file-system", configuration = FeignMultipartConfig.class)
 * public interface FileSystemClient {
 * '@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType
 * .APPLICATION_JSON_UTF8_VALUE)
 * Resp<?> upload(@RequestPart("file") MultipartFile file);
 * }
 * 场景二: 借助 {@link MultipartFileUtils} 将文件转化为 MultipartFile
 *
 * @author lihao (15215401693@163.com)
 * @date 2020/08/28
 */

@Configuration
public class FeignMultipartConfig {

    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Autowired
    public FeignMultipartConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }

    @Bean
    @Primary
    @Scope("prototype")
    public Encoder multipartFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public feign.Logger.Level multipartLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}
