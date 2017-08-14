package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ActorBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.ActorAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieActorActivity extends BaseActivity {
    public static final String TAG = MovieActorActivity.class.getSimpleName();

    private static final int pageSize = 60;
    private int pageIndex = 1;
    private int maxIndex;
    private String cacheTag;
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

        actorAdapter = new ActorAdapter(MovieActorActivity.this, new ArrayList<ActorBean.MessageBean.DataBean>(), false);
        actorAdapter.setOnItemClickListener(new OnItemClickListeners<ActorBean.MessageBean.DataBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, ActorBean.MessageBean.DataBean data, int position) {
                Intent intent = new Intent();
                intent.setClass(MovieActorActivity.this, MovieListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ACTORID", data.getID());
                bundle.putString("TYPE", Config.typeActor);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        final LinearLayoutManager layoutManager = new GridLayoutManager(MovieActorActivity.this, 2);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(actorAdapter);

        cacheTag = TAG+"ACTOR" + pageIndex;
        ACache mCache = ACache.get(MovieActorActivity.this.getApplicationContext());
        //取出缓存
        String value = mCache.getAsString(cacheTag);
        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            ActorBean actorBean = GsonUtil.fromJson(value,ActorBean.class);
            actorAdapter.setNewData(actorBean.getMessage().getData());
        } else {
            toggleShowLoading(true);
        }

        RequestBody body = new FormBody.Builder()
                .add("PageIndex", String.valueOf(pageIndex))
                .add("PageSize", String.valueOf(pageSize))
                .build();
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
                        //缓存
                        ACache aCache = ACache.get(MovieActorActivity.this.getApplicationContext());
                        aCache.put(cacheTag, GsonUtil.toJson(bean));
                        for (ActorBean.MessageBean.DataBean bean1 : bean.getMessage().getData()) {
                            ImageLoader.getInstance().preLoad(MovieActorActivity.this, bean1.getPic());
                        }
                        actorAdapter.setNewData(bean.getMessage().getData());
                    }
                });
    }
}
