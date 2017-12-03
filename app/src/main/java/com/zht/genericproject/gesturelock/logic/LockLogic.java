package com.zht.genericproject.gesturelock.logic;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.zht.genericproject.activity.LoginActivity;
import com.zht.genericproject.activity.RegisterFirstActivty;
import com.zht.genericproject.activity.RegisterSecondActivty;
import com.zht.genericproject.activity.SplashActivity;
import com.zht.genericproject.gesturelock.activity.LockActivity;
import com.zht.genericproject.gesturelock.activity.LockModifyPwdActivty;
import com.zht.genericproject.gesturelock.activity.LockStepActivty;
import com.zht.genericproject.gesturelock.view.FingerPasswordBean;
import com.zht.genericproject.util.log.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 手势密码管理类,用来指示 当一个Activity Resume时, 是否要跳转到解锁界面
 */
public final class LockLogic {
    private static final String TAG                 = "LockLogic";
    /** 锁屏 或者 返回Home界面 超过多少时间后, 出现手势解锁界面 */
    private final static long                           MILLS_WAIT_LOCK     = 5 * 1000;
    /** 锁屏 或者 按了 Home 按键的时间 */
    private              long                           millsStartTime      = -1L;
    /** 忽略的Activity */
    private final Set<Class<? extends Activity>> ignoreActivities    = new HashSet<>();
    /** 输入的错误次数 */
    private              int                            errInputCount       = 0;
    /** 最大输入错误次数 */
    private final static int                            MAX_ERR_INPUT_COUNT = 5;
    /** 是否是第一次进入 */
    private              boolean                        isFirstIn           = true;

    /**
     * 初始化需要忽略的Activity
     */
    private LockLogic() {
        clearIgnoreActivities();
        // 手势密码解锁页
        registerIgnoreActivity(LockActivity.class);
        // 手势密码设置页
        registerIgnoreActivity(LockStepActivty.class);
        // 手势密码修改页
        registerIgnoreActivity(LockModifyPwdActivty.class);
        // 登录页
        registerIgnoreActivity(LoginActivity.class);
        // 注册页
        registerIgnoreActivity(RegisterFirstActivty.class);
        registerIgnoreActivity(RegisterSecondActivty.class);
        // 引导页
        registerIgnoreActivity(SplashActivity.class);
//        registerIgnoreActivity(GuideAct.class);
    }

    public static LockLogic getInstance() {
        return LockPresenterInstance.instance;
    }

    private static class LockPresenterInstance {
        static LockLogic instance = new LockLogic();
    }

    ///////////////////////////////////////////////////////////////////////////
    // 华丽分割
    //////////////////////////////////////////////////////////////////////////

    /***
     * 检测当前情况下 是否要跳转到手势界面
     */
    public boolean checkLock(final Activity activity) {
        boolean isOpen = isFingerPasswordOpened();
        Logger.d(TAG, "isFingerPasswordOpened = " + isOpen);
        if (isOpen) {
            if (!LockLogic.getInstance().isIgnoreActivity(activity) && (isFirstIn() || LockLogic.getInstance().isElapsedEnough())) {
                isFirstIn = false;
                Logger.i(TAG, "from " + activity.getClass().getSimpleName());
                activity.startActivity(new Intent(activity, LockActivity.class));
                return true;
            }
            millsStartTime = -1L;
        } else {
            // 用户已登录，却没有开启手势密码，则到手势密码设置页面
            //  TODO:需要修改 在Application里面判断用户是否登陆
            if (true/*MyApplication.getInstance().isLand()*/) {
                if (!LockLogic.getInstance().isIgnoreActivity(activity)) {
                    isFirstIn = false;
                    activity.startActivity(new Intent(activity, LockStepActivty.class));
                }
            }
        }
        return false;
    }

    /**
     * 重置状态
     * E.G. 解锁次数、APP到后台时间 等
     */
    public void reset() {
        millsStartTime = -1L;
        errInputCount = 0;
        FingerPasswordBean bean = getEntity();
        if (bean != null) {
            bean.setErrInputCount(errInputCount);
            saveEntity(bean);
        }
    }

