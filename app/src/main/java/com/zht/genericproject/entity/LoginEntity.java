package com.zht.genericproject.entity;

/**
 * 作者：zhanghaitao on 2017/12/20 15:26
 * 邮箱：820159571@qq.com
 *
 * @describe:保存登录信息的类
 */

public class LoginEntity {
    /**
     * 第一次登录时间
     */
    private String loginFirstTime;
    /**
     * 第一次进入APP的时间
     */
    private String loginAppFirstTime;
    /**
     * 最后登录时间，在退出APP后记录：
     * 1、正常退出：ActivityUtils.onExit();
     * 2、异常退出：CrashHandler.uncaughtException()--->ActivityUtils.onExit()（还是要走的这个方法）
     */
    private String loginLastTime;

    /**
     * 用户账号
     */
    private String userName = "user_name";

    /**
     * 用户昵称
     */
    private String userNickname= "user_nickname";
    /**
     * 用户头像地址（需要联网获取）
     */
    private String userIconUrl = "user_icon_url";
    /**
     * 用户ID（需要联网获取）
     */
    private String userId = "user_id";
    /**
     * 用户Token（需要联网获取,临时存储）
     * 最简单的token组成:
     * uid(用户唯一的身份标识)、
     * time(当前时间的时间戳)、
     * sign(签名，由token的前几位+以哈希算法压缩成一定长的十六进制字符串，可以防止恶意第三方拼接token请求服务器)
     */
    private String userToken;


}
