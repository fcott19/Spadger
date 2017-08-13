package com.fcott.spadger.ui.activity;

import android.os.Bundle;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.bean.MoviePlayBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.LogUtil;
import com.tencent.smtt.sdk.TbsVideo;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetialActivity extends BaseActivity {
    public static final String TAG = MovieDetialActivity.class.getSimpleName();

    private MovieBean.MessageBean.MoviesBean moviesBean;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_movie_detial;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        moviesBean = bundle.getParcelable("DATA");
    }

    @Override
    protected void initViews() {
        LogUtil.log(TAG, GsonUtil.toJson(moviesBean));
        RequestBody playBody = new FormBody.Builder()
                .add("MovieID",moviesBean.getMovieID())
                .build();
        RetrofitUtils.getInstance().create(LookMovieService.class)
                .moviePlay(playBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MoviePlayBean>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MoviePlayBean movieBean) {
                        if (TbsVideo.canUseTbsPlayer(MovieDetialActivity.this)) {
                            TbsVideo.openVideo(MovieDetialActivity.this, movieBean.getMessage());
                        }
                    }
                });
    }
}
