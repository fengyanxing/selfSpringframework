package com.itxing.myspring.aop.config;

import lombok.Data;

/**
 * @author xing
 * @create 2020/8/22-selfSpringframework
 */

@Data
public class SelfAopConfig {
    private String pointCut;
    private String aspectClass;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;


}
