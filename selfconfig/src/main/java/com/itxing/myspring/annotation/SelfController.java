package com.itxing.myspring.annotation;

import java.lang.annotation.*;

/**
 * @author xing
 * @create 2020/8/15-myselfSpring
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelfController {
    String value() default "";
}
