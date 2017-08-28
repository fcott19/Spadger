package com.fcott.spadger.ui.activity.look;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.bean.MovieInfoBean;
import com.fcott.spadger.model.bean.MoviePlayBean;
import com.fcott.spadger.model.http.LookMovieService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.utils.GeneralSettingUtil;
import com.fcott.spadger.utils.db.DBManager;
import com.fcott.spadger.utils.glideutils.ImageLoader;
import com.fcott.spadger.utils.web.X5WebView;
import com.tencent.smtt.sdk.TbsVideo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MovieDetialActivity extends BaseActivity {
    public static final String TAG = MovieDetialActivity.class.getSimpleName();

    private MovieBean.MessageBean.MoviesBean moviesBean;
    private MoviePlayBean moviePlayBean;

    @Bind(R.id.web_filechooser)
    public X5WebView webView;
    @Bind(R.id.tv_time)
    public TextView tvTime;
    @Bind(R.id.tv_actor)
    public TextView tvActor;
    @Bind(R.id.tv_channel)
    public TextView tvChannel;
    @Bind(R.id.tv_class)
    public TextView tvClass;
    @Bind(R.id.tv_supplier)
    public TextView tvSupplier;
    @Bind(R.id.tv_title)
    public TextView tvTitle;
    @Bind(R.id.tv_descript)
    public TextView tvDescript;
    @Bind(R.id.iv_actor)
    public ImageView ivActor;
    @Bind(R.id.iv_play)
    public ImageView ivPlay;
    @Bind(R.id.iv_cut)
    public ImageView ivCut;
    @Bind(R.id.nest)
    View view;

    @Override
    protected View getLoadingTargetView() {
        return view;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_movie_detial;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        moviesBean = bundle.getParcelable("DATA");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        removeAd();
        initActionBar();
        toggleShowLoading(true);
        tvTime.setText(moviesBean.getCreateTime());
        tvDescript.setText(moviesBean.getDescription());
        tvTitle.setText(moviesBean.getName());
        Glide.with(MovieDetialActivity.this)
                .load(moviesBean.getCoverImg())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(ivPlay);
        ivActor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (moviePlayBean != null) {
                    ClipboardManager myClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    myClipboard.setPrimaryClip(ClipData.newPlainText("movieurl", moviePlayBean.getMessage()));
                    Toast.makeText(MovieDetialActivity.this, "视频地址已经复制", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //网页video
        if (GeneralSettingUtil.isOpenWebMovieMode()) {
            ivPlay.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {
                    subscriber.onNext(ImageLoader.getInstance().getImageCachePath(moviesBean.getCoverImg()));
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            initWebView(s);
                        }
                    });
        }
        //tbsviedo
        else {
            ivPlay.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (moviePlayBean == null) {
                        Toast.makeText(MovieDetialActivity.this, "未获取到播放地址", Toast.LENGTH_SHORT).show();
                    } else if (!TbsVideo.canUseTbsPlayer(MovieDetialActivity.this)) {
                        Toast.makeText(MovieDetialActivity.this, "播放器未准备好", Toast.LENGTH_SHORT).show();
                    } else {
                        TbsVideo.openVideo(MovieDetialActivity.this, moviePlayBean.getMessage());
                    }
                }
            });
        }

        RequestBody playBody = new FormBody.Builder()
                .add("MovieID", moviesBean.getMovieID())
                .build();
        RetrofitUtils.getInstance().create(LookMovieService.class)
                .requestMovieInfo(playBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieInfoBean>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        toggleShowLoading(false);
                    }

                    @Override
                    public void onNext(MovieInfoBean movieInfoBean) {
                        toggleShowLoading(false);
                        MovieInfoBean.MessageBean messageBean = movieInfoBean.getMessage();
                        tvActor.setText(makeActor(messageBean.getActor()));
                        tvChannel.setText(messageBean.getChannel().getName());
                        tvClass.setText(makeClass(messageBean.getClassBean()));
                        tvSupplier.setText(messageBean.getSupplier().getName());
                        tvTime.setText(messageBean.getCreateTime());
                        Glide.with(MovieDetialActivity.this)
                                .load(messageBean.getImg())
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(ivActor);
                        Glide.with(MovieDetialActivity.this)
                                .load(messageBean.getCutPicName())
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(ivCut);
                    }
                });

        RetrofitUtils.getInstance().create(LookMovieService.class)
                .moviePlay(playBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MoviePlayBean>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MoviePlayBean playBean) {
                        moviePlayBean = playBean;
                        webView.loadUrl("javascript:url('" + moviePlayBean.getMessage() + "')");
                    }
                });
    }

    private void removeAd() {
        getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ArrayList<View> outView = new ArrayList<View>();
                getWindow().getDecorView().findViewsWithText(outView, "QQ浏览器", View.FIND_VIEWS_WITH_TEXT);
                if (outView != null && outView.size() > 0) {
                    outView.get(0).setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 初始化webview
     */
    private void initWebView(final String localImageCachePath) {
        webView.loadUrl("file:///android_asset/webpage/fullscreenVideo.html");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        webView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        webView.setOnPageFinishListener(new X5WebView.OnPageFinishListener() {
            @Override
            public void onPageFinish() {
                if (moviePlayBean != null)
                    webView.loadUrl("javascript:url('" + moviePlayBean.getMessage() + "')");
                if (localImageCachePath != null)
                    webView.loadUrl("javascript:poster('" + localImageCachePath + "')");
                enablePageVideoFunc();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        try {
            super.onConfigurationChanged(newConfig);
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化ActionBar
     */
    private void initActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("电影详情");
    }

    private void enablePageVideoFunc() {
        if (webView.getX5WebViewExtension() != null) {
            Bundle data = new Bundle();
            data.putBoolean("standardFullScreen", false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", GeneralSettingUtil.isWindowMovie());// false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 1);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            webView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (moviesBean == null)
            return true;

        getMenuInflater().inflate(R.menu.out_map_menu, menu);
        final Switch switchShop = (Switch) menu.findItem(R.id.myswitch).getActionView().findViewById(R.id.switchForActionBar);
        final DBManager dbManager = new DBManager(this);
        if (dbManager.hasContainId(moviesBean.getMovieID())) {
            switchShop.setChecked(true);
            switchShop.setText(getResources().getString(R.string.cancel_collection));
        }
        dbManager.closeDB();

        switchShop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton btn, boolean isChecked) {
                DBManager dbManager = new DBManager(MovieDetialActivity.this);
                if (isChecked) {
                    switchShop.setText(getResources().getString(R.string.cancel_collection));
                    dbManager.add(moviesBean);
                } else {
                    switchShop.setText(getResources().getString(R.string.collection));
                    dbManager.deleteMovie(moviesBean.getMovieID());
                }
                dbManager.closeDB();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private String makeActor(List<MovieInfoBean.MessageBean.ActorBean> actorBeens) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < actorBeens.size(); i++) {
            if (i != 0)
                stringBuffer.append(",");
            stringBuffer.append(actorBeens.get(i).getName());
        }
        return stringBuffer.toString();
    }

    private String makeClass(List<MovieInfoBean.MessageBean.ClassBean> classBeen) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < classBeen.size(); i++) {
            if (i != 0)
                stringBuffer.append(",");
            stringBuffer.append(classBeen.get(i).getName());
        }
        return stringBuffer.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
    }

}
