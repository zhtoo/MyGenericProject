package com.zht.genericproject.util.statistics;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

/**
 * Description: 手机设备信息
 */
public class DeviceInfo {

    /** 全局唯一标识符（安卓传IMEI，IOS传UDID） */
    private String dev_guid;
    /** 终端设备ip */
    private String dev_add_ip;
    /** 终端设备机型 */
    private String dev_model;
    /** 设备操作系统类型 */
    private String dev_os;
    /** 设备操作系统版本号 */
    private String dev_version;
    /** App应用id（安卓传应用包名，IOS传BundleID） */
    private String app_id;
    /** 渠道id */
    private String app_channel_id;
    /** 应用版本号（版本号+Build号） */
    private String app_version;
    /** 操作模块 */
    private String app_module;
    /** 操作功能 */
    private String app_func;
    /** 操作类型 */
    private String app_type;
    /** 备注（可为空） */
    private String app_memo;

    /** 操作用户id(可为空) */
    private String app_user_id;
    /** 操作用户用(可为空) */
    private String app_user_name;


    public DeviceInfo(Context context) throws NameNotFoundException {
        super();
        this.dev_guid = DeviceInfoUtils.getIMEI(context);
        this.dev_add_ip = DeviceInfoUtils.getIP(context);
        this.dev_model = Build.MANUFACTURER + " - " + Build.BRAND + " - " + Build.MODEL;
        this.dev_os = Build.DISPLAY;
        this.dev_version = Build.VERSION.RELEASE + " - " + Build.VERSION.SDK_INT;
        this.app_id = context.getPackageName();
        this.app_channel_id = DeviceInfoUtils.getAppMetaData(context, "UMENG_CHANNEL");
        this.app_version = DeviceInfoUtils.getVersionName(context) + " - " + DeviceInfoUtils.getVersionCode(context);
    }

    public String getDev_guid() {
        return dev_guid;
    }

    public String getDev_add_ip() {
        return dev_add_ip;
    }

    public String getDev_model() {
        return dev_model;
    }

    public String getDev_os() {
        return dev_os;
    }

    public String getDev_version() {
        return dev_version;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getApp_channel_id() {
        return app_channel_id;
    }

    public String getApp_version() {
        return app_version;
    }

    public String getApp_module() {
        return app_module;
    }

    public void setApp_module(String app_module) {
        this.app_module = app_module;
    }

    public String getApp_func() {
        return app_func;
    }

    public void setApp_func(String app_func) {
        this.app_func = app_func;
    }

    public String getApp_type() {
        return app_type;
    }

    public void setApp_type(String app_type) {
        this.app_type = app_type;
    }

    public String getApp_memo() {
        return app_memo;
    }

    public void setApp_memo(String app_memo) {
        this.app_memo = app_memo;
    }

    public String getApp_user_id() {
        return app_user_id;
    }

    public void setApp_user_id(String app_user_id) {
        this.app_user_id = app_user_id;
    }

    public String getApp_user_name() {
        return app_user_name;
    }

    public void setApp_user_name(String app_user_name) {
        this.app_user_name = app_user_name;
    }
}
