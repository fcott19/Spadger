package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieClassBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.MovieClassAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.ArrayList;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieClassActivity extends BaseActivity {
    public static final String TAG = MovieClassActivity.class.getSimpleName();

    private MovieClassAdapter movieClassAdapter;
    private static final int pageSize = 60;
    private int pageIndex = 1;
    private int maxIndex;

    @Bind(R.id.rcy_actor)
    public RecyclerView recyclerView;
    @Bind(R.id.contain)
    View contain;

    @Override
    protected View getLoadingTargetView() {
        return contain;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_movie_class;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {
        toggleShowLoading(true);

        RequestBody body = new FormBody.Builder()
                .add("PageIndex", String.valueOf(pageIndex))
                .add("PageSize", String.valueOf(pageSize))
                .build();

        RetrofitUtils.getInstance().create(LookMovieService.class)
                .requestClass(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieClassBean>() {
                    @Override
                    public void onCompleted() {
                        Log.w("response", "completed");
                        toggleShowLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("response", e.toString());
                    }

                    @Override
                    public void onNext(MovieClassBean bean) {
                        movieClassAdapter.setNewData(bean.getMessage().getData());
                    }
                });

        movieClassAdapter = new MovieClassAdapter(MovieClassActivity.this, new ArrayList<MovieClassBean.MessageBean.DataBean>(), false);
        movieClassAdapter.setOnItemClickListener(new OnItemClickListeners<MovieClassBean.MessageBean.DataBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, MovieClassBean.MessageBean.DataBean data, int position) {
                Intent intent = new Intent();
                intent.setClass(MovieClassActivity.this, MovieListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("CLASSID", data.getID());
                bundle.putString("TYPE", Config.typeClass);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        final LinearLayoutManager layoutManager = new GridLayoutManager(MovieClassActivity.this, 2);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(movieClassAdapter);
    }
}
