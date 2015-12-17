package cn.jiajixin.nuwasample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import cn.jiajixin.nuwasample.Hello.TestModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView tv = (TextView) findViewById(R.id.textview);
        TestModel model = new TestModel();
        String str = String.format("TestSay:%s\nStaticMethod:%s\nField:%s\nStaticField:%s",
                model.say(),TestModel.testStatic(),
                model.filedStr,TestModel.staticFiledStr);
        tv.setText(str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
