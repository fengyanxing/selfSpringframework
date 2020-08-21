package com.itxing.myspring.annotation;

import java.lang.annotation.*;

/**
 * @author xing
 * @create 2020/8/15-myselfSpring
 * 自定义注解，用于自己的类的表示，在系统中能够被jvm识别
 * 源注解@Target 用于表示该注解的使用范围
 * 源注解@Retention 用于标识注解的级别(SOURCE<CLASS<RUNTIME)
 * 源注解@Deprecated 说明该注解可用于javadoc中
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelfAutowired {
    String value() default "";
}
