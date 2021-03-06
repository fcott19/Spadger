package com.fcott.spadger.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.fcott.spadger.App;
import com.fcott.spadger.R;
import com.fcott.spadger.ui.widget.loading.VaryViewHelperController;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/19.
 */
public abstract class BaseActivity extends AppCompatActivity{

    //上下文
    protected Context mContext = null;

    //加载提示帮助类
    private VaryViewHelperController mVaryViewHelperController = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 允许使用transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        mContext = this;

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            getBundleExtras(bundle);
        }

        if(getContentViewLayoutID() <= 0){
            throw new IllegalArgumentException("必须调用getContentViewLayoutID方法，填充布局");
        }else {
            setContentView(getContentViewLayoutID());
        }

        if(needAddActivityList())
            App.getInstance().addActivity(this);

        if (null != getLoadingTargetView()) {
            mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
        }

        initViews();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    //返回布局
    protected abstract int getContentViewLayoutID();
    //携带参数
    protected abstract void getBundleExtras(Bundle bundle);
    //初始化视图
    protected abstract void initViews();

    protected boolean needAddActivityList() {
        return true;
    }

    //设置提醒占位视图
    protected View getLoadingTargetView(){
        return null;
    }

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
    protected void toggleShowLoading(boolean toggle) {
        toggleShowLoading(toggle,getResources().getString(R.string.load_loading_tip));
    }

    protected void toggleShowError(String msg) {
        toggleShowError(msg,null);
    }
    protected void toggleShowError(String msg,View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            throw new IllegalArgumentException("You must return a right target view for loading");
        }

        mVaryViewHelperController.showEmpty(msg,onClickListener);
    }

}
