package com.fcott.spadger.model.http;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2017/8/10.
 */

public interface LookMovie {
    public String BASE_URL = "http://www.look588.com/";

    @GET
    Observable<String> getMainPage(@Url String url);

    @POST("/User/Login")
    Observable<String> login(@Body RequestBody requestBody);

    @POST("/Movie/GetMovies")
    Observable<String> rquestMovie(@Body RequestBody requestBody);

    @POST("/Movie/GetActor")
    Observable<String> rquestActor(@Body RequestBody requestBody);

    @POST("/Movie/GetMovieInfoAD")
    Observable<String> rquestMovieAd(@Body RequestBody requestBody);


}
