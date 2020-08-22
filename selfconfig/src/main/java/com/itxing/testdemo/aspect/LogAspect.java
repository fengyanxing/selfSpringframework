package com.itxing.testdemo.aspect;


import lombok.extern.slf4j.Slf4j;

/**
 * @author xing
 * @create 2020/8/22-selfSpringframework
 */
@Slf4j
public class LogAspect {
    public void before(){
        log.info("Invoking Before Method!!!");
    }
    public void after(){
        log.info("Invoking after Method!!!");
    }
    public void afterThrowing(){
        log.info("出现异常");
    }
}
