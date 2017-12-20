package com.zht.banner;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.zht.banner.banner.CBViewHolderCreator;
import com.zht.banner.banner.ConvenientBanner;
import com.zht.banner.banner.NetworkImageHolderView;
import com.zht.banner.banner.RBannerBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：zhanghaitao on 2017/12/20 16:12
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class MainActivity extends AppCompatActivity {
    private ConvenientBanner banner;
    private List<RBannerBean> networkImages = new ArrayList();

    public MainActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        for(int wm = 0; wm < 3; ++wm) {
            RBannerBean bean = new RBannerBean();
            bean.setPicUrl("");
            bean.setPicPath("");
            this.networkImages.add(bean);
        }

        WindowManager var6 = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE/*"window"*/);
        int mWidth = var6.getDefaultDisplay().getWidth();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, (int)((double)mWidth / 2.5D));
        this.banner = (ConvenientBanner)this.findViewById(R.id.banner);
        this.banner.setLayoutParams(params);
        this.banner.setPages(new CBViewHolderCreator() {
            public NetworkImageHolderView createHolder() {
                return new NetworkImageHolderView() {
                    protected void itemOnClick(int position) {
                    }
                };
            }
        }, this.networkImages).setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused});
    }

    protected void onResume() {
        super.onResume();
        this.banner.startTurning(3000L);
    }

    protected void onStop() {
        super.onStop();
        this.banner.stopTurning();
    }
}
