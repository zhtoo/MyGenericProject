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
 * @describe:
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

    public void notifyDataSetChanged() {
        this.viewPager.getAdapter().notifyDataSetChanged();
        if (this.page_indicatorId != null) {
            this.setPageIndicator(this.page_indicatorId);
        }

    }

    public ConvenientBanner<T> setPointViewVisible(boolean visible) {
        this.loPageTurningPoint.setVisibility(visible ? VISIBLE : GONE);
        return this;
    }

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

    public ConvenientBanner<T> setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.loPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_LEFT ? -1 : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT ? -1 : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL ? -1 : 0);
        this.loPageTurningPoint.setLayoutParams(layoutParams);
        return this;
    }

    public boolean isTurning() {
        return this.turning;
    }

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

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 1) {
            if (this.canTurn) {
                this.startTurning(this.autoTurningTime);
            }
        } else if (ev.getAction() == 0 && this.canTurn) {
            this.stopTurning();
        }

        return super.dispatchTouchEvent(ev);
    }

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
