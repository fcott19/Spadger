package com.fcott.spadger.ui.widget;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.fcott.spadger.R;

/**
 * Created by Administrator on 2017/9/13.
 */

public class BottomSelectDialog extends DialogFragment implements View.OnClickListener {
    private SelectFromListener selectFromListener;

    @Override
    public void onStart() {
        super.onStart();
        Window window = this.getDialog().getWindow();
        //去掉dialog默认的padding
//        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置dialog的位置在底部
        lp.gravity = Gravity.BOTTOM;
        //设置dialog的动画
        lp.windowAnimations = R.style.popup_window_anim;
        lp.dimAmount = 0.3f;
        window.setAttributes(lp);
//        window.setBackgroundDrawable(new ColorDrawable());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //去掉dialog的标题，需要在setContentView()之前
        this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.popwindow_pic, null);
        view.findViewById(R.id.tv_make_pic).setOnClickListener(this);
        view.findViewById(R.id.tv_gallery).setOnClickListener(this);

        return view;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        selectFromListener = null;
    }

    @Override
    public void onClick(View v) {
        if(selectFromListener == null){
            return;
        }
        final int id = v.getId();
        if(id == R.id.tv_make_pic){
            dismiss();
            selectFromListener.fromCamera();
        }
        else if(id == R.id.tv_gallery){
            dismiss();
            selectFromListener.fromGallery();
        }
    }

    public BottomSelectDialog setSelectFromListener(SelectFromListener selectFromListener1){
        this.selectFromListener = selectFromListener1;
        return this;
    }

    public interface SelectFromListener{
        void fromCamera();
        void fromGallery();
    }
}
