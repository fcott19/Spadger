package com.fcott.spadger.ui.activity.yiren;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MenuBean;
import com.fcott.spadger.model.bean.VedioListItemBean;
import com.fcott.spadger.model.http.YirenService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.ui.adapter.VedioListAdapter;
import com.fcott.spadger.ui.adapter.ViewPagerAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemLongClickListener;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.fragment.MenuFragment;
import com.fcott.spadger.ui.fragment.YirenMenuFragment;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.LogUtil;
import com.tencent.smtt.sdk.TbsVideo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class YirenMainActivity extends BaseActivity implements SearchView.OnQueryTextListener{
    public static final String TAG = YirenMainActivity.class.getSimpleName();
    @Bind(R.id.vp_news)
    ViewPager vp_news;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.nest)
    View contain;
    @Bind(R.id.sv)
    public SearchView sv;
    @Bind(R.id.rv_vedio_list)
    public RecyclerView recyclerView;

    private VedioListAdapter adapter = null;
    private ViewPagerAdapter pagerAdapter;
    private List<String> titles;
    private List<Fragment> fragmentList;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_yiren_main;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }
    @Override
    protected View getLoadingTargetView() {
        return contain;
    }

    @Override
    protected void initViews() {
        toggleShowLoading(true);
        initMovieList();
        RetrofitUtils.getInstance().create1(YirenService.class)
                .getMainPage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        LogUtil.log(e.toString());
                        toggleShowError("请求出错,点击重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                initViews();
                            }
                        });
                    }

                    @Override
                    public void onNext(String s) {
                        initMenu(JsoupUtil.parseYirenMenu(s));
                        adapter.setNewData(JsoupUtil.parseYirenVideo(s).getVedioList());
                        toggleShowLoading(false);
                    }
                });

        //设置该SearchView默认是否自动缩小为图标
        sv.setIconifiedByDefault(false);
        //为该SearchView组件设置事件监听器
        sv.setOnQueryTextListener(this);
        //设置该SearchView显示搜索按钮
        sv.setSubmitButtonEnabled(true);

        //设置该SearchView内默认显示的提示文本
        sv.setQueryHint("搜索");
    }

    private void initMenu(MenuBean menuBean){
        if(menuBean == null){
            return;
        }
        titles = Arrays.asList(getResources().getStringArray(R.array.news));
        fragmentList = new ArrayList<>();
        fragmentList.add(YirenMenuFragment.newInstance(menuBean.getPicList(), MenuFragment.PICTURE));
        fragmentList.add(YirenMenuFragment.newInstance(menuBean.getNovelList(), MenuFragment.NOVEL));
        fragmentList.add(YirenMenuFragment.newInstance(menuBean.getVedioList(), MenuFragment.VEDIO));

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
    private void initMovieList(){
        //列表适配器
        adapter = new VedioListAdapter(YirenMainActivity.this, new ArrayList<VedioListItemBean>(), false);
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
                                    if (url != null && TbsVideo.canUseTbsPlayer(YirenMainActivity.this)) {
                                        TbsVideo.openVideo(YirenMainActivity.this, url);
                                    }
                                }
                            });
                }else {
                    Toast.makeText(YirenMainActivity.this,getString(R.string.parse_error),Toast.LENGTH_SHORT).show();
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
                                    if (url != null && TbsVideo.canUseTbsPlayer(YirenMainActivity.this)) {
                                        ClipboardManager myClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                        myClipboard.setPrimaryClip(ClipData.newPlainText("movieurl", url));
                                        Toast.makeText(YirenMainActivity.this, "视频地址已经复制", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(YirenMainActivity.this,getString(R.string.parse_error),Toast.LENGTH_SHORT).show();
                }
            }
        });
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);//可防止Item切换
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(query.equals(""))
            return true;
        sv.setQuery("",false);
        sv.clearFocus();

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("TYPE", query);
        intent.putExtras(bundle);
        intent.setClass(YirenMainActivity.this, YirenVideoActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
