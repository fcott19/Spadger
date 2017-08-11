package com.fcott.spadger.model.http;

import com.fcott.spadger.model.bean.ActorBean;
import com.fcott.spadger.model.bean.MovieBean;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2017/8/10.
 */

public interface LookMovieService {
    public String BASE_URL = "http://www.look588.com/";

    @GET
    Observable<String> getMainPage(@Url String url);

    @POST("/User/Login")
    Observable<String> login(@Body RequestBody requestBody);

    @POST("/Movie/GetMovies")
    Observable<MovieBean> requestMovie(@Body RequestBody requestBody);

    @POST("/Movie/GetActor")
    Observable<ActorBean> requestActor(@Body RequestBody requestBody);

    @POST("/Movie/GetClass")
    Observable<String> requestClass(@Body RequestBody requestBody);

    @POST("/Movie/GetMovieInfoAD")
    Observable<String> requestMovieAd(@Body RequestBody requestBody);

    @POST("/Movie/Play")
    Observable<String> moviePlay(@Body RequestBody requestBody);

    @POST("/Movie/GetMovieInfo")
    Observable<String> requestMovieInfo(@Body RequestBody requestBody);
}
