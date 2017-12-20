package com.zht.banner.banner;

import android.database.DataSetObserver;
import android.os.Parcelable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * 作者：zhanghaitao on 2017/12/20 16:17
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class CBLoopPagerAdapterWrapper extends PagerAdapter {
    private PagerAdapter mAdapter;
    private SparseArray<ToDestroy> mToDestroy = new SparseArray();
    private boolean mBoundaryCaching;

    void setBoundaryCaching(boolean flag) {
        this.mBoundaryCaching = flag;
    }

    CBLoopPagerAdapterWrapper(PagerAdapter adapter) {
        this.mAdapter = adapter;
        adapter.registerDataSetObserver(new DataSetObserver() {
            public void onChanged() {
                CBLoopPagerAdapterWrapper.this.notifyDataSetChanged();
            }
        });
    }

    public void notifyDataSetChanged() {
        this.mToDestroy = new SparseArray();
        super.notifyDataSetChanged();
    }

    int toRealPosition(int position) {
        int realCount = this.getRealCount();
        if(realCount == 0) {
            return 0;
        } else {
            int realPosition = (position - 1) % realCount;
            if(realPosition < 0) {
                realPosition += realCount;
            }

            return realPosition;
        }
    }

    public int toInnerPosition(int realPosition) {
        return realPosition + 1;
    }

    private int getRealFirstPosition() {
        return 1;
    }

    private int getRealLastPosition() {
        return this.getRealFirstPosition() + this.getRealCount() - 1;
    }

    public int getCount() {
        return this.mAdapter.getCount() + 2;
    }

    public int getRealCount() {
        return this.mAdapter.getCount();
    }

    public PagerAdapter getRealAdapter() {
        return this.mAdapter;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = !(this.mAdapter instanceof FragmentPagerAdapter) && !(this.mAdapter instanceof FragmentStatePagerAdapter)?this.toRealPosition(position):position;
        if(this.mBoundaryCaching) {
            CBLoopPagerAdapterWrapper.ToDestroy toDestroy = (CBLoopPagerAdapterWrapper.ToDestroy)this.mToDestroy.get(position);
            if(toDestroy != null) {
                this.mToDestroy.remove(position);
                return toDestroy.object;
            }
        }

        return this.mAdapter.instantiateItem(container, realPosition);
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        int realFirst = this.getRealFirstPosition();
        int realLast = this.getRealLastPosition();
        int realPosition = !(this.mAdapter instanceof FragmentPagerAdapter) && !(this.mAdapter instanceof FragmentStatePagerAdapter)?this.toRealPosition(position):position;
        if(!this.mBoundaryCaching || position != realFirst && position != realLast) {
            this.mAdapter.destroyItem(container, realPosition, object);
        } else {
            this.mToDestroy.put(position, new CBLoopPagerAdapterWrapper.ToDestroy(container, realPosition, object));
        }

    }

    public void finishUpdate(ViewGroup container) {
        this.mAdapter.finishUpdate(container);
    }

    public boolean isViewFromObject(View view, Object object) {
        return this.mAdapter.isViewFromObject(view, object);
    }

    public void restoreState(Parcelable bundle, ClassLoader classLoader) {
        this.mAdapter.restoreState(bundle, classLoader);
    }

    public Parcelable saveState() {
        return this.mAdapter.saveState();
    }

    public void startUpdate(ViewGroup container) {
        this.mAdapter.startUpdate(container);
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        this.mAdapter.setPrimaryItem(container, position, object);
    }

    static class ToDestroy {
        ViewGroup container;
        int position;
        Object object;

        public ToDestroy(ViewGroup container, int position, Object object) {
            this.container = container;
            this.position = position;
            this.object = object;
        }
    }
}
