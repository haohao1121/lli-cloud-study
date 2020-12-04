package com.sky.lli.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 默认填充器关闭操作
 *
 * @author lihao (15215401693@163.com)
 * @date 2020/12/03
 */

@Slf4j
@Component
public class ModelMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入元对象字段填充（用于插入时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        /*
         * 设置实体属性setter进去的值，优先级要高于自动填充的值。
         * 如果实体没有设置该属性，就给默认值，防止entity的setter值被覆盖。
         */

        Object createDate = this.getFieldValByName("createTime", metaObject);
        if (null == createDate) {
            this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        }
        Object modifyDate = this.getFieldValByName("updateTime", metaObject);
        if (null == modifyDate) {
            this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }
        Object version = this.getFieldValByName("lockVersion", metaObject);
        if (null == version) {
            this.setFieldValByName("lockVersion", 0, metaObject);
        }

    }

    /**
     * 更新元对象字段填充（用于更新时对公共字段的填充）
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        //更新操作人和操作时间信息
    }
}
