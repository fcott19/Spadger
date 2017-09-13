package com.fcott.spadger.model.entity;

import com.fcott.spadger.model.bean.MovieBean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/9/11.
 */

public class AvCollections extends BmobObject implements Serializable {
    private MovieBean.MessageBean.MoviesBean moviesBean;
    private User owner;

    public MovieBean.MessageBean.MoviesBean getMoviesBean() {
        return moviesBean;
    }

    public void setMoviesBean(MovieBean.MessageBean.MoviesBean moviesBean) {
        this.moviesBean = moviesBean;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
