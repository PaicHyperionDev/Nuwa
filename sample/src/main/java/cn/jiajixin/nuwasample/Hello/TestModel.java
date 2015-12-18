package cn.jiajixin.nuwasample.Hello;

/**
 * com.euler.test
 *
 * @author:xiehonglin429
 * @date 15/12/16 18:47
 */
public class TestModel {
    public String filedStr = "newField";
    public static String staticFiledStr = "newStaticFiled";

    private String word;

    public TestModel() {
        word = "new after fix";
    }

    public String say() {
        return "new say->" + word;
    }


    public static String testStatic() {
        return "new static";
    }
}


