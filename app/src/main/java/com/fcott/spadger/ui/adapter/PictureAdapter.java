package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.fcott.spadger.App;
import com.fcott.spadger.R;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.LogUtil;

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
    protected void convert(final ViewHolder holder, String data) {
        ImageView imageView = holder.getView(R.id.img_cover);
        Glide.with(context)
                .load(data)
                .thumbnail(0.1f)
                .priority(Priority.HIGH)
                .placeholder(R.drawable.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .dontAnimate()
                .into(imageView);

    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_picture;
    }
}
