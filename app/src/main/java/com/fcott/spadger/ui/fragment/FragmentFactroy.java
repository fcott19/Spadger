package com.fcott.spadger.ui.fragment;

import android.support.v4.app.Fragment;

import com.fcott.spadger.model.bean.MenuBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/13.
 */

public class FragmentFactroy {
    static ArrayList<Fragment> fragmentList = new ArrayList<>();

    public static ArrayList<Fragment> getFragment(MenuBean menuBean) {
        if(fragmentList.size() == 3)
            return fragmentList;
        fragmentList.clear();
        fragmentList.add(MenuFragment.newInstance(menuBean.getPicList(),menuBean.getNewpicList(),MenuFragment.PICTURE));
        fragmentList.add(MenuFragment.newInstance(menuBean.getNovelList(),menuBean.getNewNovelList(),MenuFragment.NOVEL));
        fragmentList.add(MenuFragment.newInstance(menuBean.getVedioList(),menuBean.getNewVedioList(),MenuFragment.VEDIO));
        return fragmentList;
    }
}
