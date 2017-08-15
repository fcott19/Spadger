package com.fcott.spadger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.fcott.spadger.App;
import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ActorBean;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.MovieTypeAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.glideutils.ImageLoader;
import com.fcott.spadger.utils.netstatus.NetChangeObserver;
import com.fcott.spadger.utils.netstatus.NetStateReceiver;
import com.fcott.spadger.utils.netstatus.NetUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LookMovieActivity extends BaseActivity implements NetChangeObserver {
    public static final String TAG = LookMovieActivity.class.getSimpleName();

    private MovieTypeAdapter adapter = null;
    private final String[] typeList = new String[]{"最新", "动漫", "有码", "中文", "演员", "类型" , "搜索"};
    private RequestBody requestBody;
    private Subscription subscription1,subscription2;

    @Bind(R.id.rcy_movie_type)
    public RecyclerView recyclerView;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_look_movie;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {
        //注册网络监听
        NetStateReceiver.registerObserver(this);

        //wifi环境下，预加载图片
        if(NetUtils.isWifiConnected(getApplicationContext())){
            perLoadData();
        }

        //列表适配器
        adapter = new MovieTypeAdapter(LookMovieActivity.this, Arrays.asList(typeList), false);
        adapter.setOnItemClickListener(new OnItemClickListeners<String>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, String data, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                String id = null;
                switch (position) {
                    case 0:
                        intent.setClass(LookMovieActivity.this, MovieListActivity.class);
                        id = Config.newMoviceId;
                        bundle.putString("TYPE", Config.typeChannel);
                        break;
                    case 1:
                        intent.setClass(LookMovieActivity.this, MovieListActivity.class);
                        id = Config.cartoonMoviceId;
                        bundle.putString("TYPE", Config.typeChannel);
                        break;
                    case 2:
                        intent.setClass(LookMovieActivity.this, MovieListActivity.class);
                        id = Config.mosaicMoviceId;
                        bundle.putString("TYPE", Config.typeChannel);
                        break;
                    case 3:
                        intent.setClass(LookMovieActivity.this, MovieListActivity.class);
                        id = Config.chineseMoviceId;
                        bundle.putString("TYPE", Config.typeChannel);
                        break;
                    case 4:
                        intent.setClass(LookMovieActivity.this, MovieActorActivity.class);
                        break;
                    case 5:
                        intent.setClass(LookMovieActivity.this, MovieClassActivity.class);
                        break;
                    case 6:
                        intent.setClass(LookMovieActivity.this, SearchActivity.class);
                        break;
                }
                bundle.putString("ID", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(LookMovieActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 预加载数据
     */
    private void perLoadData() {
        SharedPreferences pref = App.getInstance().getSharedPreferences(Config.SP_PER_LOAD, Context.MODE_PRIVATE);
        boolean channelSuccess = pref.getBoolean("channel", false);
        boolean actorSuccess = pref.getBoolean("actor", false);

        if(!channelSuccess){
            subscription1 = Observable.merge(makeTaskList())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<MovieBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(MovieBean movieBean) {
                            for (MovieBean.MessageBean.MoviesBean bean1 : movieBean.getMessage().getMovies()) {
                                ImageLoader.getInstance().preLoad(LookMovieActivity.this, bean1.getCoverImg());
                            }
                            SharedPreferences.Editor sharedata = App.getInstance().getSharedPreferences(Config.SP_PER_LOAD, Context.MODE_PRIVATE).edit();
                            sharedata.putBoolean("channel", true);
                            sharedata.commit();
                        }
                    });
        }

        if(!actorSuccess){
            RequestBody body = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(60))
                    .add("PageSize", String.valueOf(1))
                    .build();
            subscription2 = RetrofitUtils.getInstance().create(LookMovieService.class)
                    .requestActor(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ActorBean>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(ActorBean bean) {
                            for (ActorBean.MessageBean.DataBean bean1 : bean.getMessage().getData()) {
                                ImageLoader.getInstance().preLoad(LookMovieActivity.this, bean1.getPic());
                            }
                            SharedPreferences.Editor sharedata = App.getInstance().getSharedPreferences(Config.SP_PER_LOAD, Context.MODE_PRIVATE).edit();
                            sharedata.putBoolean("actor", true);
                            sharedata.commit();
                        }
                    });
        }

    }

    /**
     * 构造请求Observable
     * @return Observable集合
     */
    private List<Observable<MovieBean>> makeTaskList() {
        String[] ids = new String[]{Config.newMoviceId, Config.cartoonMoviceId, Config.mosaicMoviceId, Config.chineseMoviceId};
        ArrayList<Observable<MovieBean>> obList = new ArrayList<>();
        LookMovieService lookMovieService = RetrofitUtils.getInstance().create(LookMovieService.class);

        for (int i = 0; i < ids.length; i++) {
            requestBody = new FormBody.Builder()
                    .add("PageIndex", "1")
                    .add("PageSize", "20")
                    .add("Type", "1")
                    .add("ID", ids[i])
                    .add("Data", "")
                    .build();
            obList.add(lookMovieService.requestMovie(requestBody));
        }
        return obList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册网络监听
        NetStateReceiver.removeRegisterObserver(this);
        if (subscription1 != null && !subscription1.isUnsubscribed())
            subscription1.unsubscribe();
        if (subscription2 != null && !subscription2.isUnsubscribed())
            subscription2.unsubscribe();
    }

    @Override
    public void onNetConnected(NetUtils.NetType type) {
        if(type == NetUtils.NetType.WIFI){
            RequestManager requestManager = Glide.with(LookMovieActivity.this);
            if(requestManager.isPaused()){
                requestManager.resumeRequests();
            }
        }else {
            Glide.with(LookMovieActivity.this).pauseRequests();
        }
    }

    @Override
    public void onNetDisConnect() {
        Glide.with(LookMovieActivity.this).pauseRequests();
    }
}
