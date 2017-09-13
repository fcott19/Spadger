package com.fcott.spadger.ui.activity.look;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.ui.adapter.MovieListAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.widget.PageController;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.NativeUtil;
import com.fcott.spadger.utils.db.DBManager;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/11.
 */

public class MovieListActivity extends BaseActivity implements PageController.ObserverPageListener {
    public static final String TAG = MovieListActivity.class.getSimpleName();
    public static final int NORMAL_CODE = 100;
    public static final int COLLECTION_CODE = 99;

    private String type;
    private String channelId;
    private String actorId;
    private String classId;
    private String searchData;
    private String cacheTag;
    private RequestBody moviceBody;
    private MovieListAdapter movieListAdapter;
    private Subscription subscription;
    private int requestCode = NORMAL_CODE;

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
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initViews() {

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("电影列表");

        pageController.setObserverPageListener(this);
        movieListAdapter = new MovieListAdapter(this, new ArrayList<MovieBean.MessageBean.MoviesBean>(), false);
        movieListAdapter.setOnItemClickListener(new OnItemClickListeners<MovieBean.MessageBean.MoviesBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, final MovieBean.MessageBean.MoviesBean data, int position) {
                    if (type.equals(Config.typeCollection)) {
                        requestCode = COLLECTION_CODE;
                    } else {
                        requestCode = NORMAL_CODE;
                    }
                    Intent intent = new Intent(MovieListActivity.this, MovieDetialActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("DATA", data);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, requestCode);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MovieListActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(movieListAdapter);
        requestData(1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COLLECTION_CODE) {
            requestDataFromDb(pageController.getCurrentPageIndex());
        }
    }

    private void requestDataFromDb(final int currentPage) {
        Observable.create(new Observable.OnSubscribe<List<MovieBean.MessageBean.MoviesBean>>() {
            @Override
            public void call(Subscriber<? super List<MovieBean.MessageBean.MoviesBean>> subscriber) {
                DBManager dbManager = new DBManager(MovieListActivity.this);
                List<MovieBean.MessageBean.MoviesBean> list = dbManager.query(currentPage);
                dbManager.closeDB();
                subscriber.onNext(list);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<MovieBean.MessageBean.MoviesBean>>() {
                    @Override
                    public void call(List<MovieBean.MessageBean.MoviesBean> moviesBeen) {
                        if (moviesBeen.size() != 0) {
                            pageController.setMaxPageIndex(moviesBeen.size() / Config.NOMOR_PAGE_SIZE + 1);
                            movieListAdapter.setNewData(moviesBeen);
                        } else {
                            toggleShowError(getString(R.string.no_collection));
                        }
                    }
                });
    }

    private void requestData(final int currentPageIndex) {
        requestData(currentPageIndex, true);
    }

    private void requestData(final int currentPage, final boolean hasUpdate) {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();

        if (type.equals(Config.typeCollection)) {
            requestDataFromDb(currentPage);
            return;
        } else if (type.equals(Config.typeChannel) && channelId != null) {
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(currentPage))
                    .add("PageSize", String.valueOf(Config.NOMOR_PAGE_SIZE))
                    .add("Type", "1")
                    .add("ID", channelId)
                    .add("Data", "")
                    .build();
            cacheTag = TAG + "CHANNELID" + channelId + String.valueOf(currentPage);
        } else if ((type.equals(Config.typeActor) && actorId != null) || (type.equals(Config.searchTypeActor) && searchData != null)) {
            String id;
            if (actorId != null)
                id = actorId;
            else
                id = searchData;
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(currentPage))
                    .add("PageSize", String.valueOf(Config.NOMOR_PAGE_SIZE))
                    .add("Type", "5")
                    .add("ID", id)
                    .add("Data", "")
                    .build();
            cacheTag = TAG + "ACTORID" + id + String.valueOf(currentPage);
        } else if ((type.equals(Config.typeClass) && classId != null) || (type.equals(Config.searchTypeClass) && searchData != null)) {
            String id;
            if (classId != null)
                id = classId;
            else
                id = searchData;
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(currentPage))
                    .add("PageSize", String.valueOf(Config.NOMOR_PAGE_SIZE))
                    .add("Type", "2")
                    .add("ID", id)
                    .add("Data", "")
                    .build();
            cacheTag = TAG + "CLASSID" + id + String.valueOf(currentPage);
        } else if (type.equals(Config.searchTypeNormal) && searchData != null) {
            moviceBody = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(currentPage))
                    .add("PageSize", String.valueOf(Config.NOMOR_PAGE_SIZE))
                    .add("Type", "1")
                    .add("ID", "-1")
                    .add("Data", searchData)
                    .build();
            cacheTag = TAG + "SEARCH" + searchData + String.valueOf(currentPage);
        } else {
            Toast.makeText(MovieListActivity.this, "操作错误", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean needUpdate = NativeUtil.needUpdate(cacheTag);
        ACache mCache = ACache.get(MovieListActivity.this.getApplicationContext());
        //取出缓存
        String value = mCache.getAsString(cacheTag);
        if (TextUtils.isEmpty(value))
            needUpdate = true;
        //显示缓存
        if (!TextUtils.isEmpty(value) && hasUpdate) {
            MovieBean movieBean = GsonUtil.fromJson(value, MovieBean.class);
            pageController.setMaxPageIndex(movieBean.getMessage().getPageCount());
            movieListAdapter.setNewData(movieBean.getMessage().getMovies());
            if (currentPage == pageController.getCurrentPageIndex()) {
                int nextPage = currentPage + 1;
                requestData(nextPage, false);
            }
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
                            toggleShowError("请求出错");
                        }

                        @Override
                        public void onNext(MovieBean bean) {
                            if (hasUpdate) {
                                if (bean.getResult() != 1) {
                                    toggleShowError("请求出错");
                                    return;
                                } else if (bean.getMessage().getTotal() == 0) {
                                    toggleShowError("无数据");
                                    return;
                                } else {
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
        LogUtil.log(TAG, "onDestroy");
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    @Override
    public void goPage(int page) {
        requestData(page);
    }
}
