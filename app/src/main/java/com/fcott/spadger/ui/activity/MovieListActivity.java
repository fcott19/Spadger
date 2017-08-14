package com.fcott.spadger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.MovieListAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/11.
 */

public class MovieListActivity extends BaseActivity {
    public static final String TAG = MovieListActivity.class.getSimpleName();

    private String type;
    private String channelId;
    private String actorId;
    private String classId;
    private String cacheTag;
    private RequestBody moviceBody;
    private MovieBean movieBean;
    private MovieListAdapter movieListAdapter;
    private int currentPageIndex = 1;
    private Subscription subscription;

    @Bind(R.id.contain)
    View contain;
    @Bind(R.id.et_page_number)
    public EditText etPageNumber;
    @Bind(R.id.rcy_movie)
    RecyclerView recyclerView;

    @OnClick({R.id.tv_first_page, R.id.tv_pre_page, R.id.tv_next_page, R.id.tv_last_page})
    public void onClick(View view) {
        if (movieBean == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_first_page:
                if (currentPageIndex != 1) {
                    requestData(1);
                } else {
                    Toast.makeText(MovieListActivity.this, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_pre_page:
                if (currentPageIndex > 1) {
                    currentPageIndex--;
                    requestData(currentPageIndex);
                } else {
                    Toast.makeText(MovieListActivity.this, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_next_page:
                if (currentPageIndex < movieBean.getMessage().getPageCount()) {
                    currentPageIndex++;
                    requestData(currentPageIndex);
                } else {
                    Toast.makeText(MovieListActivity.this, getResources().getString(R.string.already_last), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_last_page:
                if (currentPageIndex != movieBean.getMessage().getPageCount()) {
                    requestData(movieBean.getMessage().getPageCount());
                } else {
                    Toast.makeText(MovieListActivity.this, getResources().getString(R.string.already_last), Toast.LENGTH_LONG).show();
                }
                break;
        }
        etPageNumber.setText(String.valueOf(currentPageIndex));
    }

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
    }

    @Override
    protected void initViews() {

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

        //监听软键盘完成按钮
        etPageNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    /*隐藏软键盘*/
                InputMethodManager imm = (InputMethodManager) v
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(
                            v.getApplicationWindowToken(), 0);
                }
                goPage();
                //etPageNumber失去焦点，隐藏光标
                etPageNumber.clearFocus();
                etPageNumber.setFocusable(false);
                return true;
            }
        });
        //触摸etPageNumber时获取焦点
        etPageNumber.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                etPageNumber.setFocusable(true);
                etPageNumber.setFocusableInTouchMode(true);
                etPageNumber.requestFocus();
                return false;
            }
        });
    }

    //跳转到指定页面
    private void goPage() {
        int pageNumber = 0;
        try {
            pageNumber = Integer.valueOf(etPageNumber.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pageNumber < 1 || pageNumber > movieBean.getMessage().getPageCount()) {
            Toast.makeText(MovieListActivity.this, getResources().getString(R.string.input_error), Toast.LENGTH_SHORT).show();
        } else {
            requestData(pageNumber);
        }
    }

    private void requestData(final int currentPageIndex) {
        requestData(currentPageIndex, true);
    }

    private void requestData(final int currentPage, final boolean hasUpdate) {
        if(subscription != null && !subscription.isUnsubscribed())
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
        } else {
            Toast.makeText(MovieListActivity.this, "操作错误", Toast.LENGTH_SHORT).show();
            return;
        }

        ACache mCache = ACache.get(MovieListActivity.this.getApplicationContext());
        //取出缓存
        String value = mCache.getAsString(cacheTag);
        //显示缓存
        if (!TextUtils.isEmpty(value) && hasUpdate) {
            MovieBean movieBean = GsonUtil.fromJson(value, MovieBean.class);
            movieListAdapter.setNewData(movieBean.getMessage().getMovies());
        } else {
            toggleShowLoading(true);
        }

        subscription = RetrofitUtils.getInstance().create(LookMovieService.class)
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
                    public void onNext(MovieBean bean) {

                        movieBean = bean;
                        //缓存
                        ACache aCache = ACache.get(MovieListActivity.this.getApplicationContext());
                        aCache.put(cacheTag, GsonUtil.toJson(bean));

                        for (MovieBean.MessageBean.MoviesBean bean1 : bean.getMessage().getMovies()) {
                            ImageLoader.getInstance().preLoad(MovieListActivity.this, bean1.getCoverImg());
                        }
                        if (hasUpdate)
                            movieListAdapter.setNewData(bean.getMessage().getMovies());

//                        if (currentPage == currentPageIndex) {
//                            int nextPage = currentPage + 1;
//                            requestData(nextPage, false);
//                        }
                    }
                });
    }

}
