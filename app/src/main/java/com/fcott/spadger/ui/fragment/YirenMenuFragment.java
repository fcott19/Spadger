package com.fcott.spadger.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ItemBean;
import com.fcott.spadger.ui.activity.yiren.YirenExhibitionActivity;
import com.fcott.spadger.ui.activity.yiren.YirenVideoActivity;
import com.fcott.spadger.ui.adapter.MenuAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.ArrayList;

import butterknife.Bind;

public class YirenMenuFragment extends BaseFragment{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String NOVEL = "NOVEL";
    public static final String PICTURE = "PICTURE";
    public static final String VEDIO = "VEDIO";
    public static final String TYPE = "TYPE";// 区别是图片还是小说的TAG

    public static final String TITLE = "TITLE";
    public static final String URL = "URL";

    @Bind(R.id.type_item_menu)
    public RecyclerView mRvMenu;

    MenuAdapter menuAdapter = null;//菜单选择Adapter
    private ArrayList<ItemBean> menuData = new ArrayList<>();//菜单数据
    private String TAG = "";//类型标志

    public YirenMenuFragment() {
        // Required empty public constructor
    }

    public static YirenMenuFragment newInstance(ArrayList<ItemBean> menuList, String tag) {
        YirenMenuFragment fragment = new YirenMenuFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, menuList);
        args.putString(ARG_PARAM2, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getFragmentLayoutID() {
        return R.layout.fragment_yiren_menu;
    }

    @Override
    public void initViews() {
        if (getArguments() != null) {
            menuData = getArguments().getParcelableArrayList(ARG_PARAM1);
            TAG = getArguments().getString(ARG_PARAM2);
        }

        //menu列表展示
        menuAdapter = new MenuAdapter(baseActivity, menuData, false);
        menuAdapter.setOnItemClickListener(new OnItemClickListeners<ItemBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, ItemBean data, int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (TAG.equals(VEDIO)) {//电影
                    intent = new Intent(baseActivity, YirenVideoActivity.class);
                } else if (TAG.equals(PICTURE)) {//图片
                    bundle.putString(TYPE, PICTURE);
                    intent = new Intent(baseActivity, YirenExhibitionActivity.class);
                } else if (TAG.equals(NOVEL)) {//小说
                    bundle.putString(TYPE, NOVEL);
                    intent = new Intent(baseActivity, YirenExhibitionActivity.class);
                }
                bundle.putString(TITLE, data.getTitle());
                bundle.putString(URL, data.getUrl());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);//可防止Item切换
        layoutManager.setAutoMeasureEnabled(true);
        mRvMenu.setLayoutManager(layoutManager);
        mRvMenu.setAdapter(menuAdapter);
    }
}
