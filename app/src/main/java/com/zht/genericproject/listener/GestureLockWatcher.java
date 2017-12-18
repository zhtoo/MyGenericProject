package com.zht.genericproject.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Description: OS功能按键监听
 */
public class GestureLockWatcher {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private IntentFilter mFilter;
    private OnScreenPressedListener mListener;
    private InnerReceiver           mReceiver;

    // 回调接口
    public interface OnScreenPressedListener {
        void onPressed();
    }

    public GestureLockWatcher(Context context) {
        mContext = context;
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_SCREEN_ON);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mFilter.addAction(Intent.ACTION_USER_PRESENT);
        mFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    /**
     * 设置监听
     */
    public void setOnScreenPressedListener(OnScreenPressedListener listener) {
        mListener = listener;
        mReceiver = new InnerReceiver();
    }

    /**
     * 开始监听，注册广播
     */
    public void startWatch() {
        if (mReceiver != null) {
            mContext.registerReceiver(mReceiver, mFilter);
        }
    }

    /**
     * 停止监听，注销广播
     */
    public void stopWatch() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

    /**
     * 广播接收者
     */
    class InnerReceiver extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY            = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS    = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY       = "homekey";
        final String SYSTEM_DIALOG_REASON_LOCK           = "lock";
        final String SYSTEM_DIALOG_REASON_ASSIST         = "assist";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mListener != null) {
                String action = intent.getAction();
                if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) { // Home
                    mListener.onPressed();
                } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
                    mListener.onPressed();
                } else if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
                } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
                }
            }

            // if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            // String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            // if (reason != null) {
            // Logger.e(TAG, "action:" + action + ",reason:" + reason);
            // if (mListener != null) {
            // if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
            // // 短按Home键
            // Logger.i(TAG, "短按Home键");
            // } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
            // // 长按Home键 或者 activity切换键
            // Logger.i(TAG, "长按Home键 或者 activity切换键");
            // } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
            // // 锁屏
            // Logger.i(TAG, "锁屏");
            // } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
            // // samsung 长按Home键
            // Logger.i(TAG, " 长按Home键");
            // }
            // }
            // }
            // }
        }
    }
}
