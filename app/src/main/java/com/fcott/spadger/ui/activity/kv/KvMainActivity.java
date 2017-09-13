package com.fcott.spadger.ui.activity.kv;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ItemBean;
import com.fcott.spadger.model.bean.MenuBean;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.ui.adapter.ViewPagerAdapter;
import com.fcott.spadger.ui.fragment.FragmentFactroy;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.GeneralSettingUtil;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.NativeUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;
import com.fcott.spadger.utils.netstatus.NetChangeObserver;
import com.fcott.spadger.utils.netstatus.NetStateReceiver;
import com.fcott.spadger.utils.netstatus.NetUtils;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class KvMainActivity extends BaseActivity implements NetChangeObserver {
    public static final String TAG = KvMainActivity.class.getSimpleName();
    @Bind(R.id.vp_news)
    ViewPager vp_news;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.contain)
    View contain;

    private ViewPagerAdapter pagerAdapter;
    private List<String> titles;
    private List<Fragment> fragmentList;
    private MenuBean kvMenuBean;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main_kv;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        kvMenuBean = bundle.getParcelable("MENUBEAN");
    }

    @Override
    protected View getLoadingTargetView() {
        return contain;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //注册网络监听
        NetStateReceiver.registerObserver(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        if(kvMenuBean != null){
            requestComplete(kvMenuBean);
            return;
        }
        boolean needUpdate = NativeUtil.needUpdate(TAG);
        ACache mCache = ACache.get(KvMainActivity.this.getApplicationContext());
        //取出缓存
        String value = mCache.getAsString(TAG);
        //显示缓存
        if (!TextUtils.isEmpty(value) && !needUpdate) {
            requestComplete(JsoupUtil.parseMenu(value));
            return;
        }

        toggleShowLoading(true);
        RetrofitUtils.getInstance().create1(MainPageService.class)
                .getMainPage("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
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
                    public void onNext(String s) {
                        requestComplete(JsoupUtil.parseMenu(s));
                        toggleShowLoading(false);
                        //缓存
                        ACache aCache = ACache.get(KvMainActivity.this.getApplicationContext());
                        aCache.put(TAG, s);
                    }
                });
    }

    private void requestComplete(MenuBean menuBean){
        if(menuBean == null){
            return;
        }
        if (GeneralSettingUtil.isPerLoad() && NetUtils.isWifiConnected(KvMainActivity.this)) {
            Observable.from(menuBean.getNewpicList())
                    .map(new Func1<ItemBean, ItemBean>() {
                        @Override
                        public ItemBean call(ItemBean itemBean) {
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return itemBean;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ItemBean>() {
                        @Override
                        public void onCompleted() {
                            unsubscribe();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(ItemBean itemBean) {
                            ImageLoader.getInstance().perLoadImage(itemBean.getUrl(),null);
                        }
                    });
        }
        titles = Arrays.asList(getResources().getStringArray(R.array.news));
        fragmentList = FragmentFactroy.getFragment(menuBean);

        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragmentList,titles);
        vp_news.setAdapter(pagerAdapter);
        vp_news.setOffscreenPageLimit(fragmentList.size() - 1);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(vp_news);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp_news.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册网络监听
        NetStateReceiver.removeRegisterObserver(this);
    }

    @Override
    public void onNetConnected(NetUtils.NetType type) {
        if(type == NetUtils.NetType.WIFI){
            Toast.makeText(KvMainActivity.this,getString(R.string.auto_per_load),Toast.LENGTH_SHORT).show();
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
