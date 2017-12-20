package com.zht.banner.banner;

import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * 作者：zhanghaitao on 2017/12/20 16:19
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class CBPageChangeListener implements ViewPager.OnPageChangeListener {
    private ArrayList<ImageView> pointViews;
    private int[] page_indicatorId;

    public CBPageChangeListener(ArrayList<ImageView> pointViews, int[] page_indicatorId) {
        this.pointViews = pointViews;
        this.page_indicatorId = page_indicatorId;
    }

    public void onPageScrollStateChanged(int state) {
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageSelected(int index) {
        for(int i = 0; i < this.pointViews.size(); ++i) {
            ((ImageView)this.pointViews.get(index)).setImageResource(this.page_indicatorId[1]);
            if(index != i) {
                ((ImageView)this.pointViews.get(i)).setImageResource(this.page_indicatorId[0]);
            }
        }

    }
}
