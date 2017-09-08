package com.fcott.spadger.model.entity;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/9/6.
 */

public class Comment extends BmobObject {

    private String content;//评论内容

    private User author;//评论的用户，Pointer类型，一对一关系

    private Post post; //所评论的帖子，这里体现的是一对多的关系，一个评论只能属于一个微博

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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}