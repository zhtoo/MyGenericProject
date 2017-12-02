package com.zht.genericproject.util;

import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;

/**
 * Description: 类型转换器
 */
public class Converter {
    /**
     * 从字符串中获取 long 型，如果字符串为空，则返回0
     */
    public static long getLong(String args) {
        if (TextUtils.isEmpty(args)) {
            return 0L;
        } else {
            return Long.valueOf(args);
        }
    }

    /**
     * 从字符串中获取 double 型，如果字符串为空，则返回0
     */
    public static double getDouble(String args) {
        if (TextUtils.isEmpty(args)) {
            return 0.0;
        } else {
            return Double.valueOf(args);
        }
    }

    /**
     * 从字符串中获取 int 型，如果字符串为空，则返回0
     */
    public static int getInteger(String args) {
        if (TextUtils.isEmpty(args)) {
            return 0;
        } else {
            return Integer.valueOf(args);
        }
    }

    /**
     * 从字符串中获取 float 型，如果字符串为空，则返回0
     */
    public static float getFloat(String args) {
        if (TextUtils.isEmpty(args)) {
            return 0.0f;
        } else {
            return Float.valueOf(args);
        }
    }

    /**
     * 将一个整数转换成一个Drawable对象
     */
    public static ColorDrawable convertColorToDrawable(int color) {
        return new ColorDrawable(color);
    }
}
