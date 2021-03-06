package com.fcott.spadger.ui.activity.kv;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.YirenService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.JsoupUtil;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NovelDetialActivity extends BaseActivity {
    private static final String ACACHE_TAG = "CACHE_NOVEL_D";

    @Bind(R.id.tbsContent)
    WebView tbsContent;
    @Bind(R.id.nest)
    NestedScrollView nestedScrollView;
    @Bind(R.id.tv_title)
    TextView tvTitle;

    private String url = "";
    private String title = "";
    private String dataFrom = "";//数据来源

    @Override
    protected View getLoadingTargetView() {
        return nestedScrollView;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_novel_detial;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        url = bundle.getString(NovelExhibitionActivity.NOVEL_DETIAL_URL);
        title = bundle.getString(NovelExhibitionActivity.NOVEL_DETIAL_TITLE);
        dataFrom = bundle.getString(Config.DATA_FROM);
    }

    @Override
    protected void initViews() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ACache mCache = ACache.get(NovelDetialActivity.this.getApplicationContext());
        String value = mCache.getAsString(ACACHE_TAG + url);//取出缓存

        //设置标题
        tvTitle.setText(title);
        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            if(dataFrom.equals(Config.DATA_FROM_KV)){
                showNovel(JsoupUtil.parseNovelDetial(value));
            }else if(dataFrom.equals(Config.DATA_FROM_YIREN)){
                showNovel(JsoupUtil.parseYirenNovelDetial(value));
            }
            return;
        } else {
            toggleShowLoading(true);
        }

        Observable<String> ob;
        if(dataFrom.equals(Config.DATA_FROM_KV)){
            ob = RetrofitUtils.getInstance().create1(MainPageService.class)
                    .getData(url);
        }else if(dataFrom.equals(Config.DATA_FROM_YIREN)){
            ob = RetrofitUtils.getInstance().create1(YirenService.class)
                    .getData(url);
        }else {
            return;
        }
        ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        toggleShowLoading(false);
                        Log.w("response", "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("response", e.toString());
                    }

                    @Override
                    public void onNext(String s) {
                        ACache aCache = ACache.get(NovelDetialActivity.this.getApplicationContext());
                        aCache.put(ACACHE_TAG + url, s);
                        if(dataFrom.equals(Config.DATA_FROM_KV)){
                            showNovel(JsoupUtil.parseNovelDetial(s));
                        }else if(dataFrom.equals(Config.DATA_FROM_YIREN)){
                            showNovel(JsoupUtil.parseYirenNovelDetial(s));
                        }
                    }
                });
    }

    private void showNovel(String response){
        WebSettings webSettings = tbsContent.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8") ;
        webSettings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        tbsContent.loadDataWithBaseURL(null,response, "text/html",  "utf-8", null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && tbsContent.canGoBack()) {
            tbsContent.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
