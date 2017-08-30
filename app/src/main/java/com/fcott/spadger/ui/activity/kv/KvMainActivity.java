package com.fcott.spadger.ui.activity.kv;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.fcott.spadger.ui.fragment.MenuFragment;
import com.fcott.spadger.utils.GeneralSettingUtil;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;
import com.fcott.spadger.utils.netstatus.NetChangeObserver;
import com.fcott.spadger.utils.netstatus.NetStateReceiver;
import com.fcott.spadger.utils.netstatus.NetUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main_kv;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

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
                    }
                });
    }

    private void requestComplete(MenuBean menuBean){
        if(menuBean == null){
            return;
        }
        if (GeneralSettingUtil.isPerLoad() && NetUtils.isWifiConnected(KvMainActivity.this)) {
            for(ItemBean bean:menuBean.getNewpicList()){
                ImageLoader.getInstance().perLoadImage(KvMainActivity.this,bean.getUrl());
            }
        }
        titles = Arrays.asList(getResources().getStringArray(R.array.news));
        fragmentList = new ArrayList<>();
        fragmentList.add(MenuFragment.newInstance(menuBean.getPicList(),menuBean.getNewpicList(), MenuFragment.PICTURE));
        fragmentList.add(MenuFragment.newInstance(menuBean.getNovelList(),menuBean.getNewNovelList(), MenuFragment.NOVEL));
        fragmentList.add(MenuFragment.newInstance(menuBean.getVedioList(),menuBean.getNewVedioList(), MenuFragment.VEDIO));

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
            RequestManager requestManager = Glide.with(KvMainActivity.this);
            if(requestManager.isPaused()){
                requestManager.resumeRequests();
            }
        }else {
            Glide.with(KvMainActivity.this).pauseRequests();
        }
    }

    @Override
    public void onNetDisConnect() {
        Glide.with(KvMainActivity.this).pauseRequests();
    }
}
