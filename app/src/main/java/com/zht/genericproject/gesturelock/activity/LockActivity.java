package com.zht.genericproject.gesturelock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.zht.genericproject.R;
import com.zht.genericproject.base.BaseActivity;
import com.zht.genericproject.gesturelock.logic.LockLogic;
import com.zht.genericproject.gesturelock.view.LockPatternView;
import com.zht.genericproject.util.ActivityUtils;

import java.util.List;


/**
 * Description: 手势密码登录页面
 */
public class LockActivity extends BaseActivity implements LockPatternView.OnPatternListener {

    private List<LockPatternView.Cell> lockPattern;
    private static final int TIME = 1000;
    private Handler mHandler = new Handler();
    private TextView lockHint;
    private LockPatternView mLockPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_activity);

        lockHint = (TextView)findViewById(R.id.lock_hint);
        mLockPattern = (LockPatternView)findViewById(R.id.lock_pattern);


        mLockPattern.setOnPatternListener(this);
        final String password = LockLogic.getInstance().getPassword();
        if (!TextUtils.isEmpty(password)) {
            lockPattern = LockPatternView.stringToPattern(password);
        } else {
            LockLogic.getInstance().checkLock(this);
            this.finish();
        }

    }


    @Override
    public void onPatternStart() {

    }

    @Override
    public void onPatternCleared() {

    }

    @Override
    public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

    }

    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {

        if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
            lockHint.setText("请至少设置4个点");
            lockHint.setTextColor(Color.RED);
            mLockPattern.setDisplayMode(LockPatternView.DisplayMode.Wrong);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLockPattern.clearPattern();
                    mLockPattern.enableInput();
                }
            }, TIME);
            return;
        }
        if (pattern.equals(lockPattern)) {


        } else {
            LockLogic.getInstance().addErrInputCount();
            final int other = LockLogic.getInstance().getRemainErrInputCount();
            if (other > 0) {
                lockHint.setText("密码输入错误你还有 %1$d 次机会");
                lockHint.setTextColor(Color.RED);
                // 左右移动动画
                Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
               lockHint.startAnimation(shakeAnimation);
                mLockPattern.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLockPattern.clearPattern();
                        mLockPattern.enableInput();
                    }
                }, TIME);
            } else {
                mLockPattern.clearPattern();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        ActivityUtils.onExit();
    }


}
