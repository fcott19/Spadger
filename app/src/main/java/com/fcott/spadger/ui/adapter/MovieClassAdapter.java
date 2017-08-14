package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.MovieClassBean;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/8/14.
 */

public class MovieClassAdapter extends BaseAdapter<MovieClassBean.MessageBean.DataBean> {


    public MovieClassAdapter(Context context, List<MovieClassBean.MessageBean.DataBean> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
    }

    @Override
    protected void convert(ViewHolder holder, MovieClassBean.MessageBean.DataBean data) {
        TextView tv = holder.getView(R.id.tv_menu);
        tv.setText(data.getName());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_menu;
    }
}
