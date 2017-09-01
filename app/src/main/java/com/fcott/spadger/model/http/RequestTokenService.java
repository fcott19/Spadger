package com.fcott.spadger.model.http;

import com.fcott.spadger.model.bean.TokenResultBean;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Administrator on 2016/12/30.
 */

public interface RequestTokenService {
    public String BASE_URL = "http://api.imovi.cc/";

    @POST("/vodapi.html")
    Observable<TokenResultBean> requestToken(@Body RequestBody requestBody);
}
