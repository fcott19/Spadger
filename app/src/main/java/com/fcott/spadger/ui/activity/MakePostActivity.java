package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fcott.spadger.Config;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.model.entity.Comment;
import com.fcott.spadger.model.entity.Post;
import com.fcott.spadger.utils.LogUtil;

import butterknife.Bind;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.fcott.spadger.utils.UserManager.getCurrentUser;

public class MakePostActivity extends BaseActivity {

    private MovieBean.MessageBean.MoviesBean moviesBean;
    private String dataFrom = null;
    private Post post;

    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_content)
    EditText etContent;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_make_post;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        dataFrom = bundle.getString(Config.DATA_FROM);
        post = (Post) bundle.getSerializable("post");
        moviesBean = (MovieBean.MessageBean.MoviesBean) bundle.getSerializable("AV");
    }

    @Override
    protected void initViews() {
        if(dataFrom == null) {
            return;
        }else if(dataFrom.equals(Config.DATA_FROM_NEWPOST)){
            etTitle.setVisibility(View.VISIBLE);
        }else if(dataFrom.equals(Config.DATA_FROM_ADDASK)){
            etTitle.setVisibility(View.GONE);
        }else if(dataFrom.equals(Config.DATA_FROM_SHARE_AV)){
            etTitle.setVisibility(View.VISIBLE);
            etTitle.setText(moviesBean.getName());
            etContent.setText(moviesBean.getDescription());
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etContent.getText().length() < 10){
                    Toast.makeText(MakePostActivity.this,"内容至少输入10个字",Toast.LENGTH_SHORT).show();
                    return;
                }else if(dataFrom.equals(Config.DATA_FROM_NEWPOST) && etTitle.getText().length() < 2){
                    Toast.makeText(MakePostActivity.this,"标题至少输入2个字",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dataFrom.equals(Config.DATA_FROM_NEWPOST)){
                    newPost(etTitle.getText().toString(),etContent.getText().toString());
                }else if(dataFrom.equals(Config.DATA_FROM_ADDASK) && post != null){
                    addComment(post,etContent.getText().toString());
                }else if(dataFrom.equals(Config.DATA_FROM_SHARE_AV) && moviesBean != null) {
                    newPost(etTitle.getText().toString(), etContent.getText().toString(), moviesBean);
                }
            }
        });
    }

    public void newPost(final String title, final String content, final MovieBean.MessageBean.MoviesBean moviesBean){
        floatingActionButton.setEnabled(false);
        Post post = new Post();
        post.setTitle(title);
        post.setAuthor(getCurrentUser());
        post.setContent(content);
        if(moviesBean != null && moviesBean.getObjectId() != null) {
            post.setMoviesBean(moviesBean);
        }
        post.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                floatingActionButton.setEnabled(true);
                if(e==null){
                    Toast.makeText(MakePostActivity.this,"发帖成功",Toast.LENGTH_SHORT).show();
                    setResult(Config.SUCCESS_RESULT_CODE);
                    ActivityCompat.finishAfterTransition(MakePostActivity.this);
                }else{
                    LogUtil.log(e.getMessage()+e.getErrorCode());
                    Toast.makeText(MakePostActivity.this,"发帖失败："+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void newPost(final String title,final String content){
        newPost(title,content,null);
    }

    public void addComment(final Post post,final String content){
        Post tempPost = post;
        floatingActionButton.setEnabled(false);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(getCurrentUser());
        comment.setPost(tempPost);
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                floatingActionButton.setEnabled(true);
                if(e==null){
                    Toast.makeText(MakePostActivity.this,"回复成功",Toast.LENGTH_SHORT).show();
                    setResult(Config.SUCCESS_RESULT_CODE);
                    ActivityCompat.finishAfterTransition(MakePostActivity.this);
                }else{
                    LogUtil.log(e.getMessage()+e.getErrorCode());
                    Toast.makeText(MakePostActivity.this,"回复失败："+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
