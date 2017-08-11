package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class MovieTypeAdapter extends BaseAdapter<String> {

    public MovieTypeAdapter(Context context, List<String> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
    }

    @Override
    protected void convert(ViewHolder holder, String data) {
        TextView title = holder.getView(android.R.id.text1);
        title.setText(data);
    }

    @Override
    protected int getItemLayoutId() {
        return android.R.layout.simple_list_item_1;
    }
}
