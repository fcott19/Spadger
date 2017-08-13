package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.bean.MovieInfoBean;
import com.fcott.spadger.model.bean.MoviePlayBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;
import com.tencent.smtt.sdk.TbsVideo;

import java.util.List;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetialActivity extends BaseActivity {
    public static final String TAG = MovieDetialActivity.class.getSimpleName();

    private MovieBean.MessageBean.MoviesBean moviesBean;
    private MoviePlayBean moviePlayBean;

    @Bind(R.id.tv_time)
    public TextView tvTime;
    @Bind(R.id.tv_actor)
    public TextView tvActor;
    @Bind(R.id.tv_channel)
    public TextView tvChannel;
    @Bind(R.id.tv_class)
    public TextView tvClass;
    @Bind(R.id.tv_supplier)
    public TextView tvSupplier;
    @Bind(R.id.tv_title)
    public TextView tvTitle;
    @Bind(R.id.tv_descript)
    public TextView tvDescript;
    @Bind(R.id.iv_actor)
    public ImageView ivActor;
    @Bind(R.id.iv_play)
    public ImageView ivPlay;
    @Bind(R.id.nest)
    View view;

    @Override
    protected View getLoadingTargetView() {
        return view;
    }

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

        toggleShowLoading(true);
        tvTime.setText(moviesBean.getCreateTime());
        tvDescript.setText(moviesBean.getDescription());
        tvTitle.setText(moviesBean.getName());
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moviePlayBean != null && TbsVideo.canUseTbsPlayer(MovieDetialActivity.this)) {
                    TbsVideo.openVideo(MovieDetialActivity.this, moviePlayBean.getMessage());
                }else {
                    Toast.makeText(MovieDetialActivity.this,"未获取到播放地址",Toast.LENGTH_SHORT).show();
                }
            }
        });

        RequestBody playBody = new FormBody.Builder()
                .add("MovieID",moviesBean.getMovieID())
                .build();
        RetrofitUtils.getInstance().create(LookMovieService.class)
                .requestMovieInfo(playBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieInfoBean>() {
                    @Override
                    public void onCompleted() {
                        toggleShowLoading(false);
                    }
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MovieInfoBean movieInfoBean) {
                        MovieInfoBean.MessageBean messageBean = movieInfoBean.getMessage();
                        tvActor.setText(makeActor(messageBean.getActor()));
                        tvChannel.setText(messageBean.getChannel().getName());
                        tvClass.setText(makeClass(messageBean.getClassBean()));
                        tvSupplier.setText(messageBean.getSupplier().getName());
                        tvTime.setText(messageBean.getCreateTime());
                        Glide.with(MovieDetialActivity.this)
                                .load(messageBean.getCoverImg())
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(ivPlay);
                        Glide.with(MovieDetialActivity.this)
                                .load(messageBean.getImg())
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(ivActor);
                    }
                });

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
                    public void onNext(MoviePlayBean playBean) {
                        moviePlayBean = playBean;
                    }
                });
    }

    private String makeActor(List<MovieInfoBean.MessageBean.ActorBean> actorBeens){
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0;i < actorBeens.size();i++){
            if(i != 0)
                stringBuffer.append(",");
            stringBuffer.append(actorBeens.get(i).getName());
        }
        return stringBuffer.toString();
    }
    private String makeClass(List<MovieInfoBean.MessageBean.ClassBean> classBeen){
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0;i < classBeen.size();i++){
            if(i != 0)
                stringBuffer.append(",");
            stringBuffer.append(classBeen.get(i).getName());
        }
        return stringBuffer.toString();
    }
}
