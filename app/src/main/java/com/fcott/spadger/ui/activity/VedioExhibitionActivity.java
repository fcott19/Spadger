package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ItemBean;
import com.fcott.spadger.model.bean.VedioListItemBean;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.MenuAdapter;
import com.fcott.spadger.ui.adapter.VedioListAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.ParseUtil;
import com.pili.pldroid.player.AVOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VedioExhibitionActivity extends BaseActivity {
    private static final String ACACHE_TAG = "ACACHE_VEDIO";
    public static final String TITLE = "TITLE";
    public static final String URL = "URL";

    private String title = "";//标题
    private String url = "";//地址URL

    @Bind(R.id.rv_vedio_list)
    public RecyclerView recyclerView;

    private VedioListAdapter adapter = null;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_vedio_exhibition;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        title = bundle.getString(TITLE);
        url = bundle.getString(URL);
    }

    @Override
    protected void initViews() {
        ACache mCache = ACache.get(VedioExhibitionActivity.this.getApplicationContext());
        String value = mCache.getAsString(ACACHE_TAG + url);//取出缓存

        if (!TextUtils.isEmpty(value)) {
            adapter.setNewData(JsoupUtil.parseVideoList(value).getVedioList());
        }

        //menu列表展示
        adapter = new VedioListAdapter(VedioExhibitionActivity.this,new ArrayList<VedioListItemBean>(),false);
        adapter.setOnItemClickListener(new OnItemClickListeners<VedioListItemBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, VedioListItemBean data, int position) {
//                Intent intent = new Intent(VedioExhibitionActivity.this,PLVideoViewActivity.class);
//                intent.putExtra("videoPath","");
//                intent.putExtra("liveStreaming",0);
//                intent.putExtra("mediaCodec", AVOptions.MEDIA_CODEC_SW_DECODE);
//                startActivity(intent);
                RetrofitUtils.getInstance().create1(MainPageService.class)
                        .getVideo(data.getUrl())
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
                                LogUtil.log(JsoupUtil.parseVideoDetial(s));
                            }
                        });
            }
        });
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);//可防止Item切换
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        RetrofitUtils.getInstance().create1(MainPageService.class)
                .getVideo(url)
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
                        //缓存
                        ACache aCache = ACache.get(VedioExhibitionActivity.this.getApplicationContext());
                        aCache.put(ACACHE_TAG + url, s);
                        //更新界面
                        adapter.setNewData(JsoupUtil.parseVideoList(s).getVedioList());
                    }
                });
    }
}
