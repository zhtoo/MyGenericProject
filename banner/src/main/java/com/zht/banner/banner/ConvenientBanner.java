package com.zht.banner.banner;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zht.banner.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.zht.banner.R.id.cbLoopViewPager;

/**
 * 作者：zhanghaitao on 2017/12/20 16:20
 * 邮箱：820159571@qq.com
 *
 * @describe:页面翻转控件、极方便的广告栏、支持无限循环，自动翻页，翻页特效
 */

public class ConvenientBanner<T> extends LinearLayout {
    private List<T> mData;
    private int[] page_indicatorId;
    private ArrayList<ImageView> mPointViews;
    private CBPageChangeListener pageChangeListener;
    private CBPageAdapter<T> pageAdapter;
    private CBLoopViewPager viewPager;
    private ViewGroup loPageTurningPoint;
    private long autoTurningTime;
    private boolean turning;
    private boolean canTurn;
    private boolean manualPageable;
    private Handler timeHandler;
    private Runnable adSwitchTask;

    public ConvenientBanner(Context context) {
        this(context, (AttributeSet) null);
    }

    public ConvenientBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPointViews = new ArrayList();
        this.canTurn = false;
        this.manualPageable = true;
        this.timeHandler = new Handler();
        this.adSwitchTask = new Runnable() {
            public void run() {
                if (ConvenientBanner.this.viewPager != null && ConvenientBanner.this.turning) {
                    int page = ConvenientBanner.this.viewPager.getCurrentItem() + 1;
                    ConvenientBanner.this.viewPager.setCurrentItem(page);
                    ConvenientBanner.this.timeHandler.postDelayed(ConvenientBanner.this.adSwitchTask, ConvenientBanner.this.autoTurningTime);
                }

            }
        };
        this.init(context);
    }

    private void init(Context context) {
        View hView = LayoutInflater.from(context).inflate(R.layout.include_viewpager, this, true);
        this.viewPager = (CBLoopViewPager) hView.findViewById(cbLoopViewPager);
        this.loPageTurningPoint = (ViewGroup) hView.findViewById(R.id.loPageTurningPoint);
        this.initViewPagerScroll();
    }

    public ConvenientBanner<T> setPages(CBViewHolderCreator<? extends CBPageAdapter.Holder> holderCreator, List<T> data) {
        this.mData = data;
        this.pageAdapter = new CBPageAdapter(holderCreator, this.mData);
        this.viewPager.setAdapter(this.pageAdapter);
        this.viewPager.setBoundaryCaching(true);
        if (this.page_indicatorId != null) {
            this.setPageIndicator(this.page_indicatorId);
        }

        return this;
    }
    /**
     * 通知数据变化
     * 如果只是增加数据建议使用 notifyDataSetAdd()
     */
    public void notifyDataSetChanged() {
        this.viewPager.getAdapter().notifyDataSetChanged();
        if (this.page_indicatorId != null) {
            this.setPageIndicator(this.page_indicatorId);
        }

    }
    /**
     * 设置底部指示器是否可见
     *
     * @param visible
     */
    public ConvenientBanner<T> setPointViewVisible(boolean visible) {
        this.loPageTurningPoint.setVisibility(visible ? VISIBLE : GONE);
        return this;
    }
    /**
     * 底部指示器资源图片
     *
     * @param page_indicatorId
     */
    public ConvenientBanner<T> setPageIndicator(int[] page_indicatorId) {
        this.loPageTurningPoint.removeAllViews();
        this.mPointViews.clear();
        this.page_indicatorId = page_indicatorId;
        if (this.mData == null) {
            return this;
        } else {
            for (int count = 0; count < this.mData.size(); ++count) {
                ImageView pointView = new ImageView(this.getContext());
                pointView.setPadding(5, 0, 5, 0);
                if (this.mPointViews.isEmpty()) {
                    pointView.setImageResource(page_indicatorId[1]);
                } else {
                    pointView.setImageResource(page_indicatorId[0]);
                }

                this.mPointViews.add(pointView);
                this.loPageTurningPoint.addView(pointView);
            }

            this.pageChangeListener = new CBPageChangeListener(this.mPointViews, page_indicatorId);
            this.viewPager.setOnPageChangeListener(this.pageChangeListener);
            return this;
        }
    }
    /**
     * 指示器的方向
     * @param align  三个方向：居左 （RelativeLayout.ALIGN_PARENT_LEFT），居中 （RelativeLayout.CENTER_HORIZONTAL），居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     * @return
     */
    public ConvenientBanner<T> setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.loPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_LEFT ? -1 : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT ? -1 : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL ? -1 : 0);
        this.loPageTurningPoint.setLayoutParams(layoutParams);
        return this;
    }
    /***
     * 是否开启了翻页
     * @return
     */
    public boolean isTurning() {
        return this.turning;
    }
    /***
     * 开始翻页
     * @param autoTurningTime 自动翻页时间
     * @return
     */
    public ConvenientBanner<T> startTurning(long autoTurningTime) {
        if (this.turning) {
            this.stopTurning();
        }

        this.canTurn = true;
        this.autoTurningTime = autoTurningTime;
        this.turning = true;
        this.timeHandler.postDelayed(this.adSwitchTask, autoTurningTime);
        return this;
    }

    public void stopTurning() {
        this.turning = false;
        this.timeHandler.removeCallbacks(this.adSwitchTask);
    }
    /**
     * 自定义翻页动画效果
     *
     * @param transformer
     * @return
     */
    public ConvenientBanner<T> setPageTransformer(ViewPager.PageTransformer transformer) {
        this.viewPager.setPageTransformer(true, transformer);
        return this;
    }

    public ConvenientBanner<T> setPageTransformer(ConvenientBanner.Transformer transformer) {
        try {
            String e = this.getClass().getPackage().getName();
            this.viewPager.setPageTransformer(true, (ViewPager.PageTransformer) Class.forName(e + ".transforms." + transformer.getClassName()).newInstance());
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return this;
    }
    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field e = null;
            e = ViewPager.class.getDeclaredField("mScroller");
            e.setAccessible(true);
            ViewPagerScroller scroller = new ViewPagerScroller(this.viewPager.getContext());
            e.set(this.viewPager, scroller);
        } catch (NoSuchFieldException var3) {
            var3.printStackTrace();
        } catch (IllegalArgumentException var4) {
            var4.printStackTrace();
        } catch (IllegalAccessException var5) {
            var5.printStackTrace();
        }

    }

    public boolean isManualPageable() {
        return this.viewPager.isCanScroll();
    }

    public void setManualPageable(boolean manualPageable) {
        this.viewPager.setCanScroll(manualPageable);
    }
    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 1) {
            // 开始翻页
            if (this.canTurn) {
                this.startTurning(this.autoTurningTime);
            }
        } else if (ev.getAction() == 0 && this.canTurn) {
            // 停止翻页
            this.stopTurning();
        }

        return super.dispatchTouchEvent(ev);
    }
    //获取当前的页面index
    public int getCurrentPageIndex() {
        return this.viewPager != null ? this.viewPager.getCurrentItem() : -1;
    }

    public static enum Transformer {
        DefaultTransformer("DefaultTransformer"),
        AccordionTransformer("AccordionTransformer"),
        BackgroundToForegroundTransformer("BackgroundToForegroundTransformer"),
        CubeInTransformer("CubeInTransformer"),
        CubeOutTransformer("CubeOutTransformer"),
        DepthPageTransformer("DepthPageTransformer"),
        FlipHorizontalTransformer("FlipHorizontalTransformer"),
        FlipVerticalTransformer("FlipVerticalTransformer"),
        ForegroundToBackgroundTransformer("ForegroundToBackgroundTransformer"),
        RotateDownTransformer("RotateDownTransformer"),
        RotateUpTransformer("RotateUpTransformer"),
        StackTransformer("StackTransformer"),
        TabletTransformer("TabletTransformer"),
        ZoomInTransformer("ZoomInTransformer"),
        ZoomOutSlideTransformer("ZoomOutSlideTransformer"),
        ZoomOutTranformer("ZoomOutTranformer");

        private final String className;

        private Transformer(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }
    }

    public static enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT,
        ALIGN_PARENT_RIGHT,
        CENTER_HORIZONTAL;

        private PageIndicatorAlign() {
        }
    }
}
