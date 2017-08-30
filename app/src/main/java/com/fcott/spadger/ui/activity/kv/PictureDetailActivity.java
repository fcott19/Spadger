package com.fcott.spadger.ui.activity.kv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.YirenService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.ui.adapter.PictureAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PictureDetailActivity extends BaseActivity {
    public static final String TAG = PictureDetailActivity.class.getSimpleName();

    @Bind(R.id.rv_imgs)
    public RecyclerView recyclerView;

    private String url = "";
    private String title = "";
    private String dataFrom = "";//数据来源
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
        url = bundle.getString(NovelExhibitionActivity.NOVEL_DETIAL_URL);
        title = bundle.getString(NovelExhibitionActivity.NOVEL_DETIAL_TITLE);
        dataFrom = bundle.getString(Config.DATA_FROM);
    }

    @Override
    protected void initViews() {
        adapter = new PictureAdapter(PictureDetailActivity.this, new ArrayList<String>(), false);
        adapter.setOnItemClickListener(new OnItemClickListeners<String>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, String data, int position) {
                Intent intent = new Intent(PictureDetailActivity.this, PictureSinglelActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(PictureSinglelActivity.URL_TAG, dataList);
                bundle.putInt(PictureSinglelActivity.POSITION_TAG, position);
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(PictureDetailActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        requestData(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            if (requestCode == 0) {
                recyclerView.scrollToPosition(data.getIntExtra("position", 0));
            }
        }
    }

    //请求数据，更新界面
    private void requestData(final String url) {
        ACache mCache = ACache.get(PictureDetailActivity.this.getApplicationContext());
        String value = mCache.getAsString(url);//取出缓存

        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            if(dataFrom.equals(Config.DATA_FROM_KV)){
                dataList = JsoupUtil.parsePictureDetial(value);
            }else if(dataFrom.equals(Config.DATA_FROM_YIREN)){
                dataList = JsoupUtil.parseYirenPic(value);
            }
            if (!dataList.isEmpty()) {
                adapter.setNewData(dataList);
                for (String string : dataList) {
                    ImageLoader.getInstance().preLoad(PictureDetailActivity.this, string);
                }
                return;
            }
        } else {
            toggleShowLoading(true);
        }

        Observable<String> ob;
        if(dataFrom.equals(Config.DATA_FROM_KV)){
            ob = RetrofitUtils.getInstance().create1(MainPageService.class)
                    .getData(url);
        }else if(dataFrom.equals(Config.DATA_FROM_YIREN)){
            ob = RetrofitUtils.getInstance().create1(YirenService.class)
                    .getData(url);
        }else {
            return;
        }
        ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        toggleShowLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("response", e.toString());
                    }

                    @Override
                    public void onNext(String s) {
                        //缓存
                        ACache aCache = ACache.get(getApplicationContext());
                        aCache.put(url, s);
                        if(dataFrom.equals(Config.DATA_FROM_KV)){
                            dataList = JsoupUtil.parsePictureDetial(s);
                        }else if(dataFrom.equals(Config.DATA_FROM_YIREN)){
                            dataList = JsoupUtil.parseYirenPic(s);
                        }
                        for (String string : dataList) {
                            ImageLoader.getInstance().preLoad(PictureDetailActivity.this, string);
                        }
                        adapter.setNewData(dataList);
                    }
                });
    }
}
