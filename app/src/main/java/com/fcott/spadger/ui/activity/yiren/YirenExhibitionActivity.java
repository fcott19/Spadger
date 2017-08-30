package com.fcott.spadger.ui.activity.yiren;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.Target;
import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.NovelListBean;
import com.fcott.spadger.model.bean.NovelListItemBean;
import com.fcott.spadger.model.http.YirenService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.ui.activity.kv.NovelDetialActivity;
import com.fcott.spadger.ui.activity.kv.NovelExhibitionActivity;
import com.fcott.spadger.ui.activity.kv.PictureDetailActivity;
import com.fcott.spadger.ui.adapter.NovelListAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemLongClickListener;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.fragment.MenuFragment;
import com.fcott.spadger.ui.widget.PageController;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.GeneralSettingUtil;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;
import com.fcott.spadger.utils.netstatus.NetChangeObserver;
import com.fcott.spadger.utils.netstatus.NetStateReceiver;
import com.fcott.spadger.utils.netstatus.NetUtils;

import java.util.ArrayList;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

//小说展示界面，由于图片界面与此相同。故也是图片展示界面
public class YirenExhibitionActivity extends BaseActivity implements
        NetChangeObserver,PageController.ObserverPageListener{
    public static final String NOVEL_DETIAL_URL = "DETIAL_URL";
    public static final String NOVEL_DETIAL_TITLE = "DETIAL_TITLE";

    private String currentUrl = "";
    private String title = "";//标题
    private String baseUrl = "";//地址URL
    private String tyep = "";//用于区分 小说/图片
    private NovelListAdapter adapter = null;
    private Subscription subscription;
    private ArrayList<Target> targetList = new ArrayList<>();

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
        return R.layout.activity_novel_exhibition;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        title = bundle.getString(MenuFragment.TITLE);
        baseUrl = bundle.getString(MenuFragment.URL);
        tyep = bundle.getString(MenuFragment.TYPE);
        currentUrl = baseUrl;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //注册网络监听
        NetStateReceiver.registerObserver(this);
        super.onCreate(savedInstanceState);
        pageController.setObserverPageListener(this);
    }
    
    @Override
    protected void initViews() {
        //列表适配器
        adapter = new NovelListAdapter(YirenExhibitionActivity.this, new ArrayList<NovelListItemBean>(), false);
        adapter.setOnItemClickListener(new OnItemClickListeners<NovelListItemBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, NovelListItemBean data, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (tyep.equals(MenuFragment.NOVEL)) {
                    intent = new Intent(YirenExhibitionActivity.this, NovelDetialActivity.class);
                } else {
                    intent = new Intent(YirenExhibitionActivity.this, PictureDetailActivity.class);
                }
                bundle.putString(Config.DATA_FROM,Config.DATA_FROM_YIREN);
                bundle.putString(NOVEL_DETIAL_URL, data.getUrl());
                bundle.putString(NOVEL_DETIAL_TITLE, data.getTitle());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        adapter.setItemLongClickListener(new OnItemLongClickListener<NovelListItemBean>() {
            @Override
            public void onItemLongClick(ViewHolder viewHolder, NovelListItemBean data, int position) {
                if (!tyep.equals(MenuFragment.PICTURE))
                    return;
                ImageLoader.getInstance().perLoadYirenImage(YirenExhibitionActivity.this,data.getUrl());
                Toast.makeText(mContext, "开始加载本图集", Toast.LENGTH_SHORT).show();
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(YirenExhibitionActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        requestData(currentUrl);
    }

    //请求数据，更新界面
    private void requestData(final String url) {
        if(subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        for(Target target:targetList){
            Glide.clear(target);
        }

        ACache mCache = ACache.get(YirenExhibitionActivity.this.getApplicationContext());
        String value = mCache.getAsString(url);//取出缓存

        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            NovelListBean novelListBean = JsoupUtil.parseYirenList(value);
            ArrayList arrayList = novelListBean.getNovelList();
            if (!arrayList.isEmpty()) {
                pageController.setMaxPageIndex(novelListBean.getPageControlBean().getTotalPage());
                adapter.setNewData(arrayList);
            }
        } else {
            toggleShowLoading(true);
        }

        subscription = RetrofitUtils.getInstance().create1(YirenService.class)
                .getData(url)
                .map(new Func1<String, NovelListBean>() {
                    @Override
                    public NovelListBean call(String s) {
                        //缓存
                        ACache aCache = ACache.get(getApplicationContext());
                        aCache.put(url, s);
                        return JsoupUtil.parseYirenList(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMapEager(new Func1<NovelListBean, Observable<NovelListItemBean>>() {
                    @Override
                    public Observable<NovelListItemBean> call(NovelListBean novelListBean) {
                        adapter.setNewData(novelListBean.getNovelList());
                        pageController.setMaxPageIndex(novelListBean.getPageControlBean().getTotalPage());
                        toggleShowLoading(false);
                        return Observable.from(novelListBean.getNovelList());
                    }
                }).subscribe(new Subscriber<NovelListItemBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        toggleShowError("请求出错,点击重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initViews();
                            }
                        });
                    }

                    @Override
                    public void onNext(NovelListItemBean novelListItemBean) {
                        if (GeneralSettingUtil.isPerLoad() && tyep.equals(MenuFragment.PICTURE) && NetUtils.isWifiConnected(YirenExhibitionActivity.this)) {
                            targetList.add(ImageLoader.getInstance().preLoad(YirenExhibitionActivity.this, novelListItemBean.getUrl()));
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册网络监听
        NetStateReceiver.removeRegisterObserver(this);
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    @Override
    public void onNetConnected(NetUtils.NetType type) {
        if(type == NetUtils.NetType.WIFI){
            RequestManager requestManager = Glide.with(YirenExhibitionActivity.this);
            if(requestManager.isPaused()){
                requestManager.resumeRequests();
            }
        }else {
            Glide.with(YirenExhibitionActivity.this).pauseRequests();
        }
    }

    @Override
    public void onNetDisConnect() {
        Glide.with(YirenExhibitionActivity.this).pauseRequests();
    }

    @Override
    public void goPage(int page) {
        if(page == 1)
            currentUrl = baseUrl;
        else
            currentUrl = baseUrl + "/index_" + page + ".html";
        requestData(currentUrl);
    }
}
