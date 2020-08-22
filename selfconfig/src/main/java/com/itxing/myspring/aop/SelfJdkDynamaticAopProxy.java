package com.itxing.myspring.aop;

import com.itxing.myspring.aop.aspect.SelfAdvice;
import com.itxing.myspring.aop.support.SelfAdviceSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author xing
 * @create 2020/8/22-selfSpringframework
 * 动态代理字节码重组
 * 1.JDK
 * 2.cglib
 */
public class SelfJdkDynamaticAopProxy implements InvocationHandler {
    private SelfAdviceSupport config;
    public SelfJdkDynamaticAopProxy(SelfAdviceSupport config) {
        this.config = config;
    }

    public Object getProxy() {
        //jdk代理使用Proxy创建代理对象，传入的参数有三个
        //1.生成新的类用什么方式加载
        //2.生成新的类要实现什么接口
        //3.通过反射触发调用invoke
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),this.config.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Map<String, SelfAdvice> advices = this.config.getAdvices(method,this.config.getTargetClass());
        Object returnValue;

        //织入前置通知
        invokeAdvice(advices.get("before"));
        //被代理对象的业务方法
        try{
            returnValue = method.invoke(this.config.getTarget(),args);
        }catch(Exception ex){
            invokeAdvice(advices.get("afterThrowing"));
            ex.printStackTrace();
            throw  ex;
        }

        //织入后置通知
        invokeAdvice(advices.get("after"));
        return returnValue;
    }

    private void invokeAdvice(SelfAdvice advice) {
        try {
            advice.getAdviceMethod().invoke(advice.getAspect());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }
}
