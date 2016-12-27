package com.fcott.spadger.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ItemBean;

import java.util.ArrayList;

public class PictureFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<ItemBean> menuData;//菜单数据
    private ArrayList<ItemBean> newData;//新更新的数据

    public PictureFragment() {
        // Required empty public constructor
    }

    public static PictureFragment newInstance(ArrayList<ItemBean> menuList,ArrayList<ItemBean> newList) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1,menuList);
        args.putParcelableArrayList(ARG_PARAM2,newList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            menuData = getArguments().getParcelableArrayList(ARG_PARAM1);
            newData = getArguments().getParcelableArrayList(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picture, container, false);
    }
}
