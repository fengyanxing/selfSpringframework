package com.itxing.testdemo.action;

import com.itxing.myspring.annotation.SelfAutowired;
import com.itxing.myspring.annotation.SelfController;
import com.itxing.myspring.annotation.SelfRequestMapping;
import com.itxing.myspring.annotation.SelfRequestParam;
import com.itxing.testdemo.service.MyModifyService;
import com.itxing.testdemo.service.MyQueryService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xing
 * @create 2020/8/17-gupaoedu-vip-spring
 */

@SelfController
@SelfRequestMapping("/web")
public class MyAction {
    @SelfAutowired
    private MyQueryService queryService;
    @SelfAutowired
    private MyModifyService modifyService;

    @SelfRequestMapping("/query.json")
    public  void query(HttpServletRequest request, HttpServletResponse response, @SelfRequestParam("name") String name){
       String result = queryService.query(name);
       out(response,result);
    }

    @SelfRequestMapping("/remove.json")
    public  void remove(HttpServletRequest request, HttpServletResponse response, @SelfRequestParam("id") Integer id){
        String result = modifyService.remove(id);
        out(response,result);
    }

    @SelfRequestMapping("/edit.json")
    public  void edit(HttpServletRequest request, HttpServletResponse response, @SelfRequestParam("id") Integer id, @SelfRequestParam("name") String name){
        String result = modifyService.edit(id,name);
        out(response,result);
    }

    private void out(HttpServletResponse response, String result) {
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SelfRequestMapping("/add*.json")
    public  void add(HttpServletRequest request, HttpServletResponse response,@SelfRequestParam("id") Integer id,@SelfRequestParam("name") String name){
        String result = modifyService.add(id, name);
        out(response,result);
    }
}
