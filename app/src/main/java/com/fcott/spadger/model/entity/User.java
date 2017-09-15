package com.fcott.spadger.model.entity;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2017/9/6.
 */

public class User extends BmobUser implements Serializable{

    private String headImage;
    private String nickName;
    private BmobRelation avCollections;
    private BmobRelation likePosts;

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public BmobRelation getAvCollections() {
        return avCollections;
    }

    public void setAvCollections(BmobRelation avCollections) {
        this.avCollections = avCollections;
    }

    public BmobRelation getLikePosts() {
        return likePosts;
    }

    public void setLikePosts(BmobRelation likePosts) {
        this.likePosts = likePosts;
    }
}
