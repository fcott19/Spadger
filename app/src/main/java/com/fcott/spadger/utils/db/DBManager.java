package com.fcott.spadger.utils.db;

/**
 * Created by Administrator on 2016/8/1.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fcott.spadger.Config;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.entity.Post;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    private DatabaseHelper helper;
    private SQLiteDatabase db;
    private String tableName;

    public DBManager(Context context) {
        this(context,DatabaseHelper.COLLECTION_TABLE);
    }

    public DBManager(Context context,String tableName) {
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
        this.tableName = tableName;
    }

    /**
     * add RowsBean
     */
    public void add(MovieBean.MessageBean.MoviesBean moviesBean) {
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            db.execSQL("INSERT INTO " + tableName
                    + " VALUES(null, ?, ?, ?, ?, ?, ?)", new Object[] { moviesBean.getMovieID(),
                    moviesBean.getName(), moviesBean.getDescription(),moviesBean.getCreateTime(),
                    moviesBean.getImg(),moviesBean.getCoverImg()});
            db.setTransactionSuccessful(); // 设置事务成功完成
        } finally {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * add RowsBean
     */
    public void add(Post post) {
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            db.execSQL("INSERT INTO " + tableName
                    + " VALUES(null,?)", new Object[] { post.getObjectId()});
            db.setTransactionSuccessful(); // 设置事务成功完成
        } finally {
            db.endTransaction(); // 结束事务
        }
    }
    /**
     * update Movie
     *
     * @param moviesBean
     */
    public void update(MovieBean.MessageBean.MoviesBean moviesBean)
    {
        ContentValues cv = new ContentValues();
        cv.put("MovieID", moviesBean.getMovieID());
        cv.put("Name", moviesBean.getName());
        cv.put("Description", moviesBean.getDescription());
        cv.put("CreateTime", moviesBean.getCreateTime());
        cv.put("Img", moviesBean.getImg());
        cv.put("CoverImg", moviesBean.getCoverImg());
        db.update(tableName, cv, "MovieID = ?",
                new String[] { moviesBean.getMovieID() });
    }

    /**
     * delete old Movie
     */
    public void deleteMovie(String movieId) {
        db.delete(tableName, "MovieID == ?",
                new String[]{movieId});
    }

    public void deletePost(String movieId) {
        db.delete(tableName, "ObjectId == ?",
                new String[]{movieId});
    }

    /**
     * query all persons, return list
     * @return List<Person>
     */
    public List<MovieBean.MessageBean.MoviesBean> query() {
        ArrayList<MovieBean.MessageBean.MoviesBean> beanList = new ArrayList<>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            MovieBean.MessageBean.MoviesBean moviesBean = new MovieBean.MessageBean.MoviesBean();
            moviesBean.setMovieID(c.getString(c.getColumnIndex("MovieID")));
            moviesBean.setName(c.getString(c.getColumnIndex("Name")));
            moviesBean.setDescription(c.getString(c.getColumnIndex("Description")));
            moviesBean.setCreateTime(c.getString(c.getColumnIndex("CreateTime")));
            moviesBean.setImg(c.getString(c.getColumnIndex("Img")));
            moviesBean.setCoverImg(c.getString(c.getColumnIndex("CoverImg")));
            beanList.add(moviesBean);
        }
        c.close();
        return beanList;
    }

    public List<MovieBean.MessageBean.MoviesBean> query(int pageIndex) {
        ArrayList<MovieBean.MessageBean.MoviesBean> beanList = new ArrayList<>();
        Cursor c = queryTheCursor();
        c.move((pageIndex - 1) * Config.NOMOR_PAGE_SIZE);
        while (c.moveToNext()) {
            MovieBean.MessageBean.MoviesBean moviesBean = new MovieBean.MessageBean.MoviesBean();
            moviesBean.setMovieID(c.getString(c.getColumnIndex("MovieID")));
            moviesBean.setName(c.getString(c.getColumnIndex("Name")));
            moviesBean.setDescription(c.getString(c.getColumnIndex("Description")));
            moviesBean.setCreateTime(c.getString(c.getColumnIndex("CreateTime")));
            moviesBean.setImg(c.getString(c.getColumnIndex("Img")));
            moviesBean.setCoverImg(c.getString(c.getColumnIndex("CoverImg")));
            beanList.add(moviesBean);
            if(beanList.size()==Config.NOMOR_PAGE_SIZE)
                break;
        }
        c.close();
        return beanList;
    }

    public List<MovieBean.MessageBean.MoviesBean> queryReverse(int pageIndex) {
        ArrayList<MovieBean.MessageBean.MoviesBean> beanList = new ArrayList<>();
        Cursor c = queryTheCursor();
        c.move(c.getCount()-((pageIndex - 1) * Config.NOMOR_PAGE_SIZE)+1);
        while (c.moveToPrevious()) {
            MovieBean.MessageBean.MoviesBean moviesBean = new MovieBean.MessageBean.MoviesBean();
            moviesBean.setMovieID(c.getString(c.getColumnIndex("MovieID")));
            moviesBean.setName(c.getString(c.getColumnIndex("Name")));
            moviesBean.setDescription(c.getString(c.getColumnIndex("Description")));
            moviesBean.setCreateTime(c.getString(c.getColumnIndex("CreateTime")));
            moviesBean.setImg(c.getString(c.getColumnIndex("Img")));
            moviesBean.setCoverImg(c.getString(c.getColumnIndex("CoverImg")));
            beanList.add(moviesBean);
            if(beanList.size()==Config.NOMOR_PAGE_SIZE)
                break;
        }
        c.close();
        return beanList;
    }

    public List<String> queryPostReverse(int pageIndex) {
        ArrayList<String> beanList = new ArrayList<>();
        Cursor c = queryTheCursor();
        c.move(c.getCount()-((pageIndex - 1) * Config.NOMOR_PAGE_SIZE)+1);
        while (c.moveToPrevious()) {
            beanList.add(c.getString(c.getColumnIndex("ObjectId")));
            if(beanList.size()==Config.NOMOR_PAGE_SIZE)
                break;
        }
        c.close();
        return beanList;
    }

    /**
     * query all Movie, return cursor
     *
     * @return Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM " + tableName,
                null);
        return c;
    }

    /**
     * 是否存在某条记录
     * @param movieId
     * @return
     */
    public boolean hasContainId(String movieId){
        boolean hasContain = false;
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            if(c.getString(c.getColumnIndex("MovieID")).equals(movieId)){
                hasContain = true;
                break;
            }
        }
        c.close();
        return hasContain;
    }

    public boolean hasContainPostId(String postId){
        boolean hasContain = false;
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            if(c.getString(c.getColumnIndex("ObjectId")).equals(postId)){
                hasContain = true;
                break;
            }
        }
        c.close();
        return hasContain;
    }

    /**
     * close database
     */
    public void closeDB() {
        // 释放数据库资源
        db.close();
    }
    /**
     * 清空表
     */
    public void clearTable(String tableName){
        db.execSQL("DELETE FROM "+tableName);
    }

    /**
     * 清空表
     */
    public void clearTable(){
        db.execSQL("DELETE FROM "+tableName);
    }
}
