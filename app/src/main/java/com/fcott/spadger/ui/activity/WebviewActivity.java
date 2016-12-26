package com.fcott.spadger.ui.activity;

import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.fcott.spadger.R;
import com.tencent.smtt.sdk.TbsVideo;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class WebviewActivity extends AppCompatActivity {
    WebView tbsContent;
    private String url = "http://sd.52avhd.com:9888/rh/餌食牝 稲垣紗栄子/SD/playlist.m3u8";

//    public static boolean canUseTbsPlayer(Context context)
////判断当前Tbs播放器是否已经可以使用。
//    public static void openVideo(Context context, String videoUrl)
////直接调用播放接口，传入视频流的url
//    public static void openVideo(Context context, String videoUrl, Bundle extraData)
////extraData对象是根据定制需要传入约定的信息，没有需要可以传如null

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initView();

        if(TbsVideo.canUseTbsPlayer(this)){
            TbsVideo.openVideo(this,"http://hd.52avhd.com:9889/rh/%E7%AF%A0%E7%94%B0%E3%81%82%E3%82%86%E3%81%BF%E3%83%99%E3%82%B9%E3%83%88%20Part2/SD/out006.ts");
        }
    }

    private void initView() {
        tbsContent = (com.tencent.smtt.sdk.WebView) findViewById(R.id.tbsContent);
        tbsContent.loadUrl(url);
        WebSettings webSettings = tbsContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        tbsContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
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
