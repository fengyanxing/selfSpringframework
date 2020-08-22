package com.itxing.myspring.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author xing
 * @create 2020/8/22-selfSpringframework
 */
public class SelfAdvice {
    private Object aspect;
    private Method adviceMethod;
    private String throwName;
    public SelfAdvice(Object aspect, Method adviceMethod) {
        this.adviceMethod = adviceMethod;
        this.aspect = aspect;
    }

    public Object getAspect() {
        return aspect;
    }

    public void setAspect(Object aspect) {
        this.aspect = aspect;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public void setAdviceMethod(Method adviceMethod) {
        this.adviceMethod = adviceMethod;
    }

    public String getThrowName() {
        return throwName;
    }

    public void setThrowName(String throwName) {
        this.throwName = throwName;
    }
}
