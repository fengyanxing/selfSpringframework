package com.itxing.myspring.webmvc.servlet;

import com.itxing.myspring.annotation.SelfRequestMapping;
import com.itxing.myspring.context.SelfApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xing
 * @create 2020/8/15-gupaoedu-vip-spring
 */
public class SelfDispatcherServlet3 extends HttpServlet {
    //spring源码中使用一个List存储，由于value中封装了Key，因此没有必要将key单独取出再进行存储
    //private Map<String, SelfHandlerMapping> handlerMapping = new HashMap<String,SelfHandlerMapping>();
    private List<SelfHandlerMapping> handlerMappings = new ArrayList<>();
    private Map<SelfHandlerMapping,SelfHandlerAdapter> handlerAdapters = new HashMap<>();
    private List<SelfViewResolver> viewResolvers = new ArrayList<>();


    SelfApplicationContext context ;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    //运行阶段
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //6.根据URL完成方法的调度
        try {
            //req.setCharacterEncoding("UTF-8");
           resp.setContentType("text/html;charset=utf-8");
            doDispatcherServlet(req,resp);
        } catch (Exception e) {
            e.printStackTrace();
            //resp.getWriter().write("500 Exception Detail"+Arrays.toString(e.getStackTrace()));

            Map<String,Object> model = new HashMap<>();
            model.put("detail","505 Exception Detail");
            model.put("stackTrace",Arrays.toString(e.getStackTrace()));
            try {
                processDispatcherResult(req,resp,new SelfModelAndView("500",model));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private void doDispatcherServlet(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        //1.根据URL拿到对应的HandlerMapping对象
        SelfHandlerMapping handler = getHandler(req);

        if(null == handler){
            processDispatcherResult(req,resp,new SelfModelAndView("404"));
            return;
        }
        //2.根据handlerMapping获取handlerenderrAdapter
        SelfHandlerAdapter handlerAdapter = getHandlerAdapter(handler);

        //3.根据adapter拿到一个ModeAndView
        SelfModelAndView modelAndView = handlerAdapter.handle(req,resp,handler);

        //4.根据ViewResolver，根据ModelandView获取View
        processDispatcherResult(req,resp,modelAndView);
        /*
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");

        if(!this.handlerMappings.){
            resp.getWriter().write("404 Not Find");
            return ;
        }
        Method method = this.handlerMappings.get(url);

        //使用一个数组为了表示传入多个参数
        Map<String,String[]> paramMap = req.getParameterMap();
        //拿到方法的形参列表
        Class<?>[] parameterTypes = method.getParameterTypes();
        //声明实参列表
        Object[] parameterValues = new Object[parameterTypes.length];

        //根据形参配置，给实参列表赋值
        for(int i=0;i<parameterTypes.length;i++){
            Class parameterType = parameterTypes[i];
            if(parameterType==HttpServletRequest.class){
                parameterValues[i] = req;
                continue;
            }else if(parameterType==HttpServletResponse.class){
                parameterValues[i] = resp;
                continue;
            }else if(parameterType == String.class){
                Annotation[][] pa = method.getParameterAnnotations();
                for(int j=0;j<pa.length;j++){
                    for(Annotation a : pa[i]){
                        if(a instanceof SelfRequestParam){
                            String paramName = ((SelfRequestParam) a).value();
                            if(!"".equals(paramName.trim())){
                                String value = Arrays.toString(paramMap.get(paramName))
                                        .replaceAll("\\[|\\]","")
                                        .replaceAll("\\s","");
                                parameterValues[i] = value;
                            }
                        }
                    }
                }

            }

        }
        //第一个参数为method所在的实例，第二个为method的实参
        //invoke指向方法，
//        DemoAction o =(DemoAction) ioc.get(beanName);
//        o.query(req,resp,paramMap.get("name")[0],paramMap.get("id")[0]);

        System.out.println(method.getDeclaringClass());

        method.invoke(context.getBean(method.getDeclaringClass()),parameterValues);
*/
    }

    //404、500、自定义错误页面
    private void processDispatcherResult(HttpServletRequest req, HttpServletResponse resp, SelfModelAndView modelAndView) throws Exception {
        if(null==modelAndView)return;
        if(this.viewResolvers.isEmpty())return;
        for(SelfViewResolver viewResolver : this.viewResolvers){
            SelfView view = viewResolver.resolverViewName(modelAndView.getViewName());
            view.render(modelAndView.getModel(),req,resp);
            return;
        }
    }

    private SelfHandlerAdapter getHandlerAdapter(SelfHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty())return null;
        return this.handlerAdapters.get(handler);
    }

    private SelfHandlerMapping getHandler(HttpServletRequest req) {
        //req.getRequestURI() 获取到req的相对路径
        //req.getRequestURL() 获取req的绝对路径
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");
        for(SelfHandlerMapping handlerMapping:this.handlerMappings){
           Matcher matcher =  handlerMapping.getPattern().matcher(url);
           if(!matcher.matches()) continue;
           return handlerMapping;
        }
        return null;
    }

    //初始化阶段
    @Override
    public void init(ServletConfig config) throws ServletException {

        context = new SelfApplicationContext(config.getInitParameter("contentConfigLocation"));
        //=================MVC  九大组件===========
        //5.初始化HandlerMappering
        initStrategies(context);
        //doInitHandlerMappering();
        //初始化完成
        System.out.println("self spring framework is init...");

    }

    //MVC采用的是一个策略模式，对九大组件进行初始化
    private void initStrategies(SelfApplicationContext context) {
        //多文件上传组件
        //初始化本地语言环境
        //初始化模板处理器
        //handleMapping
        initHandlerMapping(context);
        //初始化参数适配器
        initHandlerAdapter(context);
        //初始化异常拦截器
        initViewResolvers(context);
        //初始化视图预处理
        //初始化视图转换器
        //FlashMap管理器
    }

    private void initViewResolvers(SelfApplicationContext context) {
        //模板引擎的根路径
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for(File file:templateRootDir.listFiles()){
            this.viewResolvers.add(new SelfViewResolver(templateRoot));
        }
    }

    //handlerAdapter与HandlerMapper是一个一对一的关系
    private void initHandlerAdapter(SelfApplicationContext context) {
        for(SelfHandlerMapping handlerMapping:handlerMappings){
            this.handlerAdapters.put(handlerMapping,new SelfHandlerAdapter());
        }
    }

    private void initHandlerMapping(SelfApplicationContext context) {
        if(context.getBeanDefinitionCount()==0) return;

        String[]  beanNames = context.getBeanDefinitionNames();

        for(String beanName:beanNames){

            Object instance = context.getBean(beanName);
            Class<?> clazz = instance.getClass();
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
                //replaceAll("\\*",".*") 将字符串中的*替换成.*，表示任意字符可以出现任意次数
                //replaceAll("/+","/")将字符串中的大于1次出现的/替换成一个/，表示路径
                String regx = ("/"+baseUrl+"/"+requestMapping.value().replaceAll("\\*",".*")).replaceAll("/+","/");
                Pattern pattern = Pattern.compile(regx);
                handlerMappings.add(new SelfHandlerMapping(pattern,instance,method));
                System.out.println("Mapper"+regx+","+method);
            }
        }
    }
}
