package com.fcott.spadger.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fcott.spadger.ui.widget.loading.VaryViewHelperController;
import com.fcott.spadger.utils.netstatus.NetChangeObserver;
import com.fcott.spadger.utils.netstatus.NetStateReceiver;
import com.fcott.spadger.utils.netstatus.NetUtils;
import com.fcott.spadger.view.MvpView;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/20.
 */
public abstract class BaseFragment extends Fragment implements MvpView {
    protected Activity baseActivity;
    protected View rootView;

    //加载提示帮助类
    private VaryViewHelperController mVaryViewHelperController = null;
     //联网状态
    protected NetChangeObserver mNetChangeObserver = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseActivity = (Activity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getFragmentLayoutID() != 0){
            rootView = inflater.inflate(getFragmentLayoutID(),null);
        }else {
            rootView = super.onCreateView(inflater, container, savedInstanceState);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);

        if (null != getLoadingTargetView()) {
            mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
        }
        if(hasMonitorNetWork()){
            NetStateReceiver.registerNetworkStateReceiver(baseActivity);
            mNetChangeObserver = new NetChangeObserver() {
                @Override
                public void onNetConnected(NetUtils.NetType type) {
                    onNetworkConnected(type);
                }

                @Override
                public void onNetDisConnect() {
                    onNetworkDisConnected();
                }
            };

            NetStateReceiver.registerObserver(mNetChangeObserver);
        }

        initViews();
    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);
        if(hasMonitorNetWork()){
            NetStateReceiver.removeRegisterObserver(mNetChangeObserver);
            NetStateReceiver.unRegisterNetworkStateReceiver(baseActivity);
        }
        super.onDestroy();
    }

    //设置提醒占位视图
    protected View getLoadingTargetView(){
        return null;
    }
    //网络连接
    protected void onNetworkConnected(NetUtils.NetType type){

    }
    //网络断开
    protected void onNetworkDisConnected(){

    }
    //是否监听网络状态
    protected boolean hasMonitorNetWork(){
        return false;
    }

    public abstract int getFragmentLayoutID();

    public abstract void initViews();

    protected void toggleShowLoading(boolean toggle, String msg) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showLoading(msg);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    protected void toggleShowEmpty(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showEmpty(msg, onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    protected void toggleShowEmpty(boolean toggle, String msg, View.OnClickListener onClickListener, int img) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showEmpty(msg, onClickListener, img);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    protected void toggleShowError(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showError(msg, onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    protected void toggleNetworkError(boolean toggle, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        if (toggle) {
            mVaryViewHelperController.showNetworkError(onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**********MvpView方法***********/
    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hidProgressDialog() {

    }

    @Override
    public void showError(String error) {

    }
}
