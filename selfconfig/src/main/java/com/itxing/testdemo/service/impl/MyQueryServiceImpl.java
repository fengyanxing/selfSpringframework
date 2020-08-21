package com.itxing.testdemo.service.impl;

import com.itxing.myspring.annotation.SelfService;
import com.itxing.testdemo.service.MyQueryService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author xing
 * @create 2020/8/17-gupaoedu-vip-spring
 */
@SelfService
public class MyQueryServiceImpl implements MyQueryService {
    @Override
    public String query(String name) {
        return "查询的用户是"+name+"，time:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}
