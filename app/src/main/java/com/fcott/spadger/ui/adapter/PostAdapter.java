package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcott.spadger.R;
import com.fcott.spadger.model.entity.Post;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2017/9/6.
 */

public class PostAdapter extends BaseAdapter<Post> {
    private Context context;

    public PostAdapter(Context context, List<Post> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, Post data) {
        ImageView imageView = holder.getView(R.id.iv_head);
        TextView title = holder.getView(R.id.tv_title);
        TextView content = holder.getView(R.id.tv_content);
        TextView tvNickName = holder.getView(R.id.tv_nick_name);

        if(data.getAuthor().getHeadImage() != null){
            ImageLoader.getInstance().loadCircle(context,data.getAuthor().getHeadImage(),imageView);
        }
        title.setText(data.getTitle());
        content.setText(data.getContent());
        tvNickName.setText(data.getAuthor().getNickName());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_bbs;
    }
}
