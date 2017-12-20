/**
 * banner模型
 */
package com.zht.genericproject.fragment;

/**
 * @author yoseflin
 *         banner模型
 */
public class BannerModel {
    /**
     * 图片URL
     */
    private String imageUrl;
    /**
     * 链接URL
     */
    private String link;
    /**
     * 内容标题
     */
    private String title;
    /**
     * 跑马灯内容添加的时间
     */
    private String addtime;
    /**
     * 跑马灯内容添加的链接
     */
    private String articleUrl;

    //--------------------------华丽分割线----------------------------------


    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
