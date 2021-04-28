package com.sky.lli.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/12/03
 */

@MapperScan({ "com.sky.lli.dao.mapper" })
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        //插件集合
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //分页插件,IPage中 pageSize为 -1 代表不分页. PageHelper中 pageSize为 0 代表不分页
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        //乐观锁插件,链接:https://baomidou.com/guide/interceptor-optimistic-locker.html#optimisticlockerinnerinterceptor
        //用法: 实体类 version 字段上加  @Version 注解
        //仅支持 updateById(id) 与 update(entity, wrapper) 方法
        //在 update(entity, wrapper) 方法下, wrapper 不能复用!!!
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return interceptor;
    }
}
