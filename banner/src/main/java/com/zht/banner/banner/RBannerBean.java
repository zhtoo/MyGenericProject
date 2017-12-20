package com.zht.banner.banner;

/**
 * 作者：zhanghaitao on 2017/12/20 16:22
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class RBannerBean {
    private String picUrl;
    private String picPath;
    private String introduction;

    public RBannerBean() {
    }

    public String getIntroduction() {
        return this.introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPicPath() {
        return this.picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getPicUrl() {
        return this.picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
}
