package com.fcott.spadger.ui.activity.yiren;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.VedioListBean;
import com.fcott.spadger.model.bean.VedioListItemBean;
import com.fcott.spadger.model.http.YirenService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.ui.adapter.VedioListAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemLongClickListener;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.fragment.MenuFragment;
import com.fcott.spadger.ui.widget.PageController;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;
import com.tencent.smtt.sdk.TbsVideo;

import java.util.ArrayList;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class YirenVideoActivity extends BaseActivity implements PageController.ObserverPageListener {

    private String type = "";//标题
    private String title = "";//标题
    private String baseUrl = "";//地址URL
    private VedioListAdapter adapter = null;
    private VedioListBean vedioListBean = null;

    @Bind(R.id.nest)
    public View nest;
    @Bind(R.id.rv_vedio_list)
    public RecyclerView recyclerView;
    @Bind(R.id.rl_pagecontrol)
    public PageController pageController;

    @Override
    protected View getLoadingTargetView() {
        return nest;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_yiren_video;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        title = bundle.getString(MenuFragment.TITLE);
        baseUrl = bundle.getString(MenuFragment.URL);
        type = bundle.getString("TYPE");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageController.setObserverPageListener(this);
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
        mActionBar.setTitle("列表");

        //列表适配器
        adapter = new VedioListAdapter(YirenVideoActivity.this, new ArrayList<VedioListItemBean>(), false);//
        adapter.setOnItemClickListener(new OnItemClickListeners<VedioListItemBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, VedioListItemBean data, int position) {
                String[] strings = data.getUrl().split("\\/");
                if (strings != null && strings.length > 0) {
                    RetrofitUtils.getInstance().create1(YirenService.class)
                            .getVideoUrl("35", strings[strings.length - 1].replace(".html", "").trim(),"0")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<String>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(String s) {
                                    String url = JsoupUtil.parseYirenVideoUrl(s);
                                    if (url != null && TbsVideo.canUseTbsPlayer(YirenVideoActivity.this)) {
                                        TbsVideo.openVideo(YirenVideoActivity.this, url);
                                    }
                                }
                            });
                }else {
                    Toast.makeText(YirenVideoActivity.this,getString(R.string.parse_error),Toast.LENGTH_SHORT).show();
                }
            }
        });
        adapter.setItemLongClickListener(new OnItemLongClickListener<VedioListItemBean>() {
            @Override
            public void onItemLongClick(ViewHolder viewHolder, VedioListItemBean data, int position) {
                String[] strings = data.getUrl().split("\\/");
                if (strings != null && strings.length > 0) {
                    RetrofitUtils.getInstance().create1(YirenService.class)
                            .getVideoUrl("35", strings[strings.length - 1].replace(".html", "").trim(),"0")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<String>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(String s) {
                                    String url = JsoupUtil.parseYirenVideoUrl(s);
                                    if (url != null && TbsVideo.canUseTbsPlayer(YirenVideoActivity.this)) {
                                        ClipboardManager myClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        myClipboard.setPrimaryClip(ClipData.newPlainText("movieurl", url));
                                        Toast.makeText(YirenVideoActivity.this, "视频地址已经复制", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(YirenVideoActivity.this,getString(R.string.parse_error),Toast.LENGTH_SHORT).show();
                }
            }
        });
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);//可防止Item切换
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        if(type != null){
            searchData(type,"1");
        }else {
            requestData(baseUrl);
        }
    }

    //http://www.yiren02.com/e/search/result/?searchid=477
    private String searchId;
    private void searchData(final String query,final String page){
        ACache mCache = ACache.get(YirenVideoActivity.this.getApplicationContext());
        String value = mCache.getAsString(query+page);//取出缓存

        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            vedioListBean = JsoupUtil.parseYirenVideo(value);
            ArrayList arrayList = vedioListBean.getVedioList();
            if (!arrayList.isEmpty()) {
                pageController.setMaxPageIndex(vedioListBean.getPageControlBean().getTotalPage());
                adapter.setNewData(arrayList);
                return;
            }
        } else {
            toggleShowLoading(true);
        }
        if(page.equals("1")){
            RequestBody body = new FormBody.Builder()
                    .add("keyboard", query)
                    .add("show", "title")
                    .add("page",page)
                    .build();
            RetrofitUtils.getInstance().create2(YirenService.class)
                    .searchData(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {

                        }
                        @Override
                        public void onError(Throwable e) {
                            toggleShowError(getString(R.string.no_data));
                            LogUtil.log(e.toString());
                        }

                        @Override
                        public void onNext(String s) {
                            //缓存
                            ACache aCache = ACache.get(YirenVideoActivity.this.getApplicationContext());
                            aCache.put(query+page, s);

                            vedioListBean = JsoupUtil.parseYirenSearch(s);
                            pageController.setMaxPageIndex(vedioListBean.getPageControlBean().getTotalPage());
                            searchId = vedioListBean.getPageControlBean().getJumpUrl();
                            //更新界面
                            adapter.setNewData(vedioListBean.getVedioList());
                            toggleShowLoading(false);

                            for (VedioListItemBean bean : vedioListBean.getVedioList()) {
                                ImageLoader.getInstance().preLoad(YirenVideoActivity.this, bean.getImgUrl());
                                LogUtil.log(bean.getImgUrl());
                            }
                        }
                    });
        }else if(searchId != null){
            RetrofitUtils.getInstance().create1(YirenService.class)
                    .searchData(searchId,page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<String>() {
                        @Override
                        public void onCompleted() {

                        }
                        @Override
                        public void onError(Throwable e) {
                            toggleShowError(getString(R.string.no_data));
                            LogUtil.log(e.toString());
                        }

                        @Override
                        public void onNext(String s) {
                            //缓存
                            ACache aCache = ACache.get(YirenVideoActivity.this.getApplicationContext());
                            aCache.put(query+page, s);

                            vedioListBean = JsoupUtil.parseYirenSearch(s);
                            pageController.setMaxPageIndex(vedioListBean.getPageControlBean().getTotalPage());
                            //更新界面
                            adapter.setNewData(vedioListBean.getVedioList());
                            toggleShowLoading(false);

                            for (VedioListItemBean bean : vedioListBean.getVedioList()) {
                                ImageLoader.getInstance().preLoad(YirenVideoActivity.this, bean.getImgUrl());
                                LogUtil.log(bean.getImgUrl());
                            }
                        }
                    });
        }
    }

    //请求数据，更新界面
    private void requestData(final String url) {
        ACache mCache = ACache.get(YirenVideoActivity.this.getApplicationContext());
        String value = mCache.getAsString(url);//取出缓存

        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            vedioListBean = JsoupUtil.parseYirenVideo(value);
            ArrayList arrayList = vedioListBean.getVedioList();
            if (!arrayList.isEmpty()) {
                pageController.setMaxPageIndex(vedioListBean.getPageControlBean().getTotalPage());
                adapter.setNewData(arrayList);
            }
        } else {
            toggleShowLoading(true);
        }

        RetrofitUtils.getInstance().create1(YirenService.class)
                .getData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.w("response", "completed");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("response", e.toString());
                        toggleShowLoading(false);
                    }

                    @Override
                    public void onNext(String s) {
                        //缓存
                        ACache aCache = ACache.get(YirenVideoActivity.this.getApplicationContext());
                        aCache.put(url, s);

                        vedioListBean = JsoupUtil.parseYirenVideo(s);
                        pageController.setMaxPageIndex(vedioListBean.getPageControlBean().getTotalPage());
                        //更新界面
                        adapter.setNewData(vedioListBean.getVedioList());
                        toggleShowLoading(false);

                        for (VedioListItemBean bean : vedioListBean.getVedioList()) {
                            ImageLoader.getInstance().preLoad(YirenVideoActivity.this, bean.getImgUrl());
                        }
                    }
                });
    }

    @Override
    public void goPage(int page) {
        if(type != null){
            searchData(type,String.valueOf(page));
        }else {
            if (page == 1)
                requestData(baseUrl);
            else
                requestData(baseUrl + "/index_" + page + ".html");
        }
    }
}
