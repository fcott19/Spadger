package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.tencent.smtt.sdk.VideoActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/10.
 */

public class MyVideoActivity extends VideoActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        removeAd();
    }
    private void removeAd(){
        getWindow().getDecorView().addOnLayoutChangeListener(new      View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                ArrayList<View> outView= new ArrayList<View>();
                getWindow().getDecorView().findViewsWithText(outView, "QQ浏览器", View.FIND_VIEWS_WITH_TEXT);
                if (outView != null && outView.size() > 0) {
                    outView.get(0).setVisibility(View.GONE);
                }
            }
        });
    }
}


//    private List<View> oldList;
//    private void removeAd(){
//        getWindow().getDecorView().addOnLayoutChangeListener(new      View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//
//                if (oldList != null &&getAllChildViews(getWindow().getDecorView()).size() > oldList.size()) {
//
//                    for (View view :getAllChildViews(getWindow().getDecorView())) {
//
//                        if (!oldList.contains(view)) {
//
//                            view.setVisibility(View.GONE);
//                        }
//                    }
//                }
//
//                ArrayList<View> outView= new ArrayList<View>();
//                getWindow().getDecorView().findViewsWithText(outView, "QQ浏览器", View.FIND_VIEWS_WITH_TEXT);
//                int size = outView.size();
//                if (outView != null && outView.size() > 0) {
//                    oldList =getAllChildViews(getWindow().getDecorView());
//                    outView.get(0).setVisibility(View.GONE);
//                }
//            }
//        });
//    }
//    private List<View> getAllChildViews(View view) {
//
//        List<View> allchildren = new ArrayList<View>();
//
//        if (view instanceof ViewGroup) {
//
//            ViewGroup vp = (ViewGroup) view;
//
//            for (int i = 0; i < vp.getChildCount(); i++) {
//                View viewchild = vp.getChildAt(i);
//                allchildren.add(viewchild);
//                allchildren.addAll(getAllChildViews(viewchild));
//            }
//
//        }
//
//        return allchildren;
//
//    }