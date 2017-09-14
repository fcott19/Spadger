package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.entity.Post;
import com.fcott.spadger.ui.adapter.PostAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.OnLoadMoreListener;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.GsonUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.UserManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class BbsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int PAGE_SIZE = 20;
    private PostAdapter postAdapter;
    private int currentPage = 1;
    private String dataFrom;
    private boolean hasInLoad = false;//是否加载中

    @Bind(R.id.srl_bbs)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.rcy_bbs)
    RecyclerView recyclerView;
    @Bind(R.id.contain)
    View view;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_bbs;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        dataFrom = bundle.getString(Config.DATA_FROM);
    }

    @Override
    protected View getLoadingTargetView() {
        return view;
    }

    @Override
    protected void initViews() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BbsActivity.this,MakePostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Config.DATA_FROM,Config.DATA_FROM_NEWPOST);
                intent.putExtras(bundle);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(BbsActivity.this, floatingActionButton, "fab");
                ActivityCompat.startActivityForResult(BbsActivity.this,
                        intent,Config.NORMAL_REQUEST_CODE, activityOptionsCompat.toBundle());
            }
        });

        postAdapter = new PostAdapter(this, new ArrayList<Post>(), true);
        postAdapter.setOnItemClickListener(new OnItemClickListeners<Post>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, Post data, int position) {
                LogUtil.log(GsonUtil.toJson(data));
                Intent intent = new Intent(BbsActivity.this,PostDetialActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("post",data);
                intent.putExtras(bundle);
                Pair<View,String> [] p = new Pair[]{
                        Pair.create(viewHolder.getView(R.id.iv_head), "post_headimg"),
                        Pair.create(viewHolder.getView(R.id.tv_nick_name), "post_nick_name")};
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(BbsActivity.this,p);
                ActivityCompat.startActivityForResult(BbsActivity.this,
                        intent,Config.NORMAL_REQUEST_CODE,activityOptionsCompat.toBundle());
            }
        });
        postAdapter.setLoadingView(R.layout.load_loading_foot);//加载中 提示view
        postAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(BaseAdapter.LoadMode loadMode) {
                if(hasInLoad)
                    return;
                currentPage++;
                queryPost(currentPage,true);
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(BbsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postAdapter);

        if(dataFrom != null && dataFrom.equals(Config.DATA_FROM_MY_POST)){
            floatingActionButton.setVisibility(View.GONE);
        }
        postRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Config.SUCCESS_RESULT_CODE){
            postRefresh();
        }
    }

    public void queryPost(final int page,final boolean isLoadmore){
        hasInLoad = true;//是否加载中
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.setLimit(PAGE_SIZE);
        query.setSkip(PAGE_SIZE * (page - 1));
        query.order("-createdAt");
        query.include("author,moviesBean");
        if(dataFrom != null && dataFrom.equals(Config.DATA_FROM_MY_POST)){
            query.addWhereEqualTo("author", UserManager.getCurrentUser());
        }
        //执行查询方法
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> object, BmobException e) {
                cancleRefresh();//取消刷新
                hasInLoad = false;//是否加载中
                if(e==null){
                    if(object.size() != 0){
                        if(isLoadmore)
                            postAdapter.setLoadMoreData(object);
                        else
                            postAdapter.setNewData(object);
                    }else {
                        postAdapter.setLoadEndView(R.layout.load_end_foot);
                    }
                }else{
                    toggleShowError(e.getMessage());
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        queryPost(currentPage,false);
    }

    /**
     * 自动刷新
     */
    private void postRefresh(){
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    /**
     * 取消刷新
     */
    private void cancleRefresh(){
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
