package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.util.Log;

import com.fcott.spadger.R;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.ParseUtil;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VedioExhibitionActivity extends BaseActivity {
    public static final String TITLE = "TITLE";
    public static final String URL = "URL";

    private String title = "";//标题
    private String url = "";//地址URL

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_vedio_exhibition;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        title = bundle.getString(TITLE);
        url = bundle.getString(URL);
    }

    @Override
    protected void initViews() {
        LogUtil.log(title+";;"+url);
        RetrofitUtils.getInstance().create1(MainPageService.class)
                .getVideo(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.w("response","completed");
                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.w("response",e.toString());
                    }

                    @Override
                    public void onNext(String s) {
                        JsoupUtil.parseVideoList(s);
                    }
                });
    }
}
