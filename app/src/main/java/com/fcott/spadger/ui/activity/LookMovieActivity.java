package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.MovieTypeAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LookMovieActivity extends BaseActivity {
    public static final String TAG = LookMovieActivity.class.getSimpleName();

    private MovieTypeAdapter adapter = null;
    private final String[] typeList = new String[]{"A","B","C","D","演员","类型"};

    @Bind(R.id.rcy_movie_type)
    public RecyclerView recyclerView;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_look_movie;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {

        RequestBody moviceBody = new FormBody.Builder()
                .add("PageIndex","1")
                .add("PageSize","20")
                .add("Type","1")
                .add("ID","-1")
                .add("Data","")
                .build();

        RequestBody body = new FormBody.Builder()
                .add("PageIndex","1")
                .add("PageSize","60")
                .build();

        LookMovieService lookMovieService = RetrofitUtils.getInstance().create1(LookMovieService.class);
        ArrayList<Observable<String>> obList = new ArrayList<>();
//        obList.add(lookMovieService.requestMovie(moviceBody));
//        obList.add(lookMovieService.requestActor(body));
        obList.add(lookMovieService.requestClass(body));
        Observable.merge(obList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.w("response","completed");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.w("response",e.toString());
                    }

                    @Override
                    public void onNext(String s) {
                        LogUtil.log(TAG,s);
                    }
                });

        //列表适配器
        adapter = new MovieTypeAdapter(LookMovieActivity.this, Arrays.asList(typeList), false);
        adapter.setOnItemClickListener(new OnItemClickListeners<String>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, String data, int position) {
                Intent intent = new Intent();
                String id;
                switch (position){
                    case 0:
                        intent.setClass(LookMovieActivity.this,MovieListActivity.class);
                        id = Config.newMoviceId;
                        break;
                    case 1:
                        intent.setClass(LookMovieActivity.this,MovieListActivity.class);
                        id = Config.cartoonMoviceId;
                        break;
                    case 2:
                        intent.setClass(LookMovieActivity.this,MovieListActivity.class);
                        id = Config.mosaicMoviceId;
                        break;
                    case 3:
                        intent.setClass(LookMovieActivity.this,MovieListActivity.class);
                        id = Config.chineseMoviceId;
                        break;
                    case 4:
                        intent.setClass(LookMovieActivity.this,ActorActivity.class);
                        id = Config.noId;
                        break;
                    case 5:
                        intent.setClass(LookMovieActivity.this,ActorActivity.class);
                        id = Config.noId;
                        break;
                    default:
                        id = Config.noId;
                        break;
                }
                Bundle bundle = new Bundle();
                bundle.putString("ID",id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(LookMovieActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
