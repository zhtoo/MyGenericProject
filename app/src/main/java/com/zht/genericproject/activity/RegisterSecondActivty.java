package com.zht.genericproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zht.genericproject.R;
import com.zht.genericproject.base.BaseActivity;
import com.zht.genericproject.util.ActivityUtils;

import butterknife.ButterKnife;

/**
 * 作者：zhanghaitao on 2017/12/1 14:20
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class RegisterSecondActivty extends BaseActivity implements View.OnClickListener {


    private EditText mName;
    private EditText mPassword;
    private EditText mPasswordAffirm;
    private TextView mPasswordConflicting;
    private Button mSure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egisterr_second);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        mName = (EditText) findViewById(R.id.register_name);
        mPassword = (EditText) findViewById(R.id.register_password);
        mPasswordAffirm = (EditText) findViewById(R.id.register_password_affirm);
        mPasswordConflicting = (TextView) findViewById(R.id.register_password_conflicting);
        mSure = (Button) findViewById(R.id.register_sure);

        mSure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        // TODO: 2017/12/18 在这里跳转到手势设置界面（是否有必要？）
        Intent intent = new Intent();
        ActivityUtils.push(LoginActivity.class, intent);
        ActivityUtils.peek().finish();
    }
}
