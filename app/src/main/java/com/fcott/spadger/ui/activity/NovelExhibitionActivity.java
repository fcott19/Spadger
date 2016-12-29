package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.NovelListBean;
import com.fcott.spadger.model.bean.NovelListItemBean;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.NovelListAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.fragment.MenuFragment;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.JsoupUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//小说展示界面，由于图片界面与此相同。故也是图片展示界面
public class NovelExhibitionActivity extends BaseActivity {
    private static final String ACACHE_TAG = "CACHE_NOVEL_E";
    public static final String NOVEL_DETIAL_URL = "DETIAL_URL";
    public static final String NOVEL_DETIAL_TITLE = "DETIAL_TITLE";

    private String title = "";//标题
    private String url = "";//地址URL
    private String tyep = "";//用于区分 小说/图片
    private NovelListAdapter adapter = null;
    private NovelListBean novelListBean = null;

    @Bind(R.id.rv_vedio_list)
    public RecyclerView recyclerView;
    @Bind(R.id.et_page_number)
    public EditText etPageNumber;
    @OnClick({R.id.tv_first_page, R.id.tv_pre_page, R.id.tv_next_page, R.id.tv_last_page})
    public void onClick(View view) {
        if (novelListBean == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_first_page:
                if (!novelListBean.getPageControlBean().getFirstPageUrl().isEmpty()) {
                    requestData(novelListBean.getPageControlBean().getFirstPageUrl());
                } else {
                    Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_pre_page:
                if (!novelListBean.getPageControlBean().getPrePage().isEmpty()) {
                    requestData(novelListBean.getPageControlBean().getPrePage());
                } else {
                    Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_next_page:
                if (!novelListBean.getPageControlBean().getNextPage().isEmpty()) {
                    requestData(novelListBean.getPageControlBean().getNextPage());
                } else {
                    Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.already_last), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_last_page:
                if (!novelListBean.getPageControlBean().getLastPageUrl().isEmpty()) {
                    requestData(novelListBean.getPageControlBean().getLastPageUrl());
                } else {
                    Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.already_last), Toast.LENGTH_LONG).show();
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
        return R.layout.activity_novel_exhibition;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        title = bundle.getString(MenuFragment.TITLE);
        url = bundle.getString(MenuFragment.URL);
        tyep = bundle.getString(MenuFragment.TYPE);
    }

    @Override
    protected void initViews() {
        //列表适配器
        adapter = new NovelListAdapter(NovelExhibitionActivity.this, new ArrayList<NovelListItemBean>(), false);
        adapter.setOnItemClickListener(new OnItemClickListeners<NovelListItemBean>() {
            @Override
            public void onItemClick(ViewHolder viewHolder,NovelListItemBean data, int position) {
                Intent intent = new Intent();
                if(tyep.equals(MenuFragment.NOVEL)){
                    intent = new Intent(NovelExhibitionActivity.this,NovelDetialActivity.class);
                }else {
                    intent = new Intent(NovelExhibitionActivity.this,PictureDetailActivity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putString(NOVEL_DETIAL_URL,data.getUrl());
                bundle.putString(NOVEL_DETIAL_TITLE,data.getTitle());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(NovelExhibitionActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        requestData(url);
    }

    //请求数据，更新界面
    private void requestData(final String url) {
        ACache mCache = ACache.get(NovelExhibitionActivity.this.getApplicationContext());
        String value = mCache.getAsString(ACACHE_TAG + url);//取出缓存

        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            novelListBean = JsoupUtil.parseNovelList(value);
            ArrayList arrayList = novelListBean.getNovelList();
            if (!arrayList.isEmpty()) {
                adapter.setNewData(arrayList);
                //设置当前页数
                etPageNumber.setText(novelListBean.getPageControlBean().getCurrentPage());
                //etPageNumber失去焦点，隐藏光标
                etPageNumber.clearFocus();
                etPageNumber.setFocusable(false);
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
                        ACache aCache = ACache.get(NovelExhibitionActivity.this.getApplicationContext());
                        aCache.put(ACACHE_TAG + url, s);
                        //更新界面
                        novelListBean = JsoupUtil.parseNovelList(s);
                        adapter.setNewData(novelListBean.getNovelList());
                        //设置当前页数
                        etPageNumber.setText(novelListBean.getPageControlBean().getCurrentPage());
                        //etPageNumber失去焦点，隐藏光标
                        etPageNumber.clearFocus();
                        etPageNumber.setFocusable(false);
                    }
                });
    }
}
