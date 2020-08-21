package com.itxing.myspring.context;

import com.itxing.myspring.annotation.SelfAutowired;
import com.itxing.myspring.annotation.SelfController;
import com.itxing.myspring.annotation.SelfService;
import com.itxing.myspring.beans.SelfBeanWrapper;
import com.itxing.myspring.beans.config.SelfBeanDefinition;
import com.itxing.myspring.beans.support.SelfBeanDefinitionReader;
import org.omg.PortableInterceptor.INACTIVE;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author xing
 * @create 2020/8/17-gupaoedu-vip-spring
 */
public class SelfApplicationContext {

    private String[] configLocations;

    private SelfBeanDefinitionReader reader;

    private final Map<String,SelfBeanDefinition> beanDefinitionMap = new HashMap<String,SelfBeanDefinition>();

    //ioc容器
    private Map<String,SelfBeanWrapper>  factoryBeanInstanceCache = new HashMap<String,SelfBeanWrapper>();
    //单例对象时使用的ioc容器
    private Map<String,Object>  factoryBeanObjectCache = new HashMap<String,Object>();


    //...表示一个数组，可以用于传入多个参数的文件
    public SelfApplicationContext(String... configLocations) {
        try {
            this.configLocations = configLocations;
            //获取配置文件对象后，读取配置文件
            reader = new SelfBeanDefinitionReader(configLocations);

            //解析配置文件
            List<SelfBeanDefinition> beanDefinitions = reader.loaderBeanDefinitions();

            //将beanDefinition对应的实例注册到ioc容器(beanDefinitionMap)中,key为beanName value为beanDefinition对象
            doRegistBeanDefinition(beanDefinitions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //配置信息初始化

        //完成依赖注入，依赖注入是在getBean才将真正的bean 注入
        doAutowrited();

    }

    private void doAutowrited() {
        //调用getbean
        for(Map.Entry<String,SelfBeanDefinition> beanDefinitionEntry:this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            getBean(beanName);
        }
    }

    private void doRegistBeanDefinition(List<SelfBeanDefinition> beanDefinitions) throws Exception {
        for(SelfBeanDefinition beanDefinition:beanDefinitions){
            if(this.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The"+beanDefinition.getFactoryBeanName()+"is Exist!");
            }
            this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
            this.beanDefinitionMap.put(beanDefinition.getBeanClassName(),beanDefinition);
        }
    }

    public Object getBean(Class clazz){
        return getBean(clazz.getName());
    }

    public Object getBean(String beanName){
        //1.获取BeanDefinition配置
        SelfBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        //2.反射实例化
        Object instance = instanceBean(beanName,beanDefinition);

        //3.将创建出来的实例包装成BeanWrapper对象
        SelfBeanWrapper beanWrapper = new SelfBeanWrapper(instance);

        //4.将beanWapper对象存入真正的Ioc容器中
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);

        //5.执行依赖注入
        populateBean(beanName,beanDefinition,beanWrapper);
        return this.factoryBeanInstanceCache.get(beanName).getWrapperInstance();
    }

    //依赖注入
    private void populateBean(String beanName, SelfBeanDefinition beanDefinition, SelfBeanWrapper beanWrapper) {
        Object wrapperInstance = beanWrapper.getWrapperInstance();
        Class<?> wrapperClass = beanWrapper.getWrapperClass();
        //进行判断，只有接入注解的才进行相关的注入
        if(!(wrapperClass.isAnnotationPresent(SelfController.class)||wrapperClass.isAnnotationPresent(SelfService.class))) return;

        //拿到IOC容器所有实例的字段(类的属性)
        Field[] declaredFields = wrapperClass.getDeclaredFields();
        for(Field field:declaredFields){
            //表示属性上没有标注该注解
            if(!field.isAnnotationPresent(SelfAutowired.class))continue;

            //获取注解自定义的bean名称，如果没有设置，默认使用类型名
            SelfAutowired autowried = field.getAnnotation(SelfAutowired.class);
            String autowriedBeanName = autowried.value().trim();
            if("".equals(autowriedBeanName)){
                autowriedBeanName = field.getType().getName();
            }

            //设置访问权限，暴力访问
            field.setAccessible(true);
            try{
                //给字段进行注入
                if(this.factoryBeanInstanceCache.get(autowriedBeanName)==null)continue;

                field.set(wrapperInstance,this.factoryBeanInstanceCache.get(autowriedBeanName).getWrapperInstance());
            }catch (IllegalAccessException ex){
                ex.printStackTrace();
                continue;
            }

        }
    }
    //实例化对象
    private Object instanceBean(String beanName, SelfBeanDefinition beanDefinition) {
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            Class<?> clazz = Class.forName(className);
            instance = clazz.newInstance();
            //spring中的容器很多，如果用户设置单例的bean
            factoryBeanObjectCache.put(className,instance);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return instance;
    }

    public int getBeanDefinitionCount() {
        return beanDefinitionMap.size();
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }
}
