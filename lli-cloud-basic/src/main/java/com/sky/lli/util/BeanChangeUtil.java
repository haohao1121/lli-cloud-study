package com.sky.lli.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：对象信息比较工具类
 *
 * @author klaus
 * @date 2019-03-22
 */

public class BeanChangeUtil<T> {

    private static Logger log = LoggerFactory.getLogger(BeanChangeUtil.class);


    /**
     * Date 2019-03-22
     * Author lihao
     * 方法说明: 新旧数据差异对比
     *
     * @param oldBean 原数据
     * @param newBean 新数据
     * @return 差异信息
     */
    public String contrast(Object oldBean, Object newBean) {
        return contrast(oldBean, newBean, new ArrayList<>());
    }

    /**
     * Date 2019-03-22
     * Author lihao
     * 方法说明: 新旧数据差异对比
     *
     * @param oldBean     原数据
     * @param newBean     新数据
     * @param ignorFields 忽略字段
     * @return 差异信息
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public String contrast(Object oldBean, Object newBean, List<String> ignorFields) {
        // 排除序列化属性
        ignorFields.add("serialVersionUID");
        StringBuilder str = new StringBuilder();
        T pojo1 = (T) oldBean;
        T pojo2 = (T) newBean;
        try {
            // 通过反射获取类的类类型及字段属性
            Class clazz = pojo1.getClass();
            Field[] fields = clazz.getDeclaredFields();
            int i = 1;
            for (Field field : fields) {
                // 排除不需要对比的属性
                if (ignorFields.contains(field.getName())) {
                    continue;
                }
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                // 获取对应属性值
                Method getMethod = pd.getReadMethod();
                Object o1 = getMethod.invoke(pojo1);
                Object o2 = getMethod.invoke(pojo2);
                if (o1 == null || o2 == null) {
                    continue;
                }
                if (!o1.toString().equals(o2.toString())) {
                    str.append(i).append("、字段名称:").append(field.getName()).append(",旧值:").append(o1).append(",新值:").append(o2).append(";");
                    i++;
                }
            }
        } catch (Exception e) {
            log.error("数据对比出错,错误信息如下:{}", e.getMessage());
        }
        return str.toString();
    }
}