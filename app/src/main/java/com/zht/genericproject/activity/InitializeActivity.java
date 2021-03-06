package com.zht.genericproject.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.zht.genericproject.DataStorage.SPUtil;
import com.zht.genericproject.R;
import com.zht.genericproject.base.BaseActivity;
import com.zht.genericproject.base.BaseParams;
import com.zht.genericproject.util.ToastUtil;

import java.lang.ref.WeakReference;


/**
 * 作者：zhanghaitao on 2017/12/1 16:22
 * 邮箱：820159571@qq.com
 *
 * @describe:替代原来闪屏界面的逻辑
 */

public class InitializeActivity extends BaseActivity {

    // 跳转引导页
    private static final int GO_GUIDE      = 0x01;
    // 跳转首页
    private static final int GO_MAIN       = 0x02;
    // 页面跳转逻辑
    private static final int DO_HANDLER    = 0x99;
    // 最小显示时间
    private static final int SHOW_TIME_MIN = 1000;//1000ms

    // 开始时间
    private static long          mStartTime;
    private        SplashHandler mHandler;
    // 获取时候标识IMEI
    private        String        imei;
    //IMEI（国际移动设备身份码）实体类
   // private        LoginingMo    registerMO;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);
        hideTitleBar();
        //得到TextView控件对象
        TextView textView =(TextView)findViewById(R.id.splash_text);
        //将字体文件保存在assets/fonts/目录下，创建Typeface对象
        //FZXKJW.TTF  方正行楷简体
        //FZZJ-FOJW.TTF 方正字迹-仿欧简体
        //unifont.ttf  这是啥字体
        Typeface typeFace =Typeface.createFromAsset(getAssets(),"fonts/FZZJ-FOJW.TTF");
        //使用字体
        textView.setTypeface(typeFace);
        // TODO: 2017/12/1  联网检验是否更新+是否显示向导页
        checkUpdate();
        // TODO: 2017/12/1  获取首页banner图,缓存到内存中
        getBanner();
        mHandler = new SplashHandler(this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        // 记录开始时间,此时界面是可见的
        mStartTime = System.currentTimeMillis();
        // TODO: 2017/12/1 跳转判断逻辑
        mHandler.sendEmptyMessage(DO_HANDLER);
    }


    /**
     * 获取首页的banner图，App设计里面没有的也可以不用。
     */
    private void getBanner() {


    }

    /**
     * 联网检验是否更新
     * 在这里不做下载操作
     */
    private void checkUpdate() {
        ToastUtil.showToast("我去联网检查更新版本了！");
        // TODO: 2017/12/4 比较结果放到SP中，下载完后将SP中的值改变

    }


    /**
     * 利用Handler跳转到不同界面
     */
    private static class  SplashHandler extends Handler {
        WeakReference<InitializeActivity> activity;

        public SplashHandler(InitializeActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 计算一下加载的时间
            long loadingTime = System.currentTimeMillis() - mStartTime;
            switch (msg.what) {
                /**第一次判断*/
                case DO_HANDLER:
                    // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
                    boolean isFirstIn = SPUtil.getBoolean(BaseParams.SP_IS_FIRST_IN + "-" + BaseParams.getVersion(), true);
                    // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面 且引导页开启
                    if (isFirstIn) {
                       sendEmptyMessage(GO_GUIDE);
                    } else {
                       sendEmptyMessage(GO_MAIN);
                    }
                    break;

                case GO_GUIDE:
                    // 如果比最小显示时间还短，就延时进入GuideActivity，否则直接进入
                    if (loadingTime < SHOW_TIME_MIN) {
                        postDelayed(goToGuideActivity, SHOW_TIME_MIN - loadingTime);
                    } else {
                        post(goToGuideActivity);
                    }
                    break;

                case GO_MAIN:
                    // 如果比最小显示时间还短，就延时进入HomeActivity，否则直接进入
                    if (loadingTime < SHOW_TIME_MIN) {
                        postDelayed(goToMainActivity, SHOW_TIME_MIN - loadingTime);
                    } else {
                        post(goToMainActivity);
                    }
                    break;
            }
        }

        // 进入 GuideActivity
        Runnable goToGuideActivity = new Runnable() {
            @Override
            public void run() {
                activity.get().startActivity(new Intent(activity.get(), GuideActivity.class));
                activity.get().finish();
            }
        };
        // 进入 MainActivity
        Runnable goToMainActivity  = new Runnable() {
            @Override
            public void run() {
                activity.get().startActivity(new Intent(activity.get(), MainActivity.class));
                activity.get().finish();
            }
        };
    }


}
