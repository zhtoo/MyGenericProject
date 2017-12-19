package com.zht.genericproject.base;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zht.genericproject.R;

import java.lang.reflect.Field;

/**
 * 作者：zhanghaitao on 2017/12/18 13:19
 * 邮箱：820159571@qq.com
 *
 * @describe:标题栏
 */

public abstract class TitleBarActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "TitleBarActivity";
    //子类的视图(交由子类实现)
    private FrameLayout mContentLayout;

    //沉浸式状态栏
    private LinearLayout mAppBar;

    //状态栏
    private LinearLayout mTransparentStatusBar;

    //标题栏
    private RelativeLayout mTitleBar;

    //左边按钮
    private LinearLayout mBarLeftButton;
    //左边图片
    private ImageView mBarLeftImage;

    //右边按钮
    private LinearLayout mBarRightButton;
    //右边文字
    private TextView mBarRightText;
    //右边的图片
    private ImageView mBarRightImage;

    //标题文字
    private TextView mBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initState();
    }

    /**
     * 初始化BaseActivity布局中的控件
     */
    private void initViews() {
        super.setContentView(R.layout.activity_title_bar);
        mAppBar = (LinearLayout) findViewById(R.id.layout_titlebar);
        mTransparentStatusBar = (LinearLayout) findViewById(R.id.transparent_status_bar);
        mTitleBar = (RelativeLayout) findViewById(R.id.title_bar);
        mBarLeftButton = (LinearLayout) findViewById(R.id.bar_left_button);
        mBarLeftImage = (ImageView) findViewById(R.id.bar_left_image);
        mBarRightButton = (LinearLayout) findViewById(R.id.bar_right_button);
        mBarRightText = (TextView) findViewById(R.id.bar_right_text);
        mBarRightImage = (ImageView) findViewById(R.id.bar_right_image);
        mBarTitle = (TextView) findViewById(R.id.bar_title);
        //界面内容，具体试图由子类决定
        mContentLayout = (FrameLayout) findViewById(R.id.layout_content);
        //设置监听
        mBarLeftButton.setOnClickListener(this);
        mBarRightButton.setOnClickListener(this);
    }

    /**
     * 动态的设置状态栏  实现沉浸式状态栏
     */
    private void initState() {


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //calculateStatusColor(Color.WHITE, (int) alphaValue)
        }


        //当系统版本为4.4或者4.4以上时可以使用沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTransparentStatusBar.setVisibility(View.VISIBLE);
            //获取到状态栏的高度
            int statusHeight;
            //通过反射的方式获取状态栏高度
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                statusHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
                statusHeight = 0;
            }
            //动态的设置隐藏布局的高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTransparentStatusBar.getLayoutParams();
            params.height = statusHeight;
            mTransparentStatusBar.setLayoutParams(params);
        }
    }

    /**
     * 左、右按钮的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bar_left_button:
                onLeftBackward(v);
                break;
            case R.id.bar_right_button:
                onRightForward(v);
                break;
            default:
                break;
        }
    }

    /**-------------------状态设置-------------------------------------*/
    //-------------------------start------------------------------------

    /**
     * 是否显示左边按钮
     *
     * @param show true显示
     */
    public void isShowLeftView(boolean show) {
        if (mBarLeftButton != null) {
            if (show) {
                mBarLeftButton.setVisibility(View.VISIBLE);
            } else {
                mBarLeftButton.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 是否显示右边按钮
     *
     * @param show true则显示
     */
    public void isShowRightView(boolean show) {
        if (mBarRightButton != null) {
            if (show) {
                mBarRightButton.setVisibility(View.VISIBLE);
            } else {
                mBarRightButton.setVisibility(View.GONE);
            }
        }
    }

    //设置标题内容
    public void setTitle(String title) {
        if (mBarTitle != null) {
            mBarTitle.setText(title);
        }
    }

    //设置标题文字颜色
    public void setTitleTextColor(int textColor) {
        if (mBarTitle != null) {
            mBarTitle.setTextColor(getResources().getColor(textColor));
        }
    }

    //设置标题文字颜色
    public void setTitleTextColor(ColorStateList colors) {
        if (mBarTitle != null) {
            mBarTitle.setTextColor(colors);
        }
    }

    //设置整体的背景(状态栏+标题栏)
    public void setTitleBackground(int backgroundColor) {
        if (mAppBar != null) {
            mAppBar.setBackgroundResource(backgroundColor);
        }
    }

    //设置标题栏的背景（状态栏）
    public void setStatusBarBackground(int backgroundColor) {
        if (mTransparentStatusBar != null) {
            mTransparentStatusBar.setBackgroundResource(backgroundColor);
        }
    }

    //设置标题栏的背景
    public void setmTitleBarBackground(int backgroundColor) {
        if (mTitleBar != null) {
            mTitleBar.setBackgroundResource(backgroundColor);
        }
    }

    //让标题栏消失(透明状态栏的颜色需要设置)
    public void hideTitleBar(int color) {
        if (mTitleBar != null) {
            mTitleBar.setVisibility(View.GONE);
        }
        if (mTransparentStatusBar != null) {
            mTransparentStatusBar.setBackgroundResource(color);
        }
    }

    public void hideTitleBar() {
        if (mTitleBar != null) {
            mTitleBar.setVisibility(View.GONE);
        }
        if (mTransparentStatusBar != null) {
            mTransparentStatusBar.setVisibility(View.GONE);
        }
    }

    // 让标题栏显示
    public void showTitleBar() {
        if (mAppBar != null) {
            mAppBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置左边的状态
     */
    //只设置图片
    public void setLeftStatus(int image) {
        if (mBarLeftImage != null) {
            int visibility = mBarLeftImage.getVisibility();
            if (visibility == View.GONE || visibility == View.INVISIBLE) {
                mBarLeftImage.setVisibility(View.VISIBLE);
            }
            mBarLeftImage.setImageResource(image);
        }
    }

    /**
     * 设置右边的状态
     * 获取view的状态
     * int status = view.getVisibility()
     * status == View.GONE          不可见也不占用布局空间
     * status == View.VISIBLE       可见
     * status == View.INVISIBLE     不可见但是占用布局空间
     */
    //只设置文字
    public void setRightStatus(String title) {
        if (mBarRightText != null) {
            int visibility = mBarRightText.getVisibility();
            if (visibility == View.GONE || visibility == View.INVISIBLE) {
                mBarRightText.setVisibility(View.VISIBLE);
            }
            mBarRightText.setText(title);
        }
        if (mBarRightImage != null) {
            mBarRightImage.setVisibility(View.GONE);
        }
    }

    //只设置图片
    public void setRightStatus(int image) {
        if (mBarRightText != null) {
            mBarRightText.setVisibility(View.GONE);
        }
        if (mBarRightImage != null) {
            int visibility = mBarRightImage.getVisibility();
            if (visibility == View.GONE || visibility == View.INVISIBLE) {
                mBarRightImage.setVisibility(View.VISIBLE);
            }
            mBarRightImage.setImageResource(image);
        }
    }

    //设置图片和图片的尺寸
    public void setRightStatus(int image, float width, float height) {
        if (mBarRightText != null) {
            mBarRightText.setVisibility(View.GONE);
        }
        if (mBarRightImage != null) {
            int visibility = mBarRightImage.getVisibility();
            if (visibility == View.VISIBLE || visibility == View.INVISIBLE) {
                mBarRightImage.setVisibility(View.GONE);
            }
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    (int) width,
                    (int) height);
            int marginRight = (int) getResources().getDimension(R.dimen.activity_vertical_margin);
            lp.setMargins(0, 0, marginRight, 0);
            imageView.setLayoutParams(lp);
            mBarRightButton.addView(imageView);
            imageView.setImageResource(image);
        }
    }

    //既有图片，又有文字
    public void setRightStatus(String title, int image) {
        if (mBarRightText != null) {
            int visibility = mBarRightText.getVisibility();
            if (visibility == View.GONE || visibility == View.INVISIBLE) {
                mBarRightText.setVisibility(View.VISIBLE);
            }
            mBarRightText.setText(title);
        }
        if (mBarRightImage != null) {
            int visibility = mBarRightImage.getVisibility();
            if (visibility == View.GONE || visibility == View.INVISIBLE) {
                mBarRightImage.setVisibility(View.VISIBLE);
            }
            mBarRightImage.setImageResource(image);
        }
    }

    //设置右边图片的样式
    public void setRightImage(int image) {
        if (mBarRightImage != null) {
            int visibility = mBarRightImage.getVisibility();
            if (visibility == View.GONE || visibility == View.INVISIBLE) {
                mBarRightImage.setVisibility(View.VISIBLE);
            }
            mBarRightImage.setImageResource(image);
        }
    }
    //-------------------------end------------------------------------


    /**-------------------功能设置-----------------------*/

    //-------------------start------------------------

    /**
     * 左边按钮点击后触发，默认关闭当前Acticity
     * @param backwardView
     */
    public abstract void onLeftBackward(View backwardView);

    /**
     * 在需要做保存、分类的时候重写
     *
     * @return 决定是否执行finish操作, true ---> finish
     */
    public boolean savaData() {
        return true;
    }

    /**
     * 右边按钮点击后触发
     *
     * @param forwardView
     */
    public void onRightForward(View forwardView) {

    }

    //----------------------end--------------------------

    /**
     * 从子类获取FrameLayout并调用父类removeAllViews()方法
     * 将子类的试图加载到布局中
     */
    @Override
    public void setContentView(int layoutResID) {
        mContentLayout.removeAllViews();
        View.inflate(this, layoutResID, mContentLayout);
        onContentChanged();
    }

    @Override
    public void setContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        onContentChanged();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view, params);
        onContentChanged();
    }

    /**
     * 监听Back键按下事件:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (savaData()) {
                finish();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


}
