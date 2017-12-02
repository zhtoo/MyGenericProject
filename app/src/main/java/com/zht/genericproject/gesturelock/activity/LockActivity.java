package com.zht.genericproject.gesturelock.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zht.genericproject.gesturelock.view.LockPatternView;

import java.util.List;


/**
 * Description: 手势密码登录页面
 */
public class LockActivity extends AppCompatActivity implements LockPatternView.OnPatternListener {
//    private LockActBinding binding;
//    private List<Cell> lockPattern;
//    private              ImageView[] pointers = new ImageView[9];
//    private static final int         TIME     = 1000;
//    private Handler mHandler = new Handler();
//
//    private static final int MAX_AVAILABLE_TIMES = Integer.MAX_VALUE;
//    boolean isTouch = SPUtil.getBoolean("isTouch", true);
//    int i = 1;
//    private CustomDialog dialog;
//    private FingerPrinterView fingerPrinterView;
//    private int fingerErrorNum = 0; // 指纹错误次数
//    RxFingerPrinter rxfingerPrinter;
//    boolean isFirstIn = SPUtil.getBoolean(BaseParams.SP_IS_FIRST_INE_LOCK+ BaseParams.getVersion(), true);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding = DataBindingUtil.setContentView(this, R.layout.lock_act);
//        fingerPrinterView = (FingerPrinterView) findViewById(R.id.fpv);
//        /**
//         * 给指纹识别在解锁时添加对话框
//         * created by zhangyapeng on 2017.9.4
//         */
//        if (isTouch) {
//            dialog = new CustomDialog.Builder(this)
//                    .setMessage("“网汇贷”正在进行指纹识别")
//
//                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            rxfingerPrinter.unSubscribe(this);
//                            dialog.dismiss();
//                        }
//                    })
//                    .create();
//
//            /**
//             * 系统版本小于6.0的手机，不支持指纹
//             */
//            if (SystemUtil.getApiCode() < 23) {
//                if (isFirstIn){
//                    SPUtil.setValue(BaseParams.SP_IS_FIRST_INE_LOCK+ BaseParams.getVersion(), false);
//                    Toast.makeText(this,"当前系统版本小于6.0，不支持指纹识别", Toast.LENGTH_SHORT).show();
//                }
//                dialog.dismiss();
//            } else {
//                dialog.show();
//                fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
//                    @Override
//                    public void onChange(int state) {
//                        if (state == FingerPrinterView.STATE_CORRECT_PWD) {
//                            dialog.dismiss();
//   //                            ToastUtil.showToast("指纹识别成功");
//                            fingerErrorNum = 0;
//                            refreshToken();
//                        }
//                        if (state == FingerPrinterView.STATE_WRONG_PWD) {
//                            ToastUtil.showToast("指纹识别失败，还剩" + (5 - fingerErrorNum) + "次机会");
//                            fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
//                            if (5-fingerErrorNum == 0){
//                                ToastUtil.showToast("“请1分钟后再试”或“使用手势密码解锁”");
//                                dialog.dismiss();
//                            }
//                        }
//                    }
//                });
//            }
//        }

//        binding.setViewModel(new LockVM());
//        //smallView();
//        binding.lockPattern.setOnPatternListener(this);
//        final String password = LockLogic.getInstance().getPassword();
//        if (!TextUtils.isEmpty(password)) {
//            lockPattern = LockPatternView.stringToPattern(password);
//        } else {
//            LockLogic.getInstance().checkLock(this);
//            this.finish();
//        }

//        rxfingerPrinter = new RxFingerPrinter(this);
//        if (isTouch) {
//            fingerErrorNum = 0;
//            rxfingerPrinter.unSubscribe(this);
//            Subscription subscription = rxfingerPrinter.begin().subscribe(new Subscriber<Boolean>() {
//                @Override
//                public void onStart() {
//                    super.onStart();
//                    if (fingerPrinterView.getState() == FingerPrinterView.STATE_SCANING) {
//                        return;
//                    } else if (fingerPrinterView.getState() == FingerPrinterView.STATE_CORRECT_PWD
//                            || fingerPrinterView.getState() == FingerPrinterView.STATE_WRONG_PWD) {
//                        fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
//                    } else {
//                        fingerPrinterView.setState(FingerPrinterView.STATE_SCANING);
//                    }
//                }
//
//                @Override
//                public void onCompleted() {
//
//                }
//
//                @Override
//                public void onError(Throwable e) {
//                    if(e instanceof FPerException){
//                        if (isFirstIn){
//                            SPUtil.setValue(BaseParams.SP_IS_FIRST_INE_LOCK+ BaseParams.getVersion(), false);
//                            Toast.makeText(LockActivity.this,((FPerException) e).getDisplayMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//
//                @Override
//                public void onNext(Boolean aBoolean) {
//                    if(aBoolean){
//                        fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
//                    }else{
//                        fingerErrorNum++;
//                        fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
//                    }
//                }
//            });
//            rxfingerPrinter.addSubscription(this,subscription);
//        }
    }

