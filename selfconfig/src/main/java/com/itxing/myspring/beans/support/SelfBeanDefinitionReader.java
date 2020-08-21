package com.itxing.myspring.beans.support;

import com.itxing.myspring.beans.config.SelfBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author xing
 * @create 2020/8/17-gupaoedu-vip-spring
 * spring中的工具类，用于加载配置文件
 */
public class SelfBeanDefinitionReader {
    private Properties contextConfig = new Properties();

    //spring中叫做registerBeanClasses
    private List<String> classNames = new ArrayList<>();

    public SelfBeanDefinitionReader(String[] configLocations) {
        //1.读取配置文件
        doLoadConfig(configLocations[0]);
        //2.解析，扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
    }

    public List<SelfBeanDefinition> loaderBeanDefinitions() {
        List<SelfBeanDefinition> result = new ArrayList<SelfBeanDefinition>();
        try{
            for (String className :classNames){
                Class<?> clazz = Class.forName(className);
                if(clazz.isInterface())continue;

               String beanName = toLowerFirstCase(clazz.getSimpleName());
                //clazz.getSimpleName()获取类名
                //clazz.getName()获取全类名
                result.add(doCreateBeanDefinition(beanName,clazz.getName()));
                //扫描包中的实现类，实现类的接口
                for(Class<?> i:clazz.getInterfaces()){
                    result.add(doCreateBeanDefinition(i.getName(),clazz.getName()));
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return result;
    }

    //创建一个BeanDefinition对象，该对象能够创建其他对象
    private SelfBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        SelfBeanDefinition beanDefinition = new SelfBeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }


    private void doLoadConfig(String contentConfigLocation) {
        //获取配置文件后，从classPath路径下找到spring配置文件
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(contentConfigLocation.replaceAll("classpath:",""));

        try {
            contextConfig.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null!=inputStream)
                try{
                    inputStream.close();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
        }
    }

    private void doScanner(String scanPackage) {
        //传入的参数是一个ClassPath下的一个.class文件，是一个包路径，即文件夹的形式，将com.itxing.xx文件夹转换成com/itxing/xx
        String temp = scanPackage.replaceAll("\\.","/");
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url = classLoader.getResource("/"+temp);
        //URL url =  this.getClass().getClass().getClassLoader().getResource(temp);
        File classPath = new File(url.getFile());
        //获取到文件夹后，循环扫描文件夹下的文件
        for(File file:classPath.listFiles()){
            if(file.isDirectory()){
                doScanner(scanPackage+"."+file.getName());
            }else{
                //类路径下的.xml、.properties、.yml等文件进行一个过滤，支架在.class文件
                if(!file.getName().endsWith(".class"))continue;
                //使用全类名作为hashMap的主键
                String className = scanPackage+"."+file.getName().replace(".class","");
                //获取到Class.forName(className)拿到Class对象，从而可以通过反射创建实例
                classNames.add(className);
            }
        }
    }

    private String toLowerFirstCase(String simpleName){
        //将类名首字母大写转换成小写
        char[] chars = simpleName.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }

    public Properties getConfig() {
        return contextConfig;
    }
}
