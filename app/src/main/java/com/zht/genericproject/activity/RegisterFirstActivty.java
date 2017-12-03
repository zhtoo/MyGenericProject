package com.zht.genericproject.activity;

import android.os.Bundle;

import com.zht.genericproject.R;
import com.zht.genericproject.base.BaseActivity;

/**
 * 作者：zhanghaitao on 2017/12/1 14:19
 * 邮箱：820159571@qq.com
 *
 * @describe:手机号输出，获取校验码，联网检验校验码--->跳转到RegisterSecondActivty
 */

public class RegisterFirstActivty extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egisterr_first);

        /**
         Intent intent = new Intent();
         intent.putExtra(BundleKeys.PHONE, phone);
         intent.putExtra(BundleKeys.CODE, code);
         ActivityUtils.push(RegisterSecondAct.class, intent);
         ActivityUtils.peek().finish();
         */
    }
}
