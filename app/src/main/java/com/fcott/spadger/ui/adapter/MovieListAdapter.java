package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class MovieListAdapter extends BaseAdapter<MovieBean.MessageBean.MoviesBean> {
    private Context context;

    public MovieListAdapter(Context context, List<MovieBean.MessageBean.MoviesBean> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, MovieBean.MessageBean.MoviesBean data) {
        ImageView imageView = holder.getView(R.id.img_cover);
        TextView title = holder.getView(R.id.tv_title);

//        ImageLoader.getInstance().load(App.getInstance(),
//                data.getCoverImg(), imageView);
        title.setText(data.getName());
        Glide.with(context)
                .load(data.getCoverImg())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_movie;
    }
}
