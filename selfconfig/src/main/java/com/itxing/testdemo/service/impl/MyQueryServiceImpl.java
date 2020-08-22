package com.itxing.testdemo.service.impl;

import com.itxing.myspring.annotation.SelfService;
import com.itxing.testdemo.service.MyQueryService;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author xing
 * @create 2020/8/17-gupaoedu-vip-spring
 */
@Slf4j
@SelfService
public class MyQueryServiceImpl implements MyQueryService {
    @Override
    public String query(String name) {
        return "查询的用户是"+name+"，time:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }

    @Override
    public String query2(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\""+name+"\",time:\""+time+"\"}";
        log.info("这是在业务方法中打印的："+json);
        return json;
    }
}
