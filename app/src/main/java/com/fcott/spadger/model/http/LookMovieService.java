package com.fcott.spadger.model.http;

import com.fcott.spadger.model.bean.ActorBean;
import com.fcott.spadger.model.bean.LoginBean;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.bean.MovieClassBean;
import com.fcott.spadger.model.bean.MovieInfoBean;
import com.fcott.spadger.model.bean.MoviePlayBean;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Administrator on 2017/8/10.
 */

public interface LookMovieService {
    public String BASE_URL = "http://www.look588.com/";

    @POST("/User/Login")
    Observable<LoginBean> login(@Body RequestBody requestBody);

    @POST("/Movie/GetMovies")
    Observable<MovieBean> requestMovie(@Body RequestBody requestBody);

    @POST("/Movie/GetActor")
    Observable<ActorBean> requestActor(@Body RequestBody requestBody);

    @POST("/Movie/GetClass")
    Observable<MovieClassBean> requestClass(@Body RequestBody requestBody);

    @POST("/Movie/Play")
    Observable<MoviePlayBean> moviePlay(@Body RequestBody requestBody);

    @POST("/Movie/GetMovieInfo")
    Observable<MovieInfoBean> requestMovieInfo(@Body RequestBody requestBody);

    @POST("/Movie/GetActor")
    Observable<String> requestActorJson(@Body RequestBody requestBody);

    @POST("/Movie/GetClass")
    Observable<String> requestClassJson(@Body RequestBody requestBody);
}
