package com.fcott.spadger.ui.activity.kv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.Target;
import com.fcott.spadger.App;
import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.NovelListBean;
import com.fcott.spadger.model.bean.NovelListItemBean;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.ui.adapter.NovelListAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemLongClickListener;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.fragment.MenuFragment;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.GeneralSettingUtil;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;
import com.fcott.spadger.utils.netstatus.NetChangeObserver;
import com.fcott.spadger.utils.netstatus.NetStateReceiver;
import com.fcott.spadger.utils.netstatus.NetUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

//小说展示界面，由于图片界面与此相同。故也是图片展示界面
public class NovelExhibitionActivity extends BaseActivity implements NetChangeObserver {
    public static final String NOVEL_DETIAL_URL = "DETIAL_URL";
    public static final String NOVEL_DETIAL_TITLE = "DETIAL_TITLE";

    private String title = "";//标题
    private String url = "";//地址URL
    private String tyep = "";//用于区分 小说/图片
    private NovelListAdapter adapter = null;
    private NovelListBean novelListBean = null;
    private Subscription subscription;
    private ArrayList<Target> targetList = new ArrayList<>();

    @Bind(R.id.nest)
    public View nest;
    @Bind(R.id.rv_vedio_list)
    public RecyclerView recyclerView;
    @Bind(R.id.et_page_number)
    public EditText etPageNumber;

    @OnClick({R.id.tv_first_page, R.id.tv_pre_page, R.id.tv_next_page, R.id.tv_last_page})
    public void onClick(View view) {
        if (novelListBean == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_first_page:
                if (!novelListBean.getPageControlBean().getFirstPageUrl().isEmpty()) {
                    url = novelListBean.getPageControlBean().getFirstPageUrl();
                    requestData(novelListBean.getPageControlBean().getFirstPageUrl());
                } else {
                    Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_pre_page:
                if (!novelListBean.getPageControlBean().getPrePage().isEmpty()) {
                    url = novelListBean.getPageControlBean().getPrePage();
                    requestData(novelListBean.getPageControlBean().getPrePage());
                } else {
                    Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_next_page:
                if (!novelListBean.getPageControlBean().getNextPage().isEmpty()) {
                    url = novelListBean.getPageControlBean().getNextPage();
                    requestData(novelListBean.getPageControlBean().getNextPage());
                } else {
                    Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.already_last), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_last_page:
                if (!novelListBean.getPageControlBean().getLastPageUrl().isEmpty()) {
                    url = novelListBean.getPageControlBean().getLastPageUrl();
                    requestData(novelListBean.getPageControlBean().getLastPageUrl());
                } else {
                    Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.already_last), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

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
        url = bundle.getString(MenuFragment.URL);
        tyep = bundle.getString(MenuFragment.TYPE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //注册网络监听
        NetStateReceiver.registerObserver(this);
        super.onCreate(savedInstanceState);
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
        adapter = new NovelListAdapter(NovelExhibitionActivity.this, new ArrayList<NovelListItemBean>(), false);
        adapter.setOnItemClickListener(new OnItemClickListeners<NovelListItemBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, NovelListItemBean data, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (tyep.equals(MenuFragment.NOVEL)) {
                    intent = new Intent(NovelExhibitionActivity.this, NovelDetialActivity.class);
                } else {
                    intent = new Intent(NovelExhibitionActivity.this, PictureDetailActivity.class);
                }
                bundle.putString(Config.DATA_FROM,Config.DATA_FROM_KV);
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
                ImageLoader.getInstance().perLoadImage(data.getUrl(),targetList);
                Toast.makeText(mContext, "开始加载本图集", Toast.LENGTH_SHORT).show();
                viewHolder.setBgRes(R.id.bg,R.drawable.selector_btn_preload);
                App.getInstance().perLoadList.add(data.getUrl());
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(NovelExhibitionActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        requestData(url);

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
        if (pageNumber < 1 || pageNumber > novelListBean.getPageControlBean().getTotalPage()) {
            Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.input_error), Toast.LENGTH_SHORT).show();
        } else if (pageNumber == 1) {
            if (!novelListBean.getPageControlBean().getFirstPageUrl().isEmpty()) {
                requestData(novelListBean.getPageControlBean().getFirstPageUrl());
            } else {
                Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
            }
        } else {
            requestData(novelListBean.getPageControlBean().getJumpUrl().replace("{!page!}", etPageNumber.getText()));
        }
    }


    //请求数据，更新界面
    private void requestData(final String url) {
        final boolean needPerLoad = GeneralSettingUtil.isPerLoad() && tyep.equals(MenuFragment.PICTURE) && NetUtils.isWifiConnected(NovelExhibitionActivity.this);
        if(subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        for(Target target:targetList){
            Glide.clear(target);
        }

        ACache mCache = ACache.get(NovelExhibitionActivity.this.getApplicationContext());
        final String value = mCache.getAsString(url);//取出缓存

        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            novelListBean = JsoupUtil.parseNovelList(value);
            ArrayList arrayList = novelListBean.getNovelList();
            if (!arrayList.isEmpty()) {
                adapter.setNewData(arrayList);
                //设置当前页数
                etPageNumber.setText(novelListBean.getPageControlBean().getCurrentPage());
            }
        } else {
            toggleShowLoading(true);
        }

        subscription = RetrofitUtils.getInstance().create1(MainPageService.class)
                .getData(url)
                .map(new Func1<String, NovelListBean>() {
                    @Override
                    public NovelListBean call(String s) {
                        //缓存
                        ACache aCache = ACache.get(getApplicationContext());
                        aCache.put(url, s);
                        return JsoupUtil.parseNovelList(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMapEager(new Func1<NovelListBean, Observable<NovelListItemBean>>() {
                    @Override
                    public Observable<NovelListItemBean> call(NovelListBean bean) {
                        novelListBean = bean;
                        adapter.setNewData(bean.getNovelList());
                        etPageNumber.setText(bean.getPageControlBean().getCurrentPage());
                        toggleShowLoading(false);
                        return Observable.from(bean.getNovelList());
                    }
                }).observeOn(Schedulers.io())
                .map(new Func1<NovelListItemBean, NovelListItemBean>() {
                    @Override
                    public NovelListItemBean call(NovelListItemBean novelListItemBean) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return novelListItemBean;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NovelListItemBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(value == null){
                            toggleShowError("请求出错,点击重试", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    initViews();
                                }
                            });
                        }
                    }

                    @Override
                    public void onNext(NovelListItemBean novelListItemBean) {
                        if (needPerLoad)
                            ImageLoader.getInstance().perLoadImage(novelListItemBean.getUrl(),targetList);
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
            Toast.makeText(NovelExhibitionActivity.this,getString(R.string.auto_per_load),Toast.LENGTH_SHORT).show();
            RequestManager requestManager = Glide.with(getApplicationContext());
            if(requestManager.isPaused()){
                requestManager.resumeRequests();
            }
        }else {
            Glide.with(getApplicationContext()).pauseRequests();
        }
    }

    @Override
    public void onNetDisConnect() {
        Glide.with(getApplicationContext()).pauseRequests();
    }
}
