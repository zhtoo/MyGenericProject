package com.zht.banner.banner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 作者：zhanghaitao on 2017/12/20 16:19
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class CBPageAdapter<T> extends RecyclingPagerAdapter {
    protected List<T> mData;
    protected CBViewHolderCreator holderCreator;

    public CBPageAdapter(CBViewHolderCreator holderCreator, List<T> datas) {
        this.holderCreator = holderCreator;
        this.mData = datas;
    }

    public View getView(int position, View view, ViewGroup container) {
        CBPageAdapter.Holder holder;
        if(view == null) {
            holder = (CBPageAdapter.Holder)this.holderCreator.createHolder();
            view = holder.createView(container.getContext());
            view.setTag(holder);
        } else {
            holder = (CBPageAdapter.Holder)view.getTag();
        }

        if(this.mData != null && !this.mData.isEmpty()) {
            holder.UpdateUI(container.getContext(), position, this.mData.get(position));
        }

        return view;
    }

    public int getCount() {
        return this.mData == null?0:this.mData.size();
    }

    public interface Holder<T> {
        View createView(Context var1);

        void UpdateUI(Context var1, int var2, T var3);
    }
}
