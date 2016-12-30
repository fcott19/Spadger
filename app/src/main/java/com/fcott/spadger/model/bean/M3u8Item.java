package com.fcott.spadger.model.bean;

/**
 * Created by Administrator on 2016/12/30.
 */

public class M3u8Item{
    private String url;
    private double time;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public M3u8Item(String url, String time){
        this.time = Double.valueOf(time);
        this.url = url;
    }
}
