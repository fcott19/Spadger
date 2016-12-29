package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcott.spadger.App;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.NovelListItemBean;
import com.fcott.spadger.model.bean.VedioListItemBean;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */

public class NovelListAdapter extends BaseAdapter<NovelListItemBean> {

    public NovelListAdapter(Context context, List<NovelListItemBean> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
    }

    @Override
    protected void convert(ViewHolder holder, NovelListItemBean data) {
        TextView title = holder.getView(R.id.tv_title);
        TextView date = holder.getView(R.id.tv_date);

        title.setText(data.getTitle());
        date.setText(data.getDate());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_novel;
    }
}
