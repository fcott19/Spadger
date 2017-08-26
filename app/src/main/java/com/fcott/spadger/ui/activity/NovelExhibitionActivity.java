package com.fcott.spadger.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fcott.spadger.App;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.NovelListBean;
import com.fcott.spadger.model.bean.NovelListItemBean;
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.ui.adapter.NovelListAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemLongClickListener;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.ui.fragment.MenuFragment;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.glideutils.ImageLoader;

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
        adapter.setItemLongClickListener(new OnItemLongClickListener<NovelListItemBean>() {
            @Override
            public void onItemLongClick(ViewHolder viewHolder, NovelListItemBean data, int position) {
                perLoadImage(data.getUrl());
                Toast.makeText(mContext,"已经开始预加载",Toast.LENGTH_SHORT).show();
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(NovelExhibitionActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
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
        if (pageNumber < 1 || pageNumber > novelListBean.getPageControlBean().getTotalPage()) {
            Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.input_error), Toast.LENGTH_SHORT).show();
        } else if (pageNumber == 1) {
            if (!novelListBean.getPageControlBean().getFirstPageUrl().isEmpty()) {
                requestData(novelListBean.getPageControlBean().getFirstPageUrl());
            } else {
                Toast.makeText(NovelExhibitionActivity.this, getResources().getString(R.string.already_first), Toast.LENGTH_LONG).show();
            }
        } else {
            requestData(novelListBean.getPageControlBean().getJumpUrl().replace("{!page!}", etPageNumber.getText()));
        }
    }

    private void perLoadImage(String url){
        RetrofitUtils.getInstance().create1(MainPageService.class)
                .getData(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("response", e.toString());
                    }

                    @Override
                    public void onNext(String s) {
                        ArrayList<String> dataList = JsoupUtil.parsePictureDetial(s);
                        for (String string : dataList) {
                            ImageLoader.getInstance().preLoad(App.getInstance().getApplicationContext(), string);
                        }
                    }
                });
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
                    }
                });
    }
}
