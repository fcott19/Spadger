package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcott.spadger.R;
import com.fcott.spadger.model.entity.Comment;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2017/9/6.
 */

public class CommentAdapter extends BaseAdapter<Comment> {
    private Context context;

    public CommentAdapter(Context context, List<Comment> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, Comment data) {
        ImageView imageView = holder.getView(R.id.iv_head);
        TextView content = holder.getView(R.id.tv_content);
        TextView tvNickName = holder.getView(R.id.tv_nick_name);
        TextView tvCreatTime = holder.getView(R.id.tv_creat_time);

        if(data.getAuthor().getHeadImage() != null){
            ImageLoader.getInstance().loadCircle(context,data.getAuthor().getHeadImage(),imageView);
        }
        tvNickName.setText(data.getAuthor().getNickName());
        content.setText(data.getContent());
        tvCreatTime.setText(data.getCreatedAt());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_comment;
    }
}
