package com.itxing.myspring.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xing
 * @create 2020/8/21-gupaoedu-vip-spring
 */
public class SelfView {
    private  File viewFile;
    public SelfView(File templateFile) {
        this.viewFile = templateFile;
    }

    public void render(Map<String,?> model, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        StringBuffer sb = new StringBuffer();
        RandomAccessFile randomAccess = new RandomAccessFile(this.viewFile, "r");
        String line = null;
        while(null!=(line=randomAccess.readLine())){
            line = new String(line.getBytes("ISO-8859-1"),"utf-8");
            //字符串￥\{[^\}]+\}表示从￥开始一直到}结束
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}",Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while(matcher.find()){
                String paramName = matcher.group();
                //将文件中的${name}中的特殊字符进行替换获取到name值
                paramName = paramName.replaceAll("￥\\{|\\}","");
                Object paramValue = model.get(paramName);
                if(null==paramValue)continue;
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                matcher=pattern.matcher(line);
            }
            sb.append(line);
        }
        resp.setContentType("utf-8");
        resp.getWriter().write(sb.toString());
    }

    //特殊字符处理
    public static String makeStringForRegExp(String str){
        return str.replace("\\","\\\\").replace("*","\\*")
                .replace("+","\\+").replace("|","\\|")
                .replace("{","\\{").replace("}","\\}")
                .replace("(","\\(").replace(")","\\)")
                .replace("^","\\^").replace("$","\\$")
                .replace("[","\\[").replace("]","\\]")
                .replace("?","\\?").replace(",","\\,")
                .replace(".","\\.").replace("&","\\&");
    }
}
