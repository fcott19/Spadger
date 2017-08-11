package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcott.spadger.App;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.ActorBean;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2017/8/11.
 */

public class ActorAdapter extends BaseAdapter<ActorBean.MessageBean.DataBean> {
    private Context context;

    public ActorAdapter(Context context, List<ActorBean.MessageBean.DataBean> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, ActorBean.MessageBean.DataBean data) {
//        ImageView imageView = (ImageView)holder.getView(R.id.iv_actor);
//        Glide.with(context)
//                .load(data.getPic())
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .into(imageView);

        TextView title = holder.getView(R.id.tv_title);
        TextView date = holder.getView(R.id.tv_date);
        ImageView cover = holder.getView(R.id.img_cover);

        title.setText(data.getName());
        date.setVisibility(View.GONE);
        ImageLoader.getInstance().load(App.getInstance(),
                data.getPic(), cover);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_vedio;
    }
}
