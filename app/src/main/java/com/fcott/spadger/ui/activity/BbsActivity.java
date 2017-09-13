package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.entity.Post;
import com.fcott.spadger.ui.adapter.PostAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class BbsActivity extends BaseActivity {
    private static final int PAGE_SIZE = 20;
    private PostAdapter postAdapter;
    private int currentPage = 1;

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

    }

    @Override
    protected View getLoadingTargetView() {
        return view;
    }

    @Override
    protected void initViews() {

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

        postAdapter = new PostAdapter(this, new ArrayList<Post>(), false);
        postAdapter.setOnItemClickListener(new OnItemClickListeners<Post>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, Post data, int position) {
                Intent intent = new Intent(BbsActivity.this,PostDetialActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("post",data);
                intent.putExtras(bundle);
                Pair<View,String> [] p = new Pair[]{Pair.create(floatingActionButton, "fab"),
                        Pair.create(viewHolder.getView(R.id.iv_head), "headimg")};
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(BbsActivity.this,p);
                ActivityCompat.startActivity(BbsActivity.this, intent,activityOptionsCompat.toBundle());
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(BbsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postAdapter);
        queryPost(currentPage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Config.SUCCESS_RESULT_CODE){
            queryPost(currentPage);
        }
    }

    public void queryPost(final int page){
        toggleShowLoading(true);
        if(page <= 0){
            //判断页码输入错误
        }
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.setLimit(PAGE_SIZE);
        query.setSkip(PAGE_SIZE * (page - 1));
        query.order("-createdAt");
        query.include("author");
        query.include("moviesBean");
        //执行查询方法
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> object, BmobException e) {
                if(e==null){
                    toggleShowLoading(false);
                    postAdapter.setNewData(object);
                }else{
                    toggleShowError(e.getMessage());
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
}
