package com.zht.banner.banner;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.zht.banner.banner.CBPageAdapter.Holder;
/**
 * 作者：zhanghaitao on 2017/12/20 16:21
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class LocalImageHolderView implements Holder<Integer> {
    private ImageView imageView;

    public LocalImageHolderView() {
    }

    public View createView(Context context) {
        this.imageView = new ImageView(context);
        this.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return this.imageView;
    }

    public void UpdateUI(Context context, final int position, Integer data) {
        this.imageView.setImageResource(data.intValue());
        this.imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
