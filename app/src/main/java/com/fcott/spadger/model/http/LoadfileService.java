package com.fcott.spadger.model.http;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2016/12/30.
 */

public interface LoadfileService {
    public String BASE_URL = "http://sd.52avhd.com:9888/";

    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);
}
