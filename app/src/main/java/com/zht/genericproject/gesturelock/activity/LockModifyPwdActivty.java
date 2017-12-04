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

import butterknife.ButterKnife;


/**
 * Description: 手势密码修改页面
 */
public class LockModifyPwdActivty extends BaseActivity implements LockPatternView.OnPatternListener {


    TextView lockModifyHint;
    LockPatternView mLockPattern;
    private List<LockPatternView.Cell> lockPattern;
    private static final int TIME = 1000;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_modify_activity);
        ButterKnife.bind(this);

        lockModifyHint=(TextView) findViewById(R.id.lock_modify_hint);
        mLockPattern=(LockPatternView) findViewById(R.id.lock_pattern);

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
    protected void onStart() {
        super.onStart();
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
            lockModifyHint.setText("请至少设置4个点");
            lockModifyHint.setTextColor(Color.RED);
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
            LockLogic.getInstance().reset();
            ActivityUtils.push(LockStepActivty.class);
            this.finish();
        } else {
            LockLogic.getInstance().addErrInputCount();
            final int other = LockLogic.getInstance().getRemainErrInputCount();
            if (other > 0) {
                lockModifyHint.setText("密码输入错误你还有 %1$d 次机会");
                lockModifyHint.setTextColor(Color.RED);
                // 左右移动动画
                Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
                lockModifyHint.startAnimation(shakeAnimation);
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

}
