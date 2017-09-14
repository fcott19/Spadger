package com.fcott.spadger.ui.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Administrator on 2017/9/14.
 */

public class AlertDialogFragment extends DialogFragment {
    private SelectFromListener selectFromListener;

    private String message = "are you ok?";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return new AlertDialog.Builder(getActivity()).setMessage(message)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectFromListener != null)
                            selectFromListener.positiveClick();
                        dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectFromListener != null)
                            selectFromListener.negativeClick();
                        dismiss();
                    }
                })
                .create();
    }

    public AlertDialogFragment setMessage(String message) {
        this.message = message;
        return this;
    }

    public AlertDialogFragment setSelectFromListener(SelectFromListener selectFromListener1){
        this.selectFromListener = selectFromListener1;
        return this;
    }

    public interface SelectFromListener{
        void positiveClick();
        void negativeClick();
    }
}