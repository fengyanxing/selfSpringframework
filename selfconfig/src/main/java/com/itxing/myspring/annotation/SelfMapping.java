package com.itxing.myspring.annotation;

import java.lang.annotation.*;

/**
 * @author xing
 * @create 2020/8/15-gupaoedu-vip-spring
 */

@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SelfMapping {
    String value() default "";
}
