package com.itxing.myspring.webmvc.servlet;

import java.util.Map;

/**
 * @author xing
 * @create 2020/8/21-gupaoedu-vip-spring
 */
public class SelfModelAndView {
    private String viewName;
    private Map<String,?> model;
    public SelfModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public SelfModelAndView(String viewName, Map<String, Object> model) {
        this.viewName =viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String,?> getModel() {
        return model;
    }
}
