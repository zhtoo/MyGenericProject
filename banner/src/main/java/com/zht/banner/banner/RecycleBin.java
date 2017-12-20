package com.zht.banner.banner;

import android.os.Build;
import android.util.SparseArray;
import android.view.View;

/**
 * 作者：zhanghaitao on 2017/12/20 16:23
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class RecycleBin {
    private View[] activeViews = new View[0];
    private int[] activeViewTypes = new int[0];
    private SparseArray<View>[] scrapViews;
    private int viewTypeCount;
    private SparseArray<View> currentScrapViews;

    public RecycleBin() {
    }

    public void setViewTypeCount(int viewTypeCount) {
        if(viewTypeCount < 1) {
            throw new IllegalArgumentException("Can\'t have a viewTypeCount < 1");
        } else {
            SparseArray[] scrapViews = new SparseArray[viewTypeCount];

            for(int i = 0; i < viewTypeCount; ++i) {
                scrapViews[i] = new SparseArray();
            }

            this.viewTypeCount = viewTypeCount;
            this.currentScrapViews = scrapViews[0];
            this.scrapViews = scrapViews;
        }
    }

    protected boolean shouldRecycleViewType(int viewType) {
        return viewType >= 0;
    }

    View getScrapView(int position, int viewType) {
        return this.viewTypeCount == 1?retrieveFromScrap(this.currentScrapViews, position):(viewType >= 0 && viewType < this.scrapViews.length?retrieveFromScrap(this.scrapViews[viewType], position):null);
    }

    void addScrapView(View scrap, int position, int viewType) {
        if(this.viewTypeCount == 1) {
            this.currentScrapViews.put(position, scrap);
        } else {
            this.scrapViews[viewType].put(position, scrap);
        }

        if(Build.VERSION.SDK_INT >= 14) {
            scrap.setAccessibilityDelegate((View.AccessibilityDelegate)null);
        }

    }

    void scrapActiveViews() {
        View[] activeViews = this.activeViews;
        int[] activeViewTypes = this.activeViewTypes;
        boolean multipleScraps = this.viewTypeCount > 1;
        SparseArray scrapViews = this.currentScrapViews;
        int count = activeViews.length;

        for(int i = count - 1; i >= 0; --i) {
            View victim = activeViews[i];
            if(victim != null) {
                int whichScrap = activeViewTypes[i];
                activeViews[i] = null;
                activeViewTypes[i] = -1;
                if(this.shouldRecycleViewType(whichScrap)) {
                    if(multipleScraps) {
                        scrapViews = this.scrapViews[whichScrap];
                    }

                    scrapViews.put(i, victim);
                    if(Build.VERSION.SDK_INT >= 14) {
                        victim.setAccessibilityDelegate((View.AccessibilityDelegate)null);
                    }
                }
            }
        }

        this.pruneScrapViews();
    }

    private void pruneScrapViews() {
        int maxViews = this.activeViews.length;
        int viewTypeCount = this.viewTypeCount;
        SparseArray[] scrapViews = this.scrapViews;

        for(int i = 0; i < viewTypeCount; ++i) {
            SparseArray scrapPile = scrapViews[i];
            int size = scrapPile.size();
            int extras = size - maxViews;
            --size;

            for(int j = 0; j < extras; ++j) {
                scrapPile.remove(scrapPile.keyAt(size--));
            }
        }

    }

    static View retrieveFromScrap(SparseArray<View> scrapViews, int position) {
        int size = scrapViews.size();
        if(size > 0) {
            int index;
            for(index = 0; index < size; ++index) {
                int r = scrapViews.keyAt(index);
                View view = (View)scrapViews.get(r);
                if(r == position) {
                    scrapViews.remove(r);
                    return view;
                }
            }

            index = size - 1;
            View var6 = (View)scrapViews.valueAt(index);
            scrapViews.remove(scrapViews.keyAt(index));
            return var6;
        } else {
            return null;
        }
    }
}
