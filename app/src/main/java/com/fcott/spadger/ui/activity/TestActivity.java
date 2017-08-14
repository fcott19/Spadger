package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.LoginBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/10.
 */

public class TestActivity extends BaseActivity {
    public static final String TAG = TestActivity.class.getSimpleName();
    //http://www.look588.com/Movie/MovieInfo/5039978
    //_Incapsula_Resource?SWJIYLWA=2977d8d74f63d7f8fedbea018b7a1d05&ns=2
    ///Scripts/Movies.js?t=2017
    ///Movie/GetMovies
    ///Movie/GetMovieInfoAD
    ///Movie/Actor
    ///Movie/Class
    ///Movie/Channel
    //Default橘优花

    ///Movie/MovieList/ + id + / +name + /5
    ///Movie/MovieList/46/橘优花/5

    ///Movie/MovieList/2/巨乳/2
    //GetMovies=function(){var info=null;var URL="/Movie/GetMovies";$.ajax({url:URL,type:"post",dataType:"json",async:false,data:{PageIndex:objMovies.PageIndex,PageSize:objMovies.PageSize,Type:objMovies.Type,ID:objMovies.ID,Data:objMovies.Data},success:function(result){if(result.Result==1){info=result.Message;$.ajax({url:"/Movie/GetMovieInfoAD",type:"post",dataType:"json",async:false,data:{Type:"4"},success:function(result){if(result.Result==1){var item1="";for(var i=0;i<result.Message.length;i++){item1+="<div class=\"hdadvline\"><a href=\""+result.Message[0].LinkURL+"\"><img src=\""+result.Message[0].MediaURL+"\" /></a></div>";}

//    moviceBody = new FormBody.Builder()
//            .add("PageIndex","1")
//                    .add("PageSize","20")
//                    .add("Type","1")
//                    .add("ID","-1")
//                    .add("Data",actorName)
//                    .build();

//    RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
//    android:layout_width="match_parent"
//    android:layout_height="wrap_content"
//    android:padding="2dp"
//    android:orientation="vertical"
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_test;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {
//        RetrofitUtils.getInstance().create1(LookMovieService.class)
//                .getMainPage("/Movie/MovieList/46/橘优花/5")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<String>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.w("response","completed");
//                    }
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.w("response",e.toString());
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        LogUtil.log(TAG,s);
//                    }
//                });

        RequestBody body = new FormBody.Builder()
                .add("PageIndex","1")
                .add("PageSize","20")
                .add("Type","1")
                .add("ID","-1")
                .add("Data","")
                .build();
        RequestBody body2 = new FormBody.Builder()
                .add("Type","3")
                .build();

        RequestBody body3 = new FormBody.Builder()
                .add("PageIndex","1")
                .add("PageSize","60")
                .build();

        RequestBody loginBody = new FormBody.Builder()
                .add("Token","7B7572EECF394DCF9F74177BA90D402A")
                .build();

        RequestBody playBody = new FormBody.Builder()
                .add("MovieID","5039978")
                .build();

        RetrofitUtils.getInstance().create1(LookMovieService.class)
                .login(loginBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LoginBean>() {
                    @Override
                    public void onCompleted() {
                        Log.w("response","completed");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.w("response",e.toString());
                    }

                    @Override
                    public void onNext(LoginBean s) {

                    }
                });

    }
}
