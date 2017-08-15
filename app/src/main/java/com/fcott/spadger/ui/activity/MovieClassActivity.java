package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieClassBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.MovieClassAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.widget.PageController;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.NativeUtil;

import java.util.ArrayList;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieClassActivity extends BaseActivity implements PageController.ObserverPageListener {
    public static final String TAG = MovieClassActivity.class.getSimpleName();

    private MovieClassAdapter movieClassAdapter;
    private static final int pageSize = 60;
    private Subscription subscription;
    private String cacheTag;

    @Bind(R.id.rcy_class)
    public RecyclerView recyclerView;
    @Bind(R.id.contain)
    public View contain;
    @Bind(R.id.rl_pagecontrol)
    public PageController pageController;

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

        pageController.setObserverPageListener(this);
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
        requestData(1);
    }

    private void requestData(final int currentPageIndex) {
        requestData(currentPageIndex, true);
    }

    private void requestData(final int currentPage, final boolean hasUpdate) {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();

        boolean needUpdate = NativeUtil.needUpdate(TAG);
        cacheTag = TAG + "CLASS" + currentPage;
        ACache mCache = ACache.get(MovieClassActivity.this.getApplicationContext());
        //取出缓存
        String value = mCache.getAsString(cacheTag);
        //显示缓存
        if (!TextUtils.isEmpty(value) && hasUpdate) {
            MovieClassBean movieClassBean = GsonUtil.fromJson(value, MovieClassBean.class);
            movieClassAdapter.setNewData(movieClassBean.getMessage().getData());
        } else if (hasUpdate && needUpdate) {
            toggleShowLoading(true);
        }

        if (needUpdate) {
            RequestBody body = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(currentPage))
                    .add("PageSize", String.valueOf(pageSize))
                    .build();

            subscription = RetrofitUtils.getInstance().create(LookMovieService.class)
                    .requestClass(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<MovieClassBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            toggleShowLoading(false);
                        }

                        @Override
                        public void onNext(MovieClassBean bean) {
                            if (hasUpdate){
                                if(bean.getResult() != 1){
                                    toggleShowError("请求出错");
                                    return;
                                }else if(bean.getMessage().getTotal() == 0){
                                    toggleShowError("无数据");
                                    return;
                                }else {
                                    toggleShowLoading(false);
                                    movieClassAdapter.setNewData(bean.getMessage().getData());
                                }
                            }

                            pageController.setMaxPageIndex(bean.getMessage().getPageCount());
                            //缓存
                            ACache aCache = ACache.get(MovieClassActivity.this.getApplicationContext());
                            aCache.put(cacheTag, GsonUtil.toJson(bean));

                            if (currentPage == pageController.getCurrentPageIndex()) {
                                int nextPage = currentPage + 1;
                                requestData(nextPage, false);
                            }
                        }
                    });
        }
    }

    @Override
    public void goPage(int page) {
        requestData(page);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
