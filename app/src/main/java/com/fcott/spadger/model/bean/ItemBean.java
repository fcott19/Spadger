package com.fcott.spadger.model.bean;

/**
 * Created by Administrator on 2016/12/27.
 */

public class ItemBean{
    public String title;
    public String url;

    public ItemBean(String title,String url){
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
