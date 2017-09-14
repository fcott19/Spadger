package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ItemBean;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2016/12/27.
 */

public class KvMenuAdapter extends BaseAdapter<ItemBean> {

    public KvMenuAdapter(Context context, List<ItemBean> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
    }

    @Override
    protected void convert(ViewHolder holder, ItemBean data) {
        TextView tv = holder.getView(R.id.tv_menu);
        tv.setText(data.getTitle());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_menu;
    }
}
