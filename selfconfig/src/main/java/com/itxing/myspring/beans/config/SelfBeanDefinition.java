package com.itxing.myspring.beans.config;

/**
 * @author xing
 * @create 2020/8/17-gupaoedu-vip-spring
 * spring中Bean的定义类，用于保存配置信息
 */
public class SelfBeanDefinition {
    private String factoryBeanName;
    private String beanClassName;

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }
}
