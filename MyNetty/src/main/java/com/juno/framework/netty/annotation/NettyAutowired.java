package com.juno.framework.netty.annotation;

import java.lang.annotation.*;

/**
 * @Author: Juno
 * @Date: 2020/4/7 9:05
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NettyAutowired {
    boolean required() default true;
}
