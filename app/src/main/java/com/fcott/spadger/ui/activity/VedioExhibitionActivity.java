package com.fcott.spadger.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.VedioListBean;
import com.fcott.spadger.model.bean.VedioListItemBean;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.VedioListAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.fragment.MenuFragment;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.LogUtil;
import com.tencent.smtt.sdk.TbsVideo;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VedioExhibitionActivity extends BaseActivity {
    private static final String ACACHE_TAG = "CACHE_VEDIO";

    private String title = "";//标题
    private String url = "";//地址URL
    private VedioListAdapter adapter = null;
    private VedioListBean vedioListBean = null;

    @Bind(R.id.rv_vedio_list)
    public RecyclerView recyclerView;
    @Bind(R.id.et_page_number)
    public EditText etPageNumber;

    @OnClick({R.id.tv_first_page, R.id.tv_pre_page, R.id.tv_next_page, R.id.tv_last_page})
    public void onClick(View view) {
        if (vedioListBean == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_first_page:
                if (!vedioListBean.getPageControlBean().getFirstPageUrl().isEmpty()) {
                    requestData(vedioListBean.getPageControlBean().getFirstPageUrl());
                } else {
                    Toast.makeText(VedioExhibitionActivity.this, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_pre_page:
                if (!vedioListBean.getPageControlBean().getPrePage().isEmpty()) {
                    requestData(vedioListBean.getPageControlBean().getPrePage());
                } else {
                    Toast.makeText(VedioExhibitionActivity.this, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_next_page:
                if (!vedioListBean.getPageControlBean().getNextPage().isEmpty()) {
                    requestData(vedioListBean.getPageControlBean().getNextPage());
                } else {
                    Toast.makeText(VedioExhibitionActivity.this, getResources().getString(R.string.already_last), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_last_page:
                if (!vedioListBean.getPageControlBean().getLastPageUrl().isEmpty()) {
                    requestData(vedioListBean.getPageControlBean().getLastPageUrl());
                } else {
                    Toast.makeText(VedioExhibitionActivity.this, getResources().getString(R.string.already_last), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected View getLoadingTargetView() {
        return recyclerView;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_vedio_exhibition;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        title = bundle.getString(MenuFragment.TITLE);
        url = bundle.getString(MenuFragment.URL);
    }

    @Override
    protected void initViews() {
        //列表适配器
        adapter = new VedioListAdapter(VedioExhibitionActivity.this, new ArrayList<VedioListItemBean>(), false);//
        adapter.setOnItemClickListener(new OnItemClickListeners<VedioListItemBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, VedioListItemBean data, int position) {
                RetrofitUtils.getInstance().create1(MainPageService.class)
                        .getData(data.getUrl())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                                Log.w("response", "completed");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.w("response", e.toString());
                            }

                            @Override
                            public void onNext(String s) {
                                if (TbsVideo.canUseTbsPlayer(VedioExhibitionActivity.this)) {
                                    TbsVideo.openVideo(VedioExhibitionActivity.this, JsoupUtil.parseVideoDetial(s));
                                }
                            }
                        });
            }
        });
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);//可防止Item切换
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        requestData(url);

        //监听软键盘完成按钮
        etPageNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    /*隐藏软键盘*/
                InputMethodManager imm = (InputMethodManager) v
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(
                            v.getApplicationWindowToken(), 0);
                }
                goPage();
                //etPageNumber失去焦点，隐藏光标
                etPageNumber.clearFocus();
                etPageNumber.setFocusable(false);
                return true;
            }
        });
        //触摸etPageNumber时获取焦点
        etPageNumber.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                etPageNumber.setFocusable(true);
                etPageNumber.setFocusableInTouchMode(true);
                etPageNumber.requestFocus();
                return false;
            }
        });
    }

    //跳转到指定页面
    private void goPage() {
        int pageNumber = 0;
        try {
            pageNumber = Integer.valueOf(etPageNumber.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (pageNumber < 1 || pageNumber > vedioListBean.getPageControlBean().getTotalPage()) {
            Toast.makeText(VedioExhibitionActivity.this, getResources().getString(R.string.input_error), Toast.LENGTH_SHORT).show();
        } else if (pageNumber == 1) {
            if (!vedioListBean.getPageControlBean().getFirstPageUrl().isEmpty()) {
                requestData(vedioListBean.getPageControlBean().getFirstPageUrl());
            } else {
                Toast.makeText(VedioExhibitionActivity.this, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
            }
        } else {
            requestData(vedioListBean.getPageControlBean().getJumpUrl().replace("{!page!}", etPageNumber.getText()));
        }
    }

    //请求数据，更新界面
    private void requestData(final String url) {
        ACache mCache = ACache.get(VedioExhibitionActivity.this.getApplicationContext());
        String value = mCache.getAsString(ACACHE_TAG + url);//取出缓存

        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            vedioListBean = JsoupUtil.parseVideoList(value);
            ArrayList arrayList = vedioListBean.getVedioList();
            if (!arrayList.isEmpty()) {
                adapter.setNewData(arrayList);
                //设置当前页数
                etPageNumber.setText(vedioListBean.getPageControlBean().getCurrentPage());
            }
        } else {
            toggleShowLoading(true);
        }

        RetrofitUtils.getInstance().create1(MainPageService.class)
                .getData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.w("response", "completed");
                        toggleShowLoading(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("response", e.toString());
                    }

                    @Override
                    public void onNext(String s) {
                        //缓存
                        ACache aCache = ACache.get(VedioExhibitionActivity.this.getApplicationContext());
                        aCache.put(ACACHE_TAG + url, s);
                        vedioListBean = JsoupUtil.parseVideoList(s);
                        //更新界面
                        adapter.setNewData(vedioListBean.getVedioList());
                        //设置当前页数
                        etPageNumber.setText(vedioListBean.getPageControlBean().getCurrentPage());
                    }
                });
    }
}
