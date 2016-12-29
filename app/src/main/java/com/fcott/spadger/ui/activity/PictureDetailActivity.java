package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.fcott.spadger.R;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.PictureAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.fcott.spadger.ui.activity.NovelExhibitionActivity.NOVEL_DETIAL_TITLE;
import static com.fcott.spadger.ui.activity.NovelExhibitionActivity.NOVEL_DETIAL_URL;

public class PictureDetailActivity extends BaseActivity {
    private static final String ACACHE_TAG = "CACHE_PIC";

    @Bind(R.id.rv_imgs)
    public RecyclerView recyclerView;

    private String url = "";
    private String title = "";
    private PictureAdapter adapter;
    private ArrayList<String> dataList = null;

    @Override
    protected View getLoadingTargetView() {
        return recyclerView;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_picture_detail;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        url = bundle.getString(NOVEL_DETIAL_URL);
        title = bundle.getString(NOVEL_DETIAL_TITLE);
    }

    @Override
    protected void initViews() {
        adapter = new PictureAdapter(PictureDetailActivity.this,new ArrayList<String>(),false);
        adapter.setOnItemClickListener(new OnItemClickListeners<String>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, String data, int position) {
                Intent intent = new Intent(PictureDetailActivity.this,PictureSinglelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(PictureSinglelActivity.URL_TAG,dataList);
                bundle.putInt(PictureSinglelActivity.POSITION_TAG,position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(PictureDetailActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        requestData(url);
    }

    //请求数据，更新界面
    private void requestData(final String url) {
        ACache mCache = ACache.get(PictureDetailActivity.this.getApplicationContext());
        String value = mCache.getAsString(ACACHE_TAG + url);//取出缓存

        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            dataList = JsoupUtil.parsePictureDetial(value);
            if (!dataList.isEmpty()) {
                adapter.setNewData(dataList);
            }
            return;
        } else {
            toggleShowLoading(true);
        }

        RetrofitUtils.getInstance().create1(MainPageService.class)
                .getData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
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
                    public void onNext(String s) {
                        dataList = JsoupUtil.parsePictureDetial(s);
                        for(String string:dataList){
                            ImageLoader.getInstance().preLoad(PictureDetailActivity.this,string);
                        }
                        adapter.setNewData(dataList);
                    }
                });
    }
}
