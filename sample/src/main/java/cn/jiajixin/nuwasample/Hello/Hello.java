package cn.jiajixin.nuwasample.Hello;

/**
 * Created by jixin.jia on 15/11/5.
 */
public class Hello {
    private String word;

    public Hello() {
        word = "old word before fix";
    }

    public String say() {
        return "say:"+word;
    }

    public static String testStatic(){
        return "old static";
    }
}
