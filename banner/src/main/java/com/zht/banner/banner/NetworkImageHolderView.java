package com.zht.banner.banner;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zht.banner.R;
import com.zht.banner.banner.CBPageAdapter.Holder;

/**
 * 作者：zhanghaitao on 2017/12/20 16:22
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public abstract class NetworkImageHolderView implements Holder<RBannerBean> {
    private ImageView imageView;

    protected abstract void itemOnClick(int var1);

    public NetworkImageHolderView() {
    }

    public View createView(Context context) {
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return this.imageView;
    }

    public void UpdateUI(Context context, final int position, RBannerBean data) {
        Glide.with(context)
                .load(data.getPicUrl())
                .placeholder(R.mipmap.banner_default)
                .into(this.imageView);
        this.imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NetworkImageHolderView.this.itemOnClick(position);
            }
        });
    }
}