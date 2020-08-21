package com.itxing.testdemo.controller;

import com.itxing.myspring.annotation.SelfAutowired;
import com.itxing.myspring.annotation.SelfController;
import com.itxing.testdemo.service.IDemoService;

/**
 * @author xing
 * @create 2020/8/16-gupaoedu-vip-spring
 */
@SelfController
public class DemoController {
    @SelfAutowired
    private IDemoService demoService;
}
