package com.fcott.spadger.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ItemBean;
import com.fcott.spadger.ui.activity.PLVideoViewActivity;
import com.fcott.spadger.ui.activity.VedioExhibitionActivity;
import com.fcott.spadger.ui.adapter.MenuAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.ParseUtil;
import com.pili.pldroid.player.AVOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.Bind;

public class NovelFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    public static final String NOVEL = "NOVEL";
    public static final String PICTURE = "PICTURE";
    public static final String VEDIO = "VEDIO";

    @Bind(R.id.type_item_menu)
    public RecyclerView mRvMenu;
    @Bind(R.id.type_item_newInfo)
    public RecyclerView mRvNewInfo;

    MenuAdapter menuAdapter = null;//菜单选择Adapter
    MenuAdapter newInfoAdapter = null;//新更新信息Adapter
    private ArrayList<ItemBean> menuData = new ArrayList<>();//菜单数据
    private ArrayList<ItemBean> newData = new ArrayList<>();//新更新的数据
    private String TAG = "";//类型标志

    public NovelFragment() {
        // Required empty public constructor
    }

    public static NovelFragment newInstance(ArrayList<ItemBean> menuList,ArrayList<ItemBean> newList,String tag) {
        NovelFragment fragment = new NovelFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1,menuList);
        args.putParcelableArrayList(ARG_PARAM2,newList);
        args.putString(ARG_PARAM3,tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getFragmentLayoutID() {
        return R.layout.fragment_novel;
    }

    @Override
    public void initViews() {
        if (getArguments() != null) {
            menuData = getArguments().getParcelableArrayList(ARG_PARAM1);
            newData = getArguments().getParcelableArrayList(ARG_PARAM2);
            TAG = getArguments().getString(ARG_PARAM3);
        }
        LogUtil.log(GsonUtil.toJson(newData));
        //menu列表展示
        menuAdapter = new MenuAdapter(baseActivity,menuData,false);
        menuAdapter.setOnItemClickListener(new OnItemClickListeners<ItemBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, ItemBean data, int position) {
                Intent intent = new Intent();
                if(TAG.equals(VEDIO)){//电影
                    intent = new Intent(baseActivity, VedioExhibitionActivity.class);
                }else if(TAG.equals(PICTURE)){//图片
                    intent = new Intent(baseActivity, VedioExhibitionActivity.class);
                }else if(TAG.equals(NOVEL)){//小说
                    intent = new Intent(baseActivity, VedioExhibitionActivity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putString(VedioExhibitionActivity.TITLE,data.getTitle());
                bundle.putString(VedioExhibitionActivity.URL,data.getUrl());
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
        newInfoAdapter = new MenuAdapter(baseActivity,newData,false);
        newInfoAdapter.setOnItemClickListener(new OnItemClickListeners<ItemBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, ItemBean data, int position) {
                if(TAG.equals(VEDIO)){

                }else if(TAG.equals(PICTURE)){

                }else if(TAG.equals(NOVEL)){

                }
            }
        });
        final LinearLayoutManager layoutManager1 = new LinearLayoutManager(baseActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager1.setAutoMeasureEnabled(true);
        mRvNewInfo.setLayoutManager(layoutManager1);
        mRvNewInfo.setAdapter(newInfoAdapter);
    }
}
