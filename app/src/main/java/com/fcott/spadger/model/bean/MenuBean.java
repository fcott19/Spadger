package com.fcott.spadger.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/27.
 */

public class MenuBean implements Parcelable {

    private ArrayList<ItemBean> vedioList = new ArrayList();//电影菜单列表
    private ArrayList<ItemBean> picList = new ArrayList();//图片菜单列表
    private ArrayList<ItemBean> novelList = new ArrayList();//小说菜单列表

    private ArrayList<ItemBean> newVedioList = new ArrayList();//最新电影列表
    private ArrayList<ItemBean> newpicList = new ArrayList();//最新图片列表
    private ArrayList<ItemBean> newNovelList = new ArrayList();//最新小说列表

    protected MenuBean(Parcel in) {
        vedioList = in.createTypedArrayList(ItemBean.CREATOR);
        picList = in.createTypedArrayList(ItemBean.CREATOR);
        novelList = in.createTypedArrayList(ItemBean.CREATOR);
        newVedioList = in.createTypedArrayList(ItemBean.CREATOR);
        newpicList = in.createTypedArrayList(ItemBean.CREATOR);
        newNovelList = in.createTypedArrayList(ItemBean.CREATOR);
    }

    public MenuBean(){

    }

    public static final Creator<MenuBean> CREATOR = new Creator<MenuBean>() {
        @Override
        public MenuBean createFromParcel(Parcel in) {
            return new MenuBean(in);
        }

        @Override
        public MenuBean[] newArray(int size) {
            return new MenuBean[size];
        }
    };

    public ArrayList<ItemBean> getNewVedioList() {
        return newVedioList;
    }

    public void setNewVedioList(ArrayList<ItemBean> newVedioList) {
        this.newVedioList = newVedioList;
    }

    public ArrayList<ItemBean> getNewpicList() {
        return newpicList;
    }

    public void setNewpicList(ArrayList<ItemBean> newpicList) {
        this.newpicList = newpicList;
    }

    public ArrayList<ItemBean> getNewNovelList() {
        return newNovelList;
    }

    public void setNewNovelList(ArrayList<ItemBean> newNovelList) {
        this.newNovelList = newNovelList;
    }

    public ArrayList<ItemBean> getVedioList() {
        return vedioList;
    }

    public void setVedioList(ArrayList<ItemBean> vedioList) {
        this.vedioList = vedioList;
    }

    public ArrayList<ItemBean> getPicList() {
        return picList;
    }

    public void setPicList(ArrayList<ItemBean> picList) {
        this.picList = picList;
    }

    public ArrayList<ItemBean> getNovelList() {
        return novelList;
    }

    public void setNovelList(ArrayList<ItemBean> novelList) {
        this.novelList = novelList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(vedioList);
        dest.writeTypedList(picList);
        dest.writeTypedList(novelList);
        dest.writeTypedList(newVedioList);
        dest.writeTypedList(newpicList);
        dest.writeTypedList(newNovelList);
    }
}
