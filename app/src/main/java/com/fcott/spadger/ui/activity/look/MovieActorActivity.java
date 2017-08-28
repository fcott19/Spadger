package com.fcott.spadger.ui.activity.look;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ActorBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.ui.adapter.ActorAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.widget.PageController;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.NativeUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieActorActivity extends BaseActivity implements PageController.ObserverPageListener{
    public static final String TAG = MovieActorActivity.class.getSimpleName();

    private static final int pageSize = 60;
    private String cacheTag;
    private ActorAdapter actorAdapter;
    private Subscription subscription;

    @Bind(R.id.contain)
    View contain;
    @Bind(R.id.rcy_actor)
    public RecyclerView recyclerView;
    @Bind(R.id.rl_pagecontrol)
    public PageController pageController;

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
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initViews() {

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("演员");

        pageController.setObserverPageListener(this);
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
        requestData(1);
    }

    private void requestData(final int currentPageIndex) {
        requestData(currentPageIndex, true);
    }

    private void requestData(final int currentPage, final boolean hasUpdate) {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();

        boolean needUpdate = NativeUtil.needUpdate(TAG);
        cacheTag = TAG + "ACTOR" + currentPage;
        ACache mCache = ACache.get(MovieActorActivity.this.getApplicationContext());
        //取出缓存
        String value = mCache.getAsString(cacheTag);
        //显示缓存
        if (!TextUtils.isEmpty(value) && hasUpdate) {
            ActorBean actorBean = GsonUtil.fromJson(value, ActorBean.class);
            actorAdapter.setNewData(actorBean.getMessage().getData());
        } else if (hasUpdate && needUpdate) {
            toggleShowLoading(true);
        }

        if (needUpdate) {
            RequestBody body = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(currentPage))
                    .add("PageSize", String.valueOf(pageSize))
                    .build();
            subscription = RetrofitUtils.getInstance().create(LookMovieService.class)
                    .requestActor(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ActorBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            toggleShowError("请求出错");
                        }

                        @Override
                        public void onNext(ActorBean bean) {
                            if (hasUpdate){
                                if(bean.getResult() != 1){
                                    toggleShowError("请求出错");
                                    return;
                                }else if(bean.getMessage().getTotal() == 0){
                                    toggleShowError("无数据");
                                    return;
                                }else {
                                    toggleShowLoading(false);
                                    actorAdapter.setNewData(bean.getMessage().getData());
                                }
                            }
                            pageController.setMaxPageIndex(bean.getMessage().getPageCount());
                            //缓存
                            ACache aCache = ACache.get(MovieActorActivity.this.getApplicationContext());
                            aCache.put(cacheTag, GsonUtil.toJson(bean));
                            for (ActorBean.MessageBean.DataBean bean1 : bean.getMessage().getData()) {
                                ImageLoader.getInstance().preLoad(MovieActorActivity.this, bean1.getPic());
                            }

                            if (currentPage == pageController.getCurrentPageIndex()) {
                                int nextPage = currentPage + 1;
                                requestData(nextPage, false);
                            }
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    @Override
    public void goPage(int page) {
        requestData(page);
    }
}
