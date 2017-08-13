package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ActorBean;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.MovieListAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/11.
 */

public class MovieListActivity extends BaseActivity {
    public static final String TAG = MovieListActivity.class.getSimpleName();

    private String channelId = Config.noId;
    private String actorName;
    private RequestBody moviceBody;
    private MovieListAdapter movieListAdapter;

    @Bind(R.id.contain)
    View contain;
    @Bind(R.id.rcy_movie)
    RecyclerView recyclerView;

    @Override
    protected View getLoadingTargetView() {
        return contain;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_movie_list;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        channelId = bundle.getString("ID");
        actorName = bundle.getString("NAME");
    }

    @Override
    protected void initViews() {
        toggleShowLoading(true);

//        if(!Config.noId.equals(channelId)){
//            moviceBody = new FormBody.Builder()
//                    .add("PageIndex","1")
//                    .add("PageSize","20")
//                    .add("Type","1")
//                    .add("ID",String.valueOf(channelId))
//                    .add("Data","")
//                    .build();
//        }else
            if(actorName != null){
            moviceBody = new FormBody.Builder()
                    .add("PageIndex","1")
                    .add("PageSize","20")
                    .add("Type","1")
                    .add("ID","-1")
                    .add("Data",actorName)
                    .build();
        }
        RetrofitUtils.getInstance().create(LookMovieService.class)
                .requestMovie(moviceBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieBean>() {
                    @Override
                    public void onCompleted() {
                        toggleShowLoading(false);
                    }
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MovieBean movieBean) {
                        for (MovieBean.MessageBean.MoviesBean bean1 : movieBean.getMessage().getMovies()) {
                            ImageLoader.getInstance().preLoad(MovieListActivity.this, bean1.getCoverImg());
                        }
                        movieListAdapter.setNewData(movieBean.getMessage().getMovies());
                    }
                });

        movieListAdapter = new MovieListAdapter(this,new ArrayList<MovieBean.MessageBean.MoviesBean>(),false);
        movieListAdapter.setOnItemClickListener(new OnItemClickListeners<MovieBean.MessageBean.MoviesBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, MovieBean.MessageBean.MoviesBean data, int position) {
                Intent intent = new Intent(MovieListActivity.this,MovieDetialActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("DATA",data);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MovieListActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(movieListAdapter);

    }
}
