package com.itxing.myspring.webmvc.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author xing
 * @create 2020/8/21-gupaoedu-vip-spring
 */
public class SelfHandlerMapping {
    private Object controller; //保存方法对应的Controller实例对象
    private Method method;  //保存映射中的方法
    private Pattern pattern;//保存URL，使用的是正则

    public SelfHandlerMapping(Pattern pattern,Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Method getMethod() {
        return method;
    }

    public Object getController() {
        return controller;
    }
}
