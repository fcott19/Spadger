package com.fcott.spadger.ui.activity;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.WindowManager;

import com.fcott.spadger.R;
import com.tencent.smtt.sdk.WebView;

import butterknife.Bind;
//小说展示界面，由于图片界面与此相同。故也是图片展示界面
public class NovelExhibitionActivity extends BaseActivity {

    @Bind(R.id.wv_novel)
    public WebView webView;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_novel_exhibition;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        webView.loadData("aaa", "text/html", "UTF-8");
    }
}
