package com.zht.genericproject.util;

import android.widget.Toast;

/**
 * 连续弹的吐司
 */
public class ToastUtil {
    private static Toast toast;

    public static void showToast(String text) {
        if (toast == null) {
            toast = Toast.makeText(ActivityUtils.peek(), text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);//如果不为空，则直接改变当前toast的文本
        }
        toast.show();
    }
}
