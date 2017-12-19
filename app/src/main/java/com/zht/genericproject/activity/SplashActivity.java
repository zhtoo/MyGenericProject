package com.zht.genericproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * 作者：zhanghaitao on 2017/12/1 16:22
 * 邮箱：820159571@qq.com
 *
 * @describe:闪屏界面
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(SplashActivity.this, InitializeActivity.class));
        finish();
    }

}
