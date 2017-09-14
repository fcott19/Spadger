package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcott.spadger.R;
import com.fcott.spadger.model.entity.MainMenu;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/9/14.
 */

public class MineMenuAdapter extends BaseAdapter<MainMenu> {
    private Context context;

    public MineMenuAdapter(Context context, List<MainMenu> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, MainMenu data) {
        ImageView imageView = holder.getView(R.id.iv);
        TextView textView = holder.getView(R.id.tv);
        imageView.setImageResource(data.getIcon());
        imageView.setColorFilter(context.getResources().getColor(data.getTint()));
        textView.setText(data.getTitle());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_mine_menu;
    }
}
