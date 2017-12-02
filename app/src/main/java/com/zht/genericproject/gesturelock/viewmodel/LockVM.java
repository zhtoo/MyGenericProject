package com.zht.genericproject.gesturelock.viewmodel;


import android.view.View;

import java.util.Calendar;

/**
 * Description: 手势密码登录管理类
 */
public class LockVM {
    private String nickname;
    private String locktime;
    private String avatarUrl;
    public LockVM() {
//        OauthTokenMo mo = SharedInfo.getInstance().getValue(OauthTokenMo.class);
//        if (null != mo) {
//            nickname = mo.getHideUserName();
//            avatarUrl=mo.getAvatarUrl();
//        }
        getLockTime();
    }
   /**
    *  dec:获取当前时间
    */
    public void getLockTime(){
        Calendar calendar= Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        locktime=year+"-"+month+"-"+day;
    }
    /**
     * 忘记手势密码
     */
    public void forgetPwd(View view) {
//        UserLogic.signOutToLogin(view.getContext().getString(R.string.lock_forgot_prompt), true);
    }

    /**
     * 其他用户登录
     */
    public void otherUserLogin(View view) {
//        UserLogic.signOutToLogin(view.getContext().getString(R.string.lock_visitor_prompt), false);
    }

//    @Bindable
//    public String getNickname() {
//        return nickname;
//    }
//
//    public void setNickname(String nickname) {
//        this.nickname = nickname;
//        notifyPropertyChanged(BR.nickname);
//    }
//    @Bindable
//    public String getLocktime() {
//        return locktime;
//    }
//
//    public void setLocktime(String locktime) {
//        this.locktime = locktime;
//    }
//    @Bindable
//    public String getAvatarUrl() {
//        return avatarUrl;
//    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

}
