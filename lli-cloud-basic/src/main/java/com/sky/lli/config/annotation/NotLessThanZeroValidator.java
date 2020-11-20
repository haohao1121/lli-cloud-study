package com.sky.lli.config.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/10/10
 */
public class NotLessThanZeroValidator implements ConstraintValidator<NotLessThanZero, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (null == value) {
            return false;
        }
        if (value instanceof Number) {
            //initValue会有小数点精度问题,此处选择doubleValue
            return ((Number) value).doubleValue() > 0;
        }
        return false;
    }
}
