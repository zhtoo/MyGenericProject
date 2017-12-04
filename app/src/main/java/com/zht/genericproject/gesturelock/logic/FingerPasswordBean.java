package com.zht.genericproject.gesturelock.logic;

/**
 * Description: 手势密码信息持久化类
 */
public class FingerPasswordBean {
    /**
     * 手势密码
     */
    private String password;
    /**
     * 错误次数
     */
    private int    errInputCount;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getErrInputCount() {
        return errInputCount;
    }

    public void setErrInputCount(int errInputCount) {
        this.errInputCount = errInputCount;
    }
}
