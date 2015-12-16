package cn.jiajixin.nuwasample.Hello;

/**
 * Created by jixin.jia on 15/11/5.
 */
public class Hello {
    private String word;

    public Hello() {
        word = "new word after fix";
    }

    public String say() {
        return "fix say:"+word;
    }

    public static String testStatic(){
        return "new static";
    }
}
