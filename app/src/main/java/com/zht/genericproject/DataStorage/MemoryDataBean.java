package com.zht.genericproject.DataStorage;

/**
 * 作者：zhanghaitao on 2017/12/5 11:08
 * 邮箱：820159571@qq.com
 *
 * @describe:缓存在内存中的数据，通过SharedInfo存放到SoftMap中。
 */

public class MemoryDataBean {

    /**---------------登录信息------------------------------------------*/

    /**授权有效时间，单位秒*/
    private String expiresIn;
    /** 授权口令*/
    private String oauthToken;
    /** 刷新授权口令*/
    private String refreshToken;
    /** 用户ID*/
    private String userId;
    /** 用户名*/
    private String username;
    /** 隐藏 用户名*/
    private String hideUserName;
    /**用户头像*/
    private String avatarUrl;




}
