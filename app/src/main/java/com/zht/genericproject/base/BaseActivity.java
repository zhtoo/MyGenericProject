package com.zht.genericproject.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.zht.genericproject.R;
import com.zht.genericproject.gesturelock.logic.LockLogic;
import com.zht.genericproject.util.ActivityUtils;
import com.zht.genericproject.util.DensityUtils;
import com.zht.genericproject.util.PermissionCheck;

import java.util.List;

/**
 * Description: Activity基类
 * 这并不是非常完善的基类，有其他activity都有的操作，都可以写在这个类上
 */
public class BaseActivity extends TitleBarActivity {
    static final String TAG = "BaseActivity";
    public float screenWidth;
    public float screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO:在创建的时候就需要将该Activity push 到栈中
        ActivityUtils.push(this);
        //Activity切换中进入动画
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_in_to_left);
        screenWidth = DensityUtils.getWidth(this);
        screenHeight = DensityUtils.getHeight(this) - getStatusBarHeight();
    }

    @Override
    public void onLeftBackward(View backwardView) {

        finish();
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = ActivityUtils.peek().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ActivityUtils.peek().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO:注册锁屏逻辑
        LockLogic.getInstance().checkLock(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtils.remove(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionCheck.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void finish() {
        super.finish();
        //Activity切换中退出动画
        overridePendingTransition(R.anim.slide_out_from_left, R.anim.slide_out_to_right);
    }

    /**
     * 向Fragment 分发onActivityResult
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fm = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            if (fm.getFragments() == null || index < 0
                    || index >= fm.getFragments().size()) {
                Log.w(TAG, "Activity result fragment index out of range: 0x"
                        + Integer.toHexString(requestCode));
                return;
            }
            Fragment frag = fm.getFragments().get(index);
            if (frag == null) {
                Log.w(TAG, "Activity result no fragment exists for index: 0x"
                        + Integer.toHexString(requestCode));
            } else {
                handleResult(frag, requestCode, resultCode, data);
            }
            return;
        }
    }

    /**
     * 递归调用，对所有子Fragement生效
     *
     * @param frag
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleResult(Fragment frag, int requestCode, int resultCode,
                              Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data);
            }
        }
    }
}
