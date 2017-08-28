package com.fcott.spadger.ui.activity.look;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ActorBean;
import com.fcott.spadger.model.bean.MovieClassBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.LogUtil;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {
    public static final String TAG = SearchActivity.class.getSimpleName();

    @Bind(R.id.sv)
    public SearchView sv;
    @Bind(R.id.lv)
    public ListView lv;
    @Bind(R.id.contain)
    public View contain;

    //自动完成的列表
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> tipList = new ArrayList();
    private Subscription subscription;
    private Map<String, ActorBean.MessageBean.DataBean> actorBeanMap;
    private Map<String, MovieClassBean.MessageBean.DataBean> movieClassBeanMap;


    @Override
    protected View getLoadingTargetView() {
        return contain;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_search;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.log(TAG);
        super.onCreate(savedInstanceState);
        LogUtil.log(TAG);
    }

    @Override
    protected void initViews() {

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("搜索");

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tipList.toArray(new String[tipList.size()]));
        lv.setAdapter(arrayAdapter);
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onQueryTextSubmit((String) lv.getAdapter().getItem(position));
            }
        });

        //设置该SearchView默认是否自动缩小为图标
        sv.setIconifiedByDefault(false);
        //为该SearchView组件设置事件监听器
        sv.setOnQueryTextListener(this);
        //设置该SearchView显示搜索按钮
        sv.setSubmitButtonEnabled(true);

        //设置该SearchView内默认显示的提示文本
        sv.setQueryHint("搜索");

        makeTips();
    }

    private void makeTips() {
        ACache mCache = ACache.get(SearchActivity.this.getApplicationContext());
        //取出缓存
        final String valueActor = mCache.getAsString(TAG + "Actor");
        final String valueClass = mCache.getAsString(TAG + "Class");
        //显示缓存
        if (!TextUtils.isEmpty(valueActor) && !TextUtils.isEmpty(valueClass)) {

            Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    makeDataMap(GsonUtil.fromJson(valueActor, ActorBean.class));
                    makeDataMap(GsonUtil.fromJson(valueClass, MovieClassBean.class));
                    subscriber.onNext(null);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            arrayAdapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, tipList.toArray(new String[tipList.size()]));
                            lv.setAdapter(arrayAdapter);
                        }
                    });
        } else {
            toggleShowLoading(true);
            RequestBody body = new FormBody.Builder()
                    .add("PageIndex", String.valueOf(1))
                    .add("PageSize", String.valueOf(10000))
                    .build();
            ArrayList<Observable<String>> obList = new ArrayList<>();
            LookMovieService lookMovieService = RetrofitUtils.getInstance().create1(LookMovieService.class);
            obList.add(lookMovieService.requestActorJson(body));
            obList.add(lookMovieService.requestClassJson(body));
            subscription = Observable.merge(obList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {
                            //缓存
                            toggleShowLoading(false);

                            arrayAdapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, tipList.toArray(new String[tipList.size()]));
                            lv.setAdapter(arrayAdapter);
                        }

                        @Override
                        public void onError(Throwable e) {
                            toggleShowLoading(false);
                        }

                        @Override
                        public void onNext(String json) {
                            ACache aCache = ACache.get(SearchActivity.this.getApplicationContext());
                            if (json.contains("http://")) {
                                aCache.put(TAG + "Actor", json);
                                makeDataMap(GsonUtil.fromJson(json, ActorBean.class));
                            } else {
                                aCache.put(TAG + "Class", json);
                                makeDataMap(GsonUtil.fromJson(json, MovieClassBean.class));
                            }
                        }
                    });
        }
    }

    //用户输入字符时激发该方法
    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        if (TextUtils.isEmpty(newText)) {
            lv.setVisibility(View.GONE);
        } else {
            lv.setVisibility(View.VISIBLE);
        }
        arrayAdapter.getFilter().filter(newText);
        return true;
    }

    private void makeDataMap(final ActorBean actorBean){
        Observable.from(actorBean.getMessage().getData())
                .toMap(new Func1<ActorBean.MessageBean.DataBean, String>() {
                    @Override
                    public String call(ActorBean.MessageBean.DataBean dataBean) {
                        return dataBean.getName();
                    }
                }).subscribe(new Action1<Map<String, ActorBean.MessageBean.DataBean>>() {
            @Override
            public void call(Map<String, ActorBean.MessageBean.DataBean> stringDataBeanMap) {
                actorBeanMap = stringDataBeanMap;
                for (ActorBean.MessageBean.DataBean dataBean : actorBean.getMessage().getData()) {
                    tipList.add(dataBean.getName());
                }
            }
        });
    }
    private void makeDataMap(final MovieClassBean movieClassBean){
        Observable.from(movieClassBean.getMessage().getData())
                .toMap(new Func1<MovieClassBean.MessageBean.DataBean, String>() {
                    @Override
                    public String call(MovieClassBean.MessageBean.DataBean dataBean) {
                        return dataBean.getName();
                    }
                }).subscribe(new Action1<Map<String, MovieClassBean.MessageBean.DataBean>>() {
            @Override
            public void call(Map<String, MovieClassBean.MessageBean.DataBean> stringDataBeanMap) {
                movieClassBeanMap = stringDataBeanMap;
                for (MovieClassBean.MessageBean.DataBean dataBean : movieClassBean.getMessage().getData()) {
                    tipList.add(dataBean.getName());
                }
            }
        });
    }

    //单击搜索按钮时激发该方法
    @Override
    public boolean onQueryTextSubmit(String query) {
        // TODO Auto-generated method stub
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        if(actorBeanMap != null && actorBeanMap.keySet().contains(query)){
            bundle.putString("TYPE", Config.searchTypeActor);
            bundle.putString("DATA", actorBeanMap.get(query).getID());
        }else if(movieClassBeanMap != null && movieClassBeanMap.keySet().contains(query)){
            bundle.putString("TYPE", Config.searchTypeClass);
            bundle.putString("DATA", movieClassBeanMap.get(query).getID());
        }else {
            bundle.putString("TYPE", Config.searchTypeNormal);
            bundle.putString("DATA", query);
        }
        intent.putExtras(bundle);
        intent.setClass(SearchActivity.this, MovieListActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
}
