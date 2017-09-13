package com.fcott.spadger.model.entity;

import com.fcott.spadger.model.bean.MovieBean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2017/9/6.
 */

public class Post extends BmobObject implements Serializable{
    //0：normal  1：电影分享
    public static final Integer TYPE_NORMAL = 0;
    public static final Integer TYPE_AV = 1;

    private Integer type;

    private String title;//帖子标题

    private String content;// 帖子内容

    private MovieBean.MessageBean.MoviesBean moviesBean;//分享的电影

    private User author;//帖子的发布者，这里体现的是一对一的关系，该帖子属于某个用户

    private BmobFile image;//帖子图片

    private BmobRelation likes;//多对多关系：用于存储喜欢该帖子的所有用户

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public MovieBean.MessageBean.MoviesBean getMoviesBean() {
        return moviesBean;
    }

    public void setMoviesBean(MovieBean.MessageBean.MoviesBean moviesBean) {
        this.moviesBean = moviesBean;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}