package com.fcott.spadger.model.http;


import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2016/9/20.
 */
public interface MainPageService {
    public String BASE_URL = "http://www.37kvkv.com/";

    @GET
    Observable<String> getMainPage(@Url String url);

    @GET("myjs/alljs.js")
    Observable<String> getNextUrl();

    @GET
    Observable<String> getData(@Url String url);
}
