package com.juno.framework.netty.annotation;

import com.juno.framework.netty.configuration.MyNettyAutoStarter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: Juno
 * @Date: 2020/4/8 11:06
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MyNettyAutoStarter.class)
public @interface EnableMyNetty {
}
