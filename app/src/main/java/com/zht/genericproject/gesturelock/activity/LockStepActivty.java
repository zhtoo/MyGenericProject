package com.zht.genericproject.gesturelock.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zht.genericproject.R;
import com.zht.genericproject.base.BaseActivity;
import com.zht.genericproject.gesturelock.logic.LockLogic;
import com.zht.genericproject.gesturelock.view.LockPatternView;
import com.zht.genericproject.util.ActivityUtils;
import com.zht.genericproject.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 手势密码设置界面
 */
public class LockStepActivty extends BaseActivity implements LockPatternView.OnPatternListener {
    private static final String CONFIRM = "confirm";
    private static final int COLOR_TEXT_NORMAL = 0x9900A8FF;
    private TextView tv_hint;
    private TextView tv_again;
    private LockPatternView lockPatternView;
    private ImageView[] pointers = new ImageView[9];
    // 开始设置
    private static final int STEP_1 = 1;
    // 第一次设置手势完成
    private static final int STEP_2 = 2;
    // 按下继续按钮
    private static final int STEP_3 = 3;
    // 操作延时
    private static final int TIME = 1000;
    // 当前第几步
    private int step;
    private List<LockPatternView.Cell> choosePattern = new ArrayList<>();
    private boolean confirm = false;
    private Handler mHandler = new Handler();
    private LinearLayout lockSetupSmall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lock_step_activity);
        smallView();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView() {
        tv_hint =(TextView) findViewById(R.id.lock_setup_hint);
        tv_again =(TextView) findViewById(R.id.lock_setup_again);
        tv_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step = STEP_1;
                updateView();
            }
        });
        lockPatternView =(LockPatternView) findViewById(R.id.lock_pattern);
        lockPatternView.setOnPatternListener(this);
        step = STEP_1;
        updateView();
    }

    private void updateView() {
        switch (step) {
            case STEP_1:
                choosePattern.clear();
                confirm = false;
                tv_hint.setText("请滑动设置密码");
                tv_again.setVisibility(View.INVISIBLE);
                changeSmall();
                lockPatternView.clearPattern();
                lockPatternView.enableInput();
                break;

            case STEP_2:
                tv_hint.setText("请再次滑动确认密码");
                tv_again.setVisibility(View.VISIBLE);
                changeSmall();
                lockPatternView.clearPattern();
                lockPatternView.enableInput();
                break;

            case STEP_3:
                if (confirm) {
                    // 保存密码到本地
                    LockLogic.getInstance().setPassword(LockPatternView.patternToString(choosePattern));
                    LockLogic.getInstance().reset();
                    ToastUtil.showToast("完成设置");

                    // Intent intent = new Intent();
                    // intent.putExtra(CONFIRM, confirm);
                    // setResult(RESULT_OK, intent);
                    this.finish();
                } else {
                    tv_hint.setText("与上次绘制不一致，请重新绘制");
                    tv_hint.setTextColor(Color.RED);
                    //tv_again.setVisibility(View.VISIBLE);
                    lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lockPatternView.clearPattern();
                            lockPatternView.enableInput();
                        }
                    }, TIME);
                }
                break;

            default:
                break;
        }
    }

    private void smallView() {
        lockSetupSmall = (LinearLayout) findViewById(R.id.lock_setup_small);
        //循环创建9个小视图
        for (int i = 0; i < 3; i++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < 3; j++) {
                ImageView pointer = new ImageView(this);
                pointer.setScaleType(ImageView.ScaleType.CENTER);
                pointer.setPadding(4, 4, 4, 4);
                pointer.setImageDrawable(getResources().getDrawable(R.drawable.lock_small_default));
                layout.addView(pointer);
                pointers[i * 3 + j] = pointer;
            }
            lockSetupSmall.addView(layout);
        }
    }

    /**
     * 改变顶部的小窗口
     */
    private void changeSmall() {
        for (ImageView pointer : pointers) {
            pointer.setImageDrawable(getResources().getDrawable(R.drawable.lock_small_default));
        }
        for (LockPatternView.Cell cell : choosePattern) {
            pointers[cell.getRow() * 3 + cell.getColumn()].setImageDrawable(getResources().getDrawable(R.drawable.lock_small_blue));
        }
    }


    @Override
    public void onPatternDetected(List<LockPatternView.Cell> pattern) {
        if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE && step == STEP_1) {
            tv_hint.setText("请至少设置4个点");
            tv_hint.setTextColor(Color.RED);
            lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
            lockPatternView.clearPattern();
            return;
        }

        if (choosePattern.size() == 0) {
            choosePattern = new ArrayList<>(pattern);
            step = STEP_2;
            updateView();
            return;
        }
        confirm = choosePattern.equals(pattern);

        step = STEP_3;
        updateView();
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.onExit();
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

}
