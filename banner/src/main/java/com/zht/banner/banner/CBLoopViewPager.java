package com.zht.banner.banner;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 作者：zhanghaitao on 2017/12/20 16:18
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class CBLoopViewPager extends ViewPager {
    private static final boolean DEFAULT_BOUNDARY_CASHING = true;
    OnPageChangeListener mOuterPageChangeListener;
    private CBLoopPagerAdapterWrapper mAdapter;
    private boolean mBoundaryCaching = true;

    private boolean isCanScroll = true;
    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousOffset = -1.0F;
        private float mPreviousPosition = -1.0F;

        public void onPageSelected(int position) {
            int realPosition = CBLoopViewPager.this.mAdapter.toRealPosition(position);
            if(this.mPreviousPosition != (float)realPosition) {
                this.mPreviousPosition = (float)realPosition;
                if(CBLoopViewPager.this.mOuterPageChangeListener != null) {
                    CBLoopViewPager.this.mOuterPageChangeListener.onPageSelected(realPosition);
                }
            }

        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int realPosition = position;
            if(CBLoopViewPager.this.mAdapter != null) {
                realPosition = CBLoopViewPager.this.mAdapter.toRealPosition(position);
                if(positionOffset == 0.0F && this.mPreviousOffset == 0.0F && (position == 0 || position == CBLoopViewPager.this.mAdapter.getCount() - 1)) {
                    CBLoopViewPager.this.setCurrentItem(realPosition, false);
                }
            }

            this.mPreviousOffset = positionOffset;
            if(CBLoopViewPager.this.mOuterPageChangeListener != null) {
                if(realPosition != CBLoopViewPager.this.mAdapter.getRealCount() - 1) {
                    CBLoopViewPager.this.mOuterPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                } else if((double)positionOffset > 0.5D) {
                    CBLoopViewPager.this.mOuterPageChangeListener.onPageScrolled(0, 0.0F, 0);
                } else {
                    CBLoopViewPager.this.mOuterPageChangeListener.onPageScrolled(realPosition, 0.0F, 0);
                }
            }

        }

        public void onPageScrollStateChanged(int state) {
            if(CBLoopViewPager.this.mAdapter != null) {
                int position = CBLoopViewPager.super.getCurrentItem();
                int realPosition = CBLoopViewPager.this.mAdapter.toRealPosition(position);
                if(state == 0 && (position == 0 || position == CBLoopViewPager.this.mAdapter.getCount() - 1)) {
                    CBLoopViewPager.this.setCurrentItem(realPosition, false);
                }
            }

            if(CBLoopViewPager.this.mOuterPageChangeListener != null) {
                CBLoopViewPager.this.mOuterPageChangeListener.onPageScrollStateChanged(state);
            }

        }
    };

    public boolean isCanScroll() {
        return this.isCanScroll;
    }

    public void setCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return this.isCanScroll?super.onTouchEvent(ev):false;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.isCanScroll?super.onInterceptTouchEvent(ev):false;
    }

    public static int toRealPosition(int position, int count) {
        --position;
        if(position < 0) {
            position += count;
        } else {
            position %= count;
        }

        return position;
    }

    public void setBoundaryCaching(boolean flag) {
        this.mBoundaryCaching = flag;
        if(this.mAdapter != null) {
            this.mAdapter.setBoundaryCaching(flag);
        }

    }

    public void setAdapter(PagerAdapter adapter) {
        this.mAdapter = new CBLoopPagerAdapterWrapper(adapter);
        this.mAdapter.setBoundaryCaching(this.mBoundaryCaching);
        super.setAdapter(this.mAdapter);
        this.setCurrentItem(0, false);
    }

    public PagerAdapter getAdapter() {
        return (PagerAdapter)(this.mAdapter != null?this.mAdapter.getRealAdapter():this.mAdapter);
    }

    public int getCurrentItem() {
        return this.mAdapter != null?this.mAdapter.toRealPosition(super.getCurrentItem()):0;
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        int realItem = 0;

        try {
            realItem = this.mAdapter.toInnerPosition(item);
        } catch (NullPointerException var5) {
            var5.printStackTrace();
        }

        super.setCurrentItem(realItem, smoothScroll);
    }

    public void setCurrentItem(int item) {
        if(this.getCurrentItem() != item) {
            this.setCurrentItem(item, true);
        }

    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mOuterPageChangeListener = listener;
    }

    public CBLoopViewPager(Context context) {
        super(context);
        this.init();
    }

    public CBLoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    private void init() {
        super.setOnPageChangeListener(this.onPageChangeListener);
    }
}
