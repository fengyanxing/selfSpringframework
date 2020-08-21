package com.itxing.myspring.webmvc.servlet;

import java.io.File;

/**
 * @author xing
 * @create 2020/8/21-gupaoedu-vip-spring
 */
public class SelfViewResolver {
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    private File templateRootDir;


    public SelfViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    public SelfView resolverViewName(String viewName) {
        if(null == viewName||"".equals(viewName.trim()))return null;
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)?viewName:(viewName+DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath()+"/"+viewName).replaceAll("/+","/"));
        return new SelfView(templateFile);
    }
}
