package com.itxing.testdemo.action;

import com.itxing.myspring.annotation.SelfAutowired;
import com.itxing.myspring.annotation.SelfController;
import com.itxing.myspring.annotation.SelfRequestMapping;
import com.itxing.myspring.annotation.SelfRequestParam;
import com.itxing.myspring.webmvc.servlet.SelfModelAndView;
import com.itxing.testdemo.service.MyQueryService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xing
 * @create 2020/8/21-gupaoedu-vip-spring
 */
@SelfController
@SelfRequestMapping("/page")
public class PageAction {
    @SelfAutowired
    MyQueryService queryService;
    @SelfRequestMapping("/first.html")
    public SelfModelAndView query(@SelfRequestParam("name") String name){
        String query = queryService.query(name);
        Map<String,Object> model = new HashMap<>();
        model.put("name",name);
        model.put("data",query);
        model.put("token","123456");
        return  new SelfModelAndView("first.html",model);
    }
}
