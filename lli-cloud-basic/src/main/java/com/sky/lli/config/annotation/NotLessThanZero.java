package com.sky.lli.config.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/10/10
 */

@Constraint(validatedBy = {NotLessThanZeroValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotLessThanZero {

    String message() default "必须大于0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
