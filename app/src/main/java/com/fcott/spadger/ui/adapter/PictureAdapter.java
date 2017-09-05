package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fcott.spadger.R;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

/**
 * Created by Administrator on 2016/12/28.
 */

public class PictureAdapter extends BaseAdapter<String> {
    private Context context;
    private ArrayList<String> dataList = new ArrayList<>();
    int screenWidth;


    public PictureAdapter(Context context, List<String> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
        this.context = context;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels
                - (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP,12,context.getResources().getDisplayMetrics());
    }

    @Override
    protected void convert(final ViewHolder holder,final String data) {
        final ImageView imageView = holder.getView(R.id.img_cover);
        if(!dataList.contains(data)){
            ViewGroup.LayoutParams para = imageView.getLayoutParams();
            para.height = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP,270,context.getResources().getDisplayMetrics());
        }
        Glide.with(context)
                .load(data)
                .priority(Priority.IMMEDIATE)
                .placeholder(R.drawable.ic_launcher_round)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if(!dataList.contains(data)){
                            dataList.add(data);
                        }
                        int height = screenWidth * resource.getIntrinsicHeight() / resource.getIntrinsicWidth();
                        ViewGroup.LayoutParams para = imageView.getLayoutParams();
                        para.height = height;
                        return false;
                    }
                })
                .into(imageView);

    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_picture;
    }
}
