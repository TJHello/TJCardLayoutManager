package com.tj.order.card;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * 作者:TJbaobao
 * 时间:2018/9/14  13:53
 * 说明:
 * 使用：
 */
public class AppLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.Companion.isRunActivity()){
            this.finish();
        }else{
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

}
