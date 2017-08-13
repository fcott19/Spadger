package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ActorBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.ActorAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ActorActivity extends BaseActivity {
    public static final String TAG = ActorActivity.class.getSimpleName();

    private static final int pageSize = 60;
    private int pageIndex = 1;
    private int maxIndex;
    private ActorAdapter actorAdapter;

    @Bind(R.id.contain)
    View contain;
    @Bind(R.id.rcy_actor)
    public RecyclerView recyclerView;

    @Override
    protected View getLoadingTargetView() {
        return contain;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_actor;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {

        RequestBody body = new FormBody.Builder()
                .add("PageIndex", String.valueOf(pageIndex))
                .add("PageSize", String.valueOf(pageSize))
                .build();

        toggleShowLoading(true);
        RetrofitUtils.getInstance().create(LookMovieService.class)
                .requestActor(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ActorBean>() {
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
                    public void onNext(ActorBean bean) {
                        for (ActorBean.MessageBean.DataBean bean1 : bean.getMessage().getData()) {
                            ImageLoader.getInstance().preLoad(ActorActivity.this, bean1.getPic());
                        }
                        actorAdapter.setNewData(bean.getMessage().getData());
                    }
                });

        actorAdapter = new ActorAdapter(ActorActivity.this, new ArrayList<ActorBean.MessageBean.DataBean>(), false);
        actorAdapter.setOnItemClickListener(new OnItemClickListeners<ActorBean.MessageBean.DataBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, ActorBean.MessageBean.DataBean data, int position) {
                Intent intent = new Intent();
                intent.setClass(ActorActivity.this, MovieListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("NAME", data.getName());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        final LinearLayoutManager layoutManager = new GridLayoutManager(ActorActivity.this, 2);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(actorAdapter);
    }
}
