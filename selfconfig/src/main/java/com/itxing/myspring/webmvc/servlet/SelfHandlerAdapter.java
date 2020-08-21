package com.itxing.myspring.webmvc.servlet;

import com.itxing.myspring.annotation.SelfRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xing
 * @create 2020/8/21-gupaoedu-vip-spring
 */
public class SelfHandlerAdapter {
    //动态匹配参数
    public SelfModelAndView handle(HttpServletRequest req, HttpServletResponse resp, SelfHandlerMapping handler) throws Exception {
        //形参列表：编译后获取到的值

        //实参列表：运行时拿取的值
        //提取加了注解的参数的名字
        Map<String,Integer> paramIndexMapping = new HashMap<>();
        Annotation[][] pa = handler.getMethod().getParameterAnnotations();
        for(int i=0;i<pa.length;i++){
            for(Annotation a:pa[i]){
                if(a instanceof SelfRequestParam){
                    String paramName = ((SelfRequestParam) a).value();
                    if(!"".equals(paramName))paramIndexMapping.put(paramName,i);
                }
            }
        }
        //提取request和resopnse的位置
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            Class<?> type = paramTypes[i];
            if(type == HttpServletRequest.class||type == HttpServletResponse.class){
                paramIndexMapping.put(type.getName(),i);
            }
        }
        //实参列表，运行时获取到的
        Map<String,String[]> parameterMap = req.getParameterMap();
        Object[] paramValues = new Object[paramTypes.length];
        for(Map.Entry<String,String[]> param:parameterMap.entrySet()){
            String value = Arrays.toString(parameterMap.get(param.getKey()))
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s","");
            if(!paramIndexMapping.containsKey(param.getKey()))continue;

            int index = paramIndexMapping.get(param.getKey());
            paramValues[index] = caseStringValue(value,paramTypes[index]);
        }

        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int index = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }
        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int index = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }
        Object result = handler.getMethod().invoke(handler.getController(), paramValues);
        if(result==null||result instanceof Void)return null;

        boolean isModelAndView = handler.getMethod().getReturnType()==SelfModelAndView.class;
        if(isModelAndView)return (SelfModelAndView) result;
        return null;
    }

    //参数的类型可以是多种类型，对参数进行类型转换
    private Object caseStringValue(String value,Class<?> paramType){
        if(String.class == paramType) return value;
        if(Integer.class == paramType) return Integer.valueOf(value);
        else if(Double.class == paramType) return Double.valueOf(value);
        else{
            if(value!=null)return value;
            return null;
        }

    }
}
