package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.MovieListAdapter;
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

/**
 * Created by Administrator on 2017/8/11.
 */

public class MovieListActivity extends BaseActivity implements PageController.ObserverPageListener {
    public static final String TAG = MovieListActivity.class.getSimpleName();

    private String type;
    private String channelId;
    private String actorId;
    private String classId;
    private String searchData;
    private String cacheTag;
    private RequestBody moviceBody;
    private MovieListAdapter movieListAdapter;
    private Subscription subscription;

    @Bind(R.id.contain)
    View contain;
    @Bind(R.id.rcy_movie)
    RecyclerView recyclerView;
    @Bind(R.id.rl_pagecontrol)
    public PageController pageController;


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
        type = bundle.getString("TYPE");
        channelId = bundle.getString("ID");
        actorId = bundle.getString("ACTORID");
        classId = bundle.getString("CLASSID");
        searchData = bundle.getString("DATA");
    }

    @Override
    protected void initViews() {

        pageController.setObserverPageListener(this);
        movieListAdapter = new MovieListAdapter(this, new ArrayList<MovieBean.MessageBean.MoviesBean>(), false);
        movieListAdapter.setOnItemClickListener(new OnItemClickListeners<MovieBean.MessageBean.MoviesBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, MovieBean.MessageBean.MoviesBean data, int position) {
                Intent intent = new Intent(MovieListActivity.this, MovieDetialActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("DATA", data);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MovieListActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(movieListAdapter);
        requestData(1);
    }

    private void requestData(final int currentPageIndex) {
        requestData(currentPageIndex, true);
    }

    private void requestData(final int currentPage, final boolean hasUpdate) {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();

        if (type.equals(Config.typeChannel) && channelId != null) {
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(currentPage))
                    .add("PageSize", "20")
                    .add("Type", "1")
                    .add("ID", channelId)
                    .add("Data", "")
                    .build();
            cacheTag = TAG + "CHANNELID" + channelId + String.valueOf(currentPage);
        } else if (type.equals(Config.typeActor) && actorId != null) {
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(currentPage))
                    .add("PageSize", "20")
                    .add("Type", "5")
                    .add("ID", actorId)
                    .add("Data", "")
                    .build();
            cacheTag = TAG + "ACTORID" + actorId + String.valueOf(currentPage);
        } else if (type.equals(Config.typeClass) && classId != null) {
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(currentPage))
                    .add("PageSize", "20")
                    .add("Type", "2")
                    .add("ID", classId)
                    .add("Data", "")
                    .build();
            cacheTag = TAG + "CLASSID" + classId + String.valueOf(currentPage);
        } else if (type.equals(Config.typeSearch) && searchData != null) {
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(currentPage))
                    .add("PageSize", "20")
                    .add("Type", "1")
                    .add("ID", "-1")
                    .add("Data", searchData)
                    .build();
            cacheTag = TAG + "SEARCH" + searchData + String.valueOf(currentPage);
        } else {
            Toast.makeText(MovieListActivity.this, "操作错误", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean needUpdate = NativeUtil.needUpdate(TAG);
        ACache mCache = ACache.get(MovieListActivity.this.getApplicationContext());
        //取出缓存
        String value = mCache.getAsString(cacheTag);
        //显示缓存
        if (!TextUtils.isEmpty(value) && hasUpdate) {
            MovieBean movieBean = GsonUtil.fromJson(value, MovieBean.class);
            movieListAdapter.setNewData(movieBean.getMessage().getMovies());
        } else if (hasUpdate && needUpdate) {
            toggleShowLoading(true);
        }

        if (needUpdate) {
            subscription = RetrofitUtils.getInstance().create(LookMovieService.class)
                    .requestMovie(moviceBody)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<MovieBean>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            toggleShowLoading(false);
                        }

                        @Override
                        public void onNext(MovieBean bean) {
                            if (hasUpdate){
                                if(bean.getResult() != 1){
                                    toggleShowError("请求出错");
                                    return;
                                }else if(bean.getMessage().getTotal() == 0){
                                    toggleShowError("无数据");
                                    return;
                                }else {
                                    toggleShowLoading(false);
                                    movieListAdapter.setNewData(bean.getMessage().getMovies());
                                }
                            }

                            pageController.setMaxPageIndex(bean.getMessage().getPageCount());
                            //缓存
                            ACache aCache = ACache.get(MovieListActivity.this.getApplicationContext());
                            aCache.put(cacheTag, GsonUtil.toJson(bean));

                            for (MovieBean.MessageBean.MoviesBean bean1 : bean.getMessage().getMovies()) {
                                ImageLoader.getInstance().preLoad(MovieListActivity.this, bean1.getCoverImg());
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
