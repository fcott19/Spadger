package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.entity.Comment;
import com.fcott.spadger.model.entity.Post;
import com.fcott.spadger.ui.adapter.CommentAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.OnItemClickListeners;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PostDetialActivity extends BaseActivity {
    private static final int PAGE_SIZE = 20;
    private int currentPage = 1;
    private Post post;
    private CommentAdapter commentAdapter;

    @Bind(R.id.tv_nick_name)
    TextView tvNickName;
    @Bind(R.id.rcy)
    RecyclerView recyclerView;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_post_detial;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        post = (Post) bundle.getSerializable("post");
    }

    /**
     * 加载占位图
     * @return
     */
    @Override
    protected View getLoadingTargetView() {
        return recyclerView;
    }

    @Override
    protected void initViews() {
        tvNickName.setText(post.getAuthor().getNickName());
        tvTitle.setText(post.getTitle());
        tvContent.setText(post.getContent());
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetialActivity.this,MakePostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Config.DATA_FROM,Config.DATA_FROM_ADDASK);
                bundle.putSerializable("post",post);
                intent.putExtras(bundle);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(PostDetialActivity.this, floatingActionButton, "fab");
                ActivityCompat.startActivityForResult(PostDetialActivity.this,
                        intent, Config.NORMAL_REQUEST_CODE, activityOptionsCompat.toBundle());
            }
        });

        commentAdapter = new CommentAdapter(this, new ArrayList<Comment>(), false);
        commentAdapter.setOnItemClickListener(new OnItemClickListeners<Comment>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, Comment data, int position) {

            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(PostDetialActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commentAdapter);
        queryComment(currentPage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Config.SUCCESS_RESULT_CODE){
            queryComment(currentPage);
        }
    }

    public void queryComment(final int page){
        toggleShowLoading(true);
        if(page <= 0){
            //判断页码输入错误
        }
        BmobQuery<Comment> query = new BmobQuery<Comment>();
        query.setLimit(PAGE_SIZE);
        query.setSkip(PAGE_SIZE * (page - 1));
        query.addWhereEqualTo("post",post);
        query.include("author");
//        query.order("-createdAt");
        //执行查询方法
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> object, BmobException e) {
                if(e==null){
                    toggleShowLoading(false);
                    commentAdapter.setNewData(object);
                }else{
                    toggleShowError(e.getMessage());
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }
}
