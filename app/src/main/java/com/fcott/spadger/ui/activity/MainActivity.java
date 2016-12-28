package com.fcott.spadger.ui.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MenuBean;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.ViewPagerAdapter;
import com.fcott.spadger.ui.fragment.MenuFragment;
import com.fcott.spadger.utils.JsoupUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    @Bind(R.id.vp_news)
    ViewPager vp_news;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;

    private ViewPagerAdapter pagerAdapter;
    private List<String> titles;
    private List<Fragment> fragmentList;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {
        RetrofitUtils.getInstance().create1(MainPageService.class)
                .getMainPage("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.w("response","completed");
                        requestComplete(null);
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.w("response",e.toString());
                    }

                    @Override
                    public void onNext(String s) {
                        requestComplete(JsoupUtil.parseMenu(s));
                    }
                });
    }

    private void requestComplete(MenuBean menuBean){
        if(menuBean == null){
            return;
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

}
