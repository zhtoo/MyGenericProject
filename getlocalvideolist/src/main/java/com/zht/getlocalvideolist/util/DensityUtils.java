package com.zht.getlocalvideolist.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

/**
 * @author： Tom on 2018/1/13 16:01
 * @email： 820159571@qq.com
 *  
 */
public class DensityUtils {






    /**
     * 获取屏幕像素
     *
     * @return
     */
    public static int getWidthPixels(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Configuration cf = context.getResources().getConfiguration();
        int ori = cf.orientation;
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
            return displayMetrics.heightPixels;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {// 竖屏
            return displayMetrics.widthPixels;
        }
        return 0;
    }

}
