package com.itxing.testdemo.service.impl;

import com.itxing.myspring.annotation.SelfService;
import com.itxing.testdemo.service.MyModifyService;

/**
 * @author xing
 * @create 2020/8/17-gupaoedu-vip-spring
 */

@SelfService
public class MyModifyServiceImpl implements MyModifyService {

    @Override
    public String add(Integer id, String name) {
        return "添加用户，用户id = "+id+", 用户姓名 = "+name;
    }

    @Override
    public String edit(Integer id, String name) {
        return "更新用户，用户的姓名是："+name+",用户的编码是： "+ id;
    }

    @Override
    public String remove(Integer id) {
        return "要删除的用户编码时 "+id;
    }
}
