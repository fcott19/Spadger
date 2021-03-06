package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.fcott.spadger.R;
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
        TextView tv = holder.getView(R.id.tv_menu);
        tv.setText(data);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_menu;
    }
}
