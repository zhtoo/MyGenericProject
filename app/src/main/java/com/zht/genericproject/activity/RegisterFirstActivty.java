package com.zht.genericproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zht.genericproject.R;
import com.zht.genericproject.base.BaseActivity;
import com.zht.genericproject.util.ActivityUtils;
import com.zht.genericproject.util.ToastUtil;

/**
 * 作者：zhanghaitao on 2017/12/1 14:19
 * 邮箱：820159571@qq.com
 *
 * @describe:手机号输出，获取校验码，联网检验校验码--->跳转到RegisterSecondActivty
 */

public class RegisterFirstActivty extends BaseActivity implements View.OnClickListener {

    private Button mGetVerificationCode;
    private Button mNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egisterr_first);
        initView();

    }

    /**
     * 初始化View
     */
    private void initView() {

        mGetVerificationCode = (Button) findViewById(R.id.register_get_verification_code);
        mNext = (Button) findViewById(R.id.register_next);

        mGetVerificationCode.setOnClickListener(this);
        mNext.setOnClickListener(this);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_get_verification_code:
                ToastUtil.showToast("注册码");
                break;
            case R.id.register_next:
                Intent intent = new Intent();
                /*intent.putExtra(BundleKeys.PHONE, phone);
                intent.putExtra(BundleKeys.CODE, code);*/
                ActivityUtils.push(RegisterSecondActivty.class, intent);
                ActivityUtils.peek().finish();
                break;
        }
    }
}
