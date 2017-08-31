package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.TextView;

import com.fcott.spadger.App;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.NovelListItemBean;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

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

        if(App.getInstance().perLoadList.contains(data.getUrl())){
            holder.setBgRes(R.id.bg,R.drawable.selector_btn_preload);
        }else {
            holder.setBgRes(R.id.bg,R.drawable.selector_btn_bg);
        }
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_novel;
    }
}
