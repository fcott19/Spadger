package com.fcott.spadger.model.http;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2017/8/29.
 */

public interface YirenService {
    public String BASE_URL = "http://www.yiren02.com/";

    @GET("/")
    Observable<String> getMainPage();

    @GET
    Observable<String> getData(@Url String url);

    @GET("/e/DownSys/play/")
    Observable<String> getVideoUrl(@Query("classid") String classid,@Query("id") String id,@Query("pathid") String pathid);

    @POST("/e/search/index.php")
    Observable<String> searchData(@Body RequestBody requestBody);

    @GET("/e/search/result/")
    Observable<String> searchData(@Query("searchid") String searchid,@Query("page") String page);
}