//    @Override
//    public void onPatternDetected(List<Cell> pattern) {
//
//        if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
//            binding.lockHint.setText(getResources().getString(R.string.lock_pattern_recording_incorrect_too_short));
//            binding.lockHint.setTextColor(Color.RED);
//            binding.lockPattern.setDisplayMode(DisplayMode.Wrong);
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    binding.lockPattern.clearPattern();
//                    binding.lockPattern.enableInput();
//                }
//            }, TIME);
//            return;
//        }
//        //changeSmall(pattern);
//        if (pattern.equals(lockPattern)) {
//            refreshToken();
//        } else {
//            LockLogic.getInstance().addErrInputCount();
//            final int other = LockLogic.getInstance().getRemainErrInputCount();
//            if (other > 0) {
//                binding.lockHint.setText(getString(R.string.lock_pattern_error2, other));
//                binding.lockHint.setTextColor(Color.RED);
//                // 左右移动动画
//                Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
//                binding.lockHint.startAnimation(shakeAnimation);
//                binding.lockPattern.setDisplayMode(DisplayMode.Wrong);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        binding.lockPattern.clearPattern();
//                        binding.lockPattern.enableInput();
//                    }
//                }, TIME);
//            } else {
//                binding.lockPattern.clearPattern();
//                UserLogic.signOutPwdTime(getString(R.string.lock_pattern_error3), true);
//            }
//        }
//
//    }

//    private void refreshToken() {
//        Call<OauthTokenMo> call = RDClient.getService(UserService.class).refreshToken(SharedInfo.getInstance().getValue(OauthTokenMo.class).getRefreshToken());
//        NetworkUtil.showCutscenes(call);
//        call.enqueue(new RequestCallBack<OauthTokenMo>(true) {
//            @Override
//            public void onSuccess(Call<OauthTokenMo> call, Response<OauthTokenMo> response) {
//                binding.lockPattern.clearPattern();
//                UserLogic.saveToken(response.body());
//                LockLogic.getInstance().reset();
//                finish();
//            }
//
//            @Override
//            public void onFailure(Call<OauthTokenMo> call, Throwable t) {
//                binding.lockPattern.clearPattern();
//                super.onFailure(call, t);
//            }
//        });
//    }





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

//        if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
//            binding.lockHint.setText(getResources().getString(R.string.lock_pattern_recording_incorrect_too_short));
//            binding.lockHint.setTextColor(Color.RED);
//            binding.lockPattern.setDisplayMode(DisplayMode.Wrong);
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    binding.lockPattern.clearPattern();
//                    binding.lockPattern.enableInput();
//                }
//            }, TIME);
//            return;
//        }
//        //changeSmall(pattern);
//        if (pattern.equals(lockPattern)) {
//            refreshToken();
//        } else {
//            LockLogic.getInstance().addErrInputCount();
//            final int other = LockLogic.getInstance().getRemainErrInputCount();
//            if (other > 0) {
//                binding.lockHint.setText(getString(R.string.lock_pattern_error2, other));
//                binding.lockHint.setTextColor(Color.RED);
//                // 左右移动动画
//                Animation shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
//                binding.lockHint.startAnimation(shakeAnimation);
//                binding.lockPattern.setDisplayMode(DisplayMode.Wrong);
//                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        binding.lockPattern.clearPattern();
//                        binding.lockPattern.enableInput();
//                    }
//                }, TIME);
//            } else {
//                binding.lockPattern.clearPattern();
//                UserLogic.signOutPwdTime(getString(R.string.lock_pattern_error3), true);
//            }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



    //    @Override
//    public void onBackPressed() {
//        ActivityUtils.onExit();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
////        if (isTouch) {
////            mFingerprintIdentify.cancelIdentify();
////        }
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        rxfingerPrinter.unSubscribe(this);
//    }



}
