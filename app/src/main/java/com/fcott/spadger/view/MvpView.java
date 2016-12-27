package com.fcott.spadger.view;

/**
 * Created by Administrator on 2016/9/20.
 */
public interface MvpView {
    void showProgressDialog();

    void hidProgressDialog();

    void showError(String error);
}