    /**
     * APP到后台,记录时间
     */
    public void start() {
        if (millsStartTime < 0) {
            millsStartTime = System.currentTimeMillis();
        }
    }

    /**
     * APP在后台运行的时间，是否小于无需解锁的最小时间
     */
    public boolean isElapsedEnough() {
        return millsStartTime > 0 && System.currentTimeMillis() > (millsStartTime + MILLS_WAIT_LOCK);
    }

    /**
     * 保存解锁错误次数
     */
    public void addErrInputCount() {
        FingerPasswordBean bean = getEntity();
        Logger.i(TAG, "errInputCount:" + bean.getErrInputCount());
        this.errInputCount = Math.min(bean.getErrInputCount() + 1, MAX_ERR_INPUT_COUNT);
        bean.setErrInputCount(errInputCount);
        saveEntity(bean);
    }

    /**
     * 重置解锁错误次数
     */
    public void resetErrInputCount() {
        this.errInputCount = 0;
        FingerPasswordBean bean = getEntity();
        bean.setErrInputCount(0);
        saveEntity(bean);
    }

    /**
     * 获取还剩余的可解锁的次数
     */
    public int getRemainErrInputCount() {
        return Math.max(MAX_ERR_INPUT_COUNT - this.errInputCount, 0);
    }

    /**
     * 获取手势密码
     */
    public String getPassword() {
        //TODO:需要修改
        final FingerPasswordBean bean         = getEntity();
        if (bean != null) {
            Logger.i("LockLogic", "errInputCount:" + bean.getErrInputCount());
            return bean.getPassword();
        }
        return null;
    }

    /**
     * 保存手势密码
     */
    public void setPassword(final String password) {
        if (!TextUtils.isEmpty(password)) {
            FingerPasswordBean bean = new FingerPasswordBean();
            bean.setPassword(password);
            saveEntity(bean);
        } else {
            //TODO:需要修改
//            OauthTokenMo oauthTokenMo = SharedInfo.getInstance().getValue(OauthTokenMo.class);
//            SPUtil.remove(FingerPasswordBean.class, oauthTokenMo.getUserId());
        }
    }

    /**
     * 当前手势密码是否打开
     */
    private boolean isFingerPasswordOpened() {
        final FingerPasswordBean bean = getEntity();
        return bean != null && !TextUtils.isEmpty(bean.getPassword());
    }

    /**
     * 清空忽略列表
     */
    private void clearIgnoreActivities() {
        ignoreActivities.clear();
    }

    /**
     * 注册忽略的Activity
     */
    private void registerIgnoreActivity(Class<? extends Activity> clazz) {
        ignoreActivities.add(clazz);
    }

    /**
     * 判断activity是否在忽略列表内
     */
    private boolean isIgnoreActivity(Activity activity) {
        return activity != null && ignoreActivities.contains(activity.getClass());
    }

    public boolean isFirstIn() {
        return isFirstIn;
    }

    public void setFirstIn(boolean firstIn) {
        isFirstIn = firstIn;
    }

    /**
     * 加密保存手势密码数据
     */
    private void saveEntity(FingerPasswordBean bean) {
        //TODO:需要修改
//        OauthTokenMo oauthTokenMo = SharedInfo.getInstance().getValue(OauthTokenMo.class);
//        if (bean != null && oauthTokenMo != null) {
//            SPUtil.setEntity(bean, oauthTokenMo.getUserId(), true);
//        }
    }

    /**
     * 获取加密保存的手势密码数据类
     */
    private FingerPasswordBean getEntity() {
        //TODO:需要修改

//        OauthTokenMo oauthTokenMo = SharedInfo.getInstance().getValue(OauthTokenMo.class);
//        if (oauthTokenMo == null) {
//            return new FingerPasswordBean();
//        }
//        FingerPasswordBean bean = SPUtil.getEntity(FingerPasswordBean.class, oauthTokenMo.getUserId(), null, true);
//        if (null == bean) {
//            bean = new FingerPasswordBean();
//        }
        FingerPasswordBean bean = null;
        return bean;
    }
}
