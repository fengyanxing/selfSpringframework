package com.itxing.testdemo.service.impl;

import com.itxing.myspring.annotation.SelfService;
import com.itxing.testdemo.service.IDemoService;

/**
 * @author xing
 * @create 2020/8/16-gupaoedu-vip-spring
 */

@SelfService
public class IDemoServiceImpl implements IDemoService {

    @Override
    public String get(String name) {
        return "My name is "+name ;
    }
}
