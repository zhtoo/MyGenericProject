package com.zht.genericproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zht.genericproject.R;
import com.zht.genericproject.base.BaseActivity;
import com.zht.genericproject.gesturelock.activity.LockStepActivty;
import com.zht.genericproject.util.ActivityUtils;
import com.zht.genericproject.util.ToastUtil;

/**
 * 作者：zhanghaitao on 2017/12/1 13:07
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

   private EditText mName;
   private EditText mPassword;
   private Button   mBtn;
   private TextView mFrogetPassword;
   private TextView mRegisterAccount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();

    }

    private void initView() {
     mName =(EditText)  findViewById(R.id.login_name);
     mPassword =(EditText)  findViewById(R.id.login_password);
     mBtn =(Button)  findViewById(R.id.login_btn);
     mFrogetPassword =(TextView)  findViewById(R.id.login_froget_password);
     mRegisterAccount =(TextView)  findViewById(R.id.login_register_account);

    }

    private void initListener() {
        mBtn.setOnClickListener(this);
        mFrogetPassword.setOnClickListener(this);
        mRegisterAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.login_btn:
                checkLogin();
                break;
            case R.id.login_froget_password:
                startActivity(new Intent(this, LockStepActivty.class));
                break;
            case R.id.login_register_account:
                startActivity(new Intent(this, RegisterFirstActivty.class));
                break;
        }
    }

    private void checkLogin() {

        String name = mName.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            ToastUtil.showToast("账户不能为空！！！");
        }else if(TextUtils.isEmpty(password)) {
            ToastUtil.showToast("密码不能为空！！！");
        }else {
            //TODO:联网进行账号密码判断
            //TODO:写入登录信息到内存和SP中
            //写入登录信息到内存和SP中
            /** 登录逻辑处理 */
            Activity activity = ActivityUtils.peek();
            Intent intent = new Intent();
            intent.setClass(activity, MainActivity.class);
            ActivityUtils.push(MainActivity.class, intent);
            ActivityUtils.pop(activity);
        }



    }



}
