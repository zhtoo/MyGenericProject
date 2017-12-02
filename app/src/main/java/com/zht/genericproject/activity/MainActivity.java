package com.zht.genericproject.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zht.genericproject.R;
import com.zht.genericproject.util.ActivityUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtils.push(this);
        setContentView(R.layout.activity_main);
    }
}
