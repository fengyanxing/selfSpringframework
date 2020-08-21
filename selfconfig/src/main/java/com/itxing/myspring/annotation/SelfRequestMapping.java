package com.itxing.myspring.annotation;

import java.lang.annotation.*;

/**
 * @author xing
 * @create 2020/8/15-gupaoedu-vip-spring
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SelfMapping
public @interface SelfRequestMapping {
    String name() default "";

    String value() default "";

    String[] path() default {};
}
