package com.zht.genericproject.util.ref;

/**
 * 反射异常类
 */
public class RefException extends Exception {
    private static final long serialVersionUID = 8248853556846348815L;

    public RefException() {
        super();
    }

    public RefException(String detailMessage) {
        super(detailMessage);
    }

    public RefException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RefException(Throwable throwable) {
        super(throwable);
    }
}
