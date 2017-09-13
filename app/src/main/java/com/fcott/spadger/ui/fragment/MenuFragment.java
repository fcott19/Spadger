package com.fcott.spadger.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.TextView;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ItemBean;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.kv.NovelDetialActivity;
import com.fcott.spadger.ui.activity.kv.NovelExhibitionActivity;
import com.fcott.spadger.ui.activity.kv.PictureDetailActivity;
import com.fcott.spadger.ui.activity.kv.VedioExhibitionActivity;
import com.fcott.spadger.ui.adapter.MenuAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.JsoupUtil;
import com.tencent.smtt.sdk.TbsVideo;

import java.util.ArrayList;

import butterknife.Bind;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.fcott.spadger.ui.activity.kv.NovelExhibitionActivity.NOVEL_DETIAL_TITLE;
import static com.fcott.spadger.ui.activity.kv.NovelExhibitionActivity.NOVEL_DETIAL_URL;

public class MenuFragment extends BaseFragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    public static final String NOVEL = "NOVEL";
    public static final String PICTURE = "PICTURE";
    public static final String VEDIO = "VEDIO";
    public static final String TYPE = "TYPE";// 区别是图片还是小说的TAG

    public static final String TITLE = "TITLE";
    public static final String URL = "URL";

    @Bind(R.id.type_item_menu)
    public RecyclerView mRvMenu;
    @Bind(R.id.type_item_newInfo)
    public RecyclerView mRvNewInfo;
    @Bind(R.id.tv_title)
    public TextView tvTitle;

    MenuAdapter menuAdapter = null;//菜单选择Adapter
    MenuAdapter newInfoAdapter = null;//新更新信息Adapter
    private ArrayList<ItemBean> menuData = new ArrayList<>();//菜单数据
    private ArrayList<ItemBean> newData = new ArrayList<>();//新更新的数据
    private String TAG = "";//类型标志
    private Handler handler = new Handler();
    private boolean needWait = true;
    private int waitTime;

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance(ArrayList<ItemBean> menuList, ArrayList<ItemBean> newList, String tag) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, menuList);
        args.putParcelableArrayList(ARG_PARAM2, newList);
        args.putString(ARG_PARAM3, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            menuData = getArguments().getParcelableArrayList(ARG_PARAM1);
            newData = getArguments().getParcelableArrayList(ARG_PARAM2);
            TAG = getArguments().getString(ARG_PARAM3);
            //设置标题
            if (TAG.equals(VEDIO)) {
                waitTime = 200;
            } else if (TAG.equals(PICTURE)) {
                waitTime = 0;
            } else if (TAG.equals(NOVEL)) {
                waitTime = 100;
            }
            needWait = true;
        }
    }

    @Override
    public int getFragmentLayoutID() {
        return R.layout.fragment_novel;
    }

    @Override
    public void initViews() {
        if(needWait) {
            needWait = false;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initViews();
                }
            },waitTime);
            return;
        }

        //设置标题
        if (TAG.equals(VEDIO)) {
            tvTitle.setText(getResources().getString(R.string.new_video));
        } else if (TAG.equals(PICTURE)) {
            tvTitle.setText(getResources().getString(R.string.new_picture));
        } else if (TAG.equals(NOVEL)) {
            tvTitle.setText(getResources().getString(R.string.new_novel));
        }
        //menu列表展示
        menuAdapter = new MenuAdapter(baseActivity, menuData, false);
        menuAdapter.setOnItemClickListener(new OnItemClickListeners<ItemBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, ItemBean data, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (TAG.equals(VEDIO)) {//电影
                    bundle.putString("TYPE", null);
                    intent = new Intent(baseActivity, VedioExhibitionActivity.class);
                } else if (TAG.equals(PICTURE)) {//图片
                    bundle.putString(TYPE, PICTURE);
                    intent = new Intent(baseActivity, NovelExhibitionActivity.class);
                } else if (TAG.equals(NOVEL)) {//小说
                    bundle.putString(TYPE, NOVEL);
                    intent = new Intent(baseActivity, NovelExhibitionActivity.class);
                }
                bundle.putString(TITLE, data.getTitle());
                bundle.putString(URL, data.getUrl());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);//可防止Item切换
        layoutManager.setAutoMeasureEnabled(true);
        mRvMenu.setLayoutManager(layoutManager);
        mRvMenu.setAdapter(menuAdapter);
        //新内容列表展示
        newInfoAdapter = new MenuAdapter(baseActivity, newData, false);
        newInfoAdapter.setOnItemClickListener(new OnItemClickListeners<ItemBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, ItemBean data, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (TAG.equals(VEDIO)) {
                    goVedio(data.getUrl());
                    return;
                } else if (TAG.equals(PICTURE)) {
                    intent = new Intent(baseActivity, PictureDetailActivity.class);
                    bundle.putString(Config.DATA_FROM,Config.DATA_FROM_KV);
                } else if (TAG.equals(NOVEL)) {
                    intent = new Intent(baseActivity, NovelDetialActivity.class);
                    bundle.putString(Config.DATA_FROM,Config.DATA_FROM_KV);
                }
                bundle.putString(NOVEL_DETIAL_URL, data.getUrl());
                bundle.putString(NOVEL_DETIAL_TITLE, data.getTitle());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        final LinearLayoutManager layoutManager1 = new LinearLayoutManager(baseActivity);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager1.setAutoMeasureEnabled(true);
        mRvNewInfo.setLayoutManager(layoutManager1);
        mRvNewInfo.setAdapter(newInfoAdapter);
    }

    private void goVedio(String url) {
        RetrofitUtils.getInstance().create1(MainPageService.class)
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
                    }

                    @Override
                    public void onNext(String s) {
                        if (TbsVideo.canUseTbsPlayer(baseActivity)) {
                            TbsVideo.openVideo(baseActivity, JsoupUtil.parseVideoDetial(s));
                        }
                    }
                });
    }
}
