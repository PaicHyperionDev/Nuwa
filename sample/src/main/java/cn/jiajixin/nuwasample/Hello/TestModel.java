package cn.jiajixin.nuwasample.Hello;

/**
 * com.euler.test
 *
 * @author:xiehonglin429
 * @date 15/12/16 18:47
 */
public class TestModel {
    public String filedStr = "oldField";
    public static String staticFiledStr = "oldStaticFiled";

    private String word;

    public TestModel() {
        word = "old before fix";
    }

    public String say() {
        return "old say->" + word;
    }


    public static String testStatic() {
        return "old static";
    }
}


