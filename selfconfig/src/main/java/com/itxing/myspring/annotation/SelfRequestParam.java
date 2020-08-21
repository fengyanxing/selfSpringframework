package com.itxing.myspring.annotation;

import java.lang.annotation.*;

/**
 * @author xing
 * @create 2020/8/15-gupaoedu-vip-spring
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelfRequestParam {
    String value() default "";

    String name() default "";

    boolean required() default true;

}
