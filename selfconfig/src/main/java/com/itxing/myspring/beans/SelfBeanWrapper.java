package com.itxing.myspring.beans;

/**
 * @author xing
 * @create 2020/8/17-gupaoedu-vip-spring
 * spring中对bean的包装，可能是代理对象也可能是原生对象
 */
public class SelfBeanWrapper {
    private Object wrapperInstance;
    private Class<?> wrapperClass;
    public SelfBeanWrapper(Object wrapperInstance){
        this.wrapperInstance = wrapperInstance;
        this.wrapperClass = wrapperInstance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public Class<?> getWrapperClass() {
        return wrapperClass;
    }
}
