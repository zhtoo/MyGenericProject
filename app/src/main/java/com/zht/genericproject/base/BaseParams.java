package com.zht.genericproject.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.zht.genericproject.util.ActivityUtils;

import java.io.File;

/**
 * 作者：zhanghaitao on 2017/12/1 17:08
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class BaseParams {

    //SP文件名
    public static final String SP_NAME = "my_generic_project";
    public static final String SP_IS_FIRST_INE = "is_first_in";
    /**
     * 加密是需要使用的密钥
     * DES加解密时KEY必须是16进制字符串,不可小于8位
     * E.G.    6C4E60E55552386C
     * <p/>
     * 3DES加解密时KEY必须是6进制字符串,不可小于24位
     * E.G.    6C4E60E55552386C759569836DC0F83869836DC0F838C0F7
     */
    public static final String SECRET_KEY = "6C4E60E55552386C759569836DC0F83869836DC0F838C0F7";

    /**
     * IMEI相关
     */
    public static final String SP_ACKAPPKEY = "ackAppkey";
    public static final String ACKTOKEN = "ackToken";















    /**
     * 获取SD卡的根目录
     */
    public static String getSDPath() {
        File sdDir = null;
        // 判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            // 获取跟目录
            sdDir = Environment.getExternalStorageDirectory();
        }
        if (sdDir == null) {
            return "";
        } else {
            return sdDir.toString();
        }
    }

    /**
     * 获取VersionCode
     */
    public static int getVersion() {
        try {
            Context context = ActivityUtils.peek();
            PackageManager pm      = context.getPackageManager();//context为当前Activity上下文
            PackageInfo pi      = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 1;
        }
    }

    /**
     * 获取VersionCode
     */
    public static String getVersionName() {
        try {
            Context        context = ActivityUtils.peek();
            PackageManager pm      = context.getPackageManager();//context为当前Activity上下文
            PackageInfo    pi      = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "--";
        }
    }


}
