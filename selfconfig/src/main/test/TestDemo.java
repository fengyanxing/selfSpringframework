import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xing
 * @create 2020/8/22-selfSpringframework
 */
public class TestDemo {
    public static void main(String[] args) {

        String str = "public .* com.itxing.testdemo.service..*Service..*(.*)";
        String pointCut = str.replaceAll("\\.","\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\(")
                .replaceAll("\\)","\\)");
        String pointCutForClassRegx = pointCut.substring(0,pointCut.lastIndexOf("("));

        System.out.println(pointCutForClassRegx);

        pointCutForClassRegx = "class " + pointCutForClassRegx.substring(str.lastIndexOf(" ")+1);
        System.out.println(pointCutForClassRegx);

        //class com\.itxing\.testdemo\.service\..*Service
        //class com.itxing.testdemo.service.impl.MyQueryServiceImpl
        Pattern compile1 = Pattern.compile(pointCutForClassRegx);
        Matcher matcher1 = compile1.matcher("class com.itxing.testdemo.service.impl.MyQueryServiceImpl");
        System.out.println(matcher1.matches());
        //public .* com\.itxing\.testdemo\.service\..*Service\..*\(.*\)
        Pattern compile = Pattern.compile(str);
        Matcher matcher = compile.matcher("public java.lang.String com.itxing.testdemo.service.impl.MyModifyServiceImpl.remove(java.lang.Integer)");
        System.out.println(matcher.matches());
    }
}
