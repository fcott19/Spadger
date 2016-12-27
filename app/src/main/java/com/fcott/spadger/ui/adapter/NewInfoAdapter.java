package com.fcott.spadger.ui.adapter;

import android.content.Context;

import com.fcott.spadger.model.bean.ItemBean;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2016/12/27.
 */

public class NewInfoAdapter extends BaseAdapter<ItemBean> {
    public NewInfoAdapter(Context context, List<ItemBean> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
    }

    @Override
    protected void convert(ViewHolder holder, ItemBean data) {

    }

    @Override
    protected int getItemLayoutId() {
        return 0;
    }
}
