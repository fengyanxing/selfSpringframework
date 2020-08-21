package com.itxing.testdemo.action;

import com.itxing.myspring.annotation.SelfAutowired;
import com.itxing.myspring.annotation.SelfController;
import com.itxing.myspring.annotation.SelfRequestMapping;
import com.itxing.myspring.annotation.SelfRequestParam;
import com.itxing.testdemo.service.IDemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xing
 * @create 2020/8/16-gupaoedu-vip-spring
 */
@SelfController
@SelfRequestMapping("/demo")
public class DemoAction {
    @SelfAutowired
    private IDemoService demoService;
    private IDemoService demoService2;


    @SelfRequestMapping("/query")
    public  void query(HttpServletRequest request, HttpServletResponse response,@SelfRequestParam("name") String name,@SelfRequestParam("id") String id){
        String result = "我的姓名是"+name+"，我的编码是"+id;
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SelfRequestMapping("/add")
    public  void add(HttpServletRequest request, HttpServletResponse response,@SelfRequestParam("a") Integer a,@SelfRequestParam("b") Integer b){
        try {
            response.getWriter().write(a+" + "+b+" = "+(a+b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SelfRequestMapping("/remove")
    public  void remove(HttpServletRequest request, HttpServletResponse response,@SelfRequestParam("id") String id){
        String str = "移除id为"+id;
        try {
            response.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
