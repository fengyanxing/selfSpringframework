package com.itxing.myspring.aop.support;

import com.itxing.myspring.aop.aspect.SelfAdvice;
import com.itxing.myspring.aop.config.SelfAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xing
 * @create 2020/8/22-selfSpringframework
 */
public class SelfAdviceSupport {

    private SelfAopConfig config;
    private Class targetClass;
    private Object target;
    private Pattern pointCutClassPattern;
    private Map<Method, Map<String, SelfAdvice>> methodCache;

    public SelfAdviceSupport(SelfAopConfig selfAopConfig) {
        this.config = selfAopConfig;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public Object getTarget() {
        return target;
    }

    private void parse() {
        //正则表达式，对于配置文件中的特殊字符进行转义
        String pointCut = config.getPointCut().replaceAll("\\.","\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\(")
                .replaceAll("\\)","\\)");

        //public .* com.itxing.testdemo.service..*Service..*(.*)
        //public .* com.itxing.testdemo.service..*Service..*(.*);
        //class com.itxing.testdemo.service..*Service..*
        String pointCutForClassRegx = pointCut.substring(0,pointCut.lastIndexOf("("));
        //提取class全名
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegx.substring(pointCutForClassRegx.lastIndexOf(" ")+1));
        try {
            //开始映射方法与通知的关系
            methodCache = new HashMap<Method,Map<String,SelfAdvice>>();
            Pattern pointCutPattern = Pattern.compile(pointCut);
            //先把织入的切面的方法缓存起来
            Class aspectClass = Class.forName(this.config.getAspectClass());

            Map<String, Method> aspectMethods = new HashMap<>();
            for(Method method:aspectClass.getMethods()){
                aspectMethods.put(method.getName(),method);
            }

            //扫描具体类的所有的方法
            for(Method method:this.targetClass.getMethods()){
                //包括了修饰符、返回值、方法名、形参列表
                String methodString = method.toString();
                //获取到其中的异常进行处理
                if(methodString.contains("throws")){
                    methodString = methodString.substring(0,methodString.lastIndexOf("throws")).trim();
                }

                Matcher matcher = pointCutPattern.matcher(methodString);
                if(matcher.matches()){
                   Map<String, SelfAdvice> advice = new HashMap<>();

                    //前置通知处理
                    if(!(null==config.getAspectBefore()||"".equals(config.getAspectBefore()))){
                        advice.put("before",new SelfAdvice(aspectClass.newInstance(),aspectMethods.get(config.getAspectBefore())));
                    }

                    //后置通知处理
                    if(!(null==config.getAspectAfter()||"".equals(config.getAspectAfter()))){
                        advice.put("after",new SelfAdvice(aspectClass.newInstance(),aspectMethods.get(config.getAspectAfter())));
                    }

                    //异常通知通知处理
                    if(!(null==config.getAspectAfterThrow()||"".equals(config.getAspectAfterThrow()))){
                        advice.put("afterThrowing",new SelfAdvice(aspectClass.newInstance(),aspectMethods.get(config.getAspectAfterThrow())));
                    }

                    methodCache.put(method,advice);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

    public Map<String, SelfAdvice> getAdvices(Method method, Class targetClass) throws Exception {
        Map<String, SelfAdvice> cached = methodCache.get(method);
        //边界处理，拿到的是代理之后的方法
        if(null==cached){
            Method method1 = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(method1);
            this.methodCache.put(method1,cached);
        }
        return cached;
    }
}
