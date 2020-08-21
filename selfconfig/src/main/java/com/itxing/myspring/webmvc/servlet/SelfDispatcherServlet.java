package com.itxing.myspring.webmvc.servlet;

import com.itxing.myspring.annotation.SelfAutowired;
import com.itxing.myspring.annotation.SelfController;
import com.itxing.myspring.annotation.SelfRequestMapping;
import com.itxing.myspring.annotation.SelfService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author xing
 * @create 2020/8/15-gupaoedu-vip-spring
 */
public class SelfDispatcherServlet extends HttpServlet {
    private Map<String,Object> ioc = new HashMap<>();

    private Properties contextConfig = new Properties();

    private List<String> classNames = new ArrayList<>();

    private Map<String, Method> handlerMapping = new HashMap<String,Method>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    //运行阶段
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //6.根据URL完成方法的调度
        try {
            req.setCharacterEncoding("UTF-8");
            resp.setContentType("text/html;charset=utf-8");
            doDispatcherServlet(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("500 Exception Detail"+Arrays.toString(e.getStackTrace()));
        }
    }

    private void doDispatcherServlet(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");

        if(!this.handlerMapping.containsKey(url)){
            resp.getWriter().write("404 Not Find");
            return ;
        }
        Method method = this.handlerMapping.get(url);

        //使用一个数组为了表示传入多个参数
        Map<String,String[]> paramMap = req.getParameterMap();
        String beanName = toLowerFirstCase(method.getDeclaringClass().getSimpleName());
        //第一个参数为method所在的实例，第二个为method的实参
        //invoke指向方法，
//        DemoAction o =(DemoAction) ioc.get(beanName);
//        o.query(req,resp,paramMap.get("name")[0],paramMap.get("id")[0]);
        method.invoke(ioc.get(beanName),new Object[]{req,resp,paramMap.get("name")[0],paramMap.get("id")[0]});

    }

    //初始化阶段
    @Override
    public void init(ServletConfig config) throws ServletException {
        //============IOC==================
        //1.加载配置文件
        doLoadConfig(config.getInitParameter("contentConfigLocation"));
        //2.初始化IOC容器,使用Map作为ioc容器,扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));
        //3.初始化扫描的类，并创建实例保存到ioc
        doInstance();
        //====================DI======
        //4.完成DI
        doAutowired();
        //=================MVC===========
        //5.初始化HandlerMappering
        doInitHandlerMappering();
        //初始化完成
        System.out.println("self spring framework is init...");

    }

    private void doInitHandlerMappering() {
        if(ioc.isEmpty())return;

        for(Map.Entry<String,Object> entry:ioc.entrySet()){

            Class<?> clazz = entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(SelfRequestMapping.class))continue;

            String baseUrl = "";
            if(clazz.isAnnotationPresent(SelfRequestMapping.class)){
                SelfRequestMapping requestMapping = clazz.getAnnotation(SelfRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            //获取类的方法，spring只获取public方法
            for(Method method :clazz.getMethods()){
                if(!method.isAnnotationPresent(SelfRequestMapping.class))continue;

                //获取方法上的request注解
                SelfRequestMapping requestMapping = method.getAnnotation(SelfRequestMapping.class);
                //url需要获取类的配置+方法的配置,配置url时用户可以加/save或者直接save，在执行链中进行正则匹配
                String url = ("/"+baseUrl+"/"+requestMapping.value()).replaceAll("/+","/");
                handlerMapping.put(url,method);
                System.out.println("Mapper"+url+","+method);
            }
        }
    }

    private void doAutowired() {
        if(ioc.isEmpty())return;

        for(Map.Entry<String,Object> entry:ioc.entrySet()){
            //拿到ioc容器所有实例的字段(属性)
            Field[] Fields = entry.getValue().getClass().getDeclaredFields();
            //给有注解的属性依赖输入
            for(Field field:Fields){
                if(!field.isAnnotationPresent(SelfAutowired.class))continue;

                SelfAutowired autowried = field.getAnnotation(SelfAutowired.class);
                String beanName = autowried.value().trim();
                if("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                //使用反射对类中的所有访问权限进行强制设置
                field.setAccessible(true);

                try {
                    //field对应类中的属性
                    field.set(entry.getValue(),ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    continue;
                }
            }

        }
    }

    private void doInstance() {
        if(classNames.isEmpty())return;
        try {
            for(String className:classNames){
                Class<?> clazz = Class.forName(className);
                //加注解才能够实例化
                //分为Controller层和Service层
                if(clazz.isAnnotationPresent(SelfController.class)){
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    Object instance = clazz.newInstance();
                    ioc.put(beanName,instance);
                }else if(clazz.isAnnotationPresent(SelfService.class)){
                    //默认类名首字母小写
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    //自定义BeanName，保证不同包下有相同类创建的实例唯一
                    //如果自定义类名则使用自定义类名替换
                    SelfService service = clazz.getAnnotation(SelfService.class);
                    if(!"".equals(service.value())){
                        beanName = service.value();
                    }

                    Object instance = clazz.newInstance();
                    ioc.put(beanName,instance);
                    //用全类名作为beanName
                    for(Class<?> i:clazz.getInterfaces()){
                        //一个接口被多个类实现
                        if(ioc.containsKey(i.getName()))throw new Exception("The Bean is exist");

                        ioc.put(i.getName(),instance);
                    }

                }else{
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private String toLowerFirstCase(String simpleName){
        //将类名首字母大写转换成小写
        char[] chars = simpleName.toCharArray();
        chars[0]+=32;
        return String.valueOf(chars);
    }
}
