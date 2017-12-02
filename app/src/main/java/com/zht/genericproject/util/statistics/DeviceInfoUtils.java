package com.zht.genericproject.util.statistics;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 手机设备信息手机工具类
 */
public class DeviceInfoUtils {
    /**
     * 获得IMEI号
     */
    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            String imei = tm.getDeviceId() != null ? tm.getDeviceId() : "";
            if (imei.equals("0")) {
                imei = "000000000000000";
            }
            int len = 15 - imei.length();
            for (int i = 0; i < len; i++) {
                imei += "0";
            }
            return imei;
        }
        return "";
    }

    /**
     * 获取IP地址
     */
    public static String getIP(Context context) {
        int    WIFI_IP = getWIFIIP(context);
        String GPRS_IP = getGPRSIP();
        String ip      = "0.0.0.0";
        if (WIFI_IP != 0) {
            ip = intToIP(WIFI_IP);
        } else if (!TextUtils.isEmpty(GPRS_IP)) {
            ip = GPRS_IP;
        }
        return ip;
    }

    /**
     * 返回当前版本号名称，例如：V1.0
     */
    public static String getVersionName(Context context) {
        // 默认为1.0
        String ret_val = "1.0";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info    = manager.getPackageInfo(context.getPackageName(), 0);
            ret_val = substringNumFormat(info.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_val;
    }

    /**
     * 返回当前版本号的值，例如：1
     */
    public static int getVersionCode(Context context) {
        // 默认为1
        int ret_val = 1;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info    = manager.getPackageInfo(context.getPackageName(), 0);
            ret_val = info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret_val;
    }
    /**
     * 获取渠道编号
     */
    public  static String getChannelName(Context context){
      PackageManager pm=context.getPackageManager();
        String data="";
        try {
        ApplicationInfo info= pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if(info.metaData!=null){
                data=info.metaData.getString("UMENG_CHANNEL");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
     return data;
    }
    /**
     * 获取AndroidManifest.xml中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值, 或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context context, String key) {
        if (context == null || TextUtils.isEmpty(key)) {
            return "";
        }
        String resultData = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        if (applicationInfo.metaData.get(key) != null) {
                            resultData = applicationInfo.metaData.get(key).toString();
                        }
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return resultData;
    }

    /**
     * 获得wifi的IP地址
     *
     * 需要添加权限
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
     <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
     */
    private static int getWIFIIP(Context context) {
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getIpAddress();
    }

    /**
     * 整型IP地址转成String的
     */
    private static String intToIP(int IPAddress) {
        return (IPAddress & 0xFF) + "." + ((IPAddress >> 8) & 0xFF) + "." + ((IPAddress >> 16) & 0xFF) + "." + (IPAddress >> 24 & 0xFF);
    }

    /**
     * 获取数据网络的IP地址
     */
    private static String getGPRSIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> addresses = networkInterface.getInetAddresses(); addresses.hasMoreElements(); ) {
                    InetAddress inetAddress = addresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 截取字符串中的数值部分
     */
    public static String substringNumFormat(String params) {
        Matcher matcher = Pattern.compile("[0-9,.%]+").matcher(params);
        if (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            return params.substring(start, end);
        } else {
            return "0";
        }
    }

}
