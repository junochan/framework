package com.juno.framework.netty.annotation;

import java.lang.annotation.*;

/**
 * @Author: Juno
 * @Date: 2020/4/7 9:01
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NettyController {

    String value() default "";

}
