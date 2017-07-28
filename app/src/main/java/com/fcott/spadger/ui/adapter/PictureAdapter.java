package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fcott.spadger.R;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */

public class PictureAdapter extends BaseAdapter<String> {
    private Context context;

    public PictureAdapter(Context context, List<String> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, String data) {
        ImageView imageView = holder.getView(R.id.img_cover);
//        ImageLoader.getInstance().load(App.getInstance(), data, imageView);
        Glide.with(context)
                .load(data)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_picture;
    }
}
