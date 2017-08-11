package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcott.spadger.App;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieBean;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class MovieListAdapter extends BaseAdapter<MovieBean.MessageBean.MoviesBean> {

    public MovieListAdapter(Context context, List<MovieBean.MessageBean.MoviesBean> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
    }

    @Override
    protected void convert(ViewHolder holder, MovieBean.MessageBean.MoviesBean data) {
        ImageView imageView = holder.getView(R.id.img_cover);
        TextView descript = holder.getView(R.id.tv_descript);
        TextView title = holder.getView(R.id.tv_title);

        ImageLoader.getInstance().load(App.getInstance(),
                data.getCoverImg(), imageView);
        descript.setText(data.getDescription());
        title.setText(data.getName());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_movie;
    }
}
