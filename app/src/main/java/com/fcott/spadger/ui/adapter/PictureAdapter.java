package com.fcott.spadger.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.fcott.spadger.App;
import com.fcott.spadger.R;
import com.fcott.spadger.model.bean.NovelListItemBean;
import com.fcott.spadger.ui.adapter.baseadapter.BaseAdapter;
import com.fcott.spadger.ui.adapter.baseadapter.ViewHolder;
import com.fcott.spadger.utils.glideutils.ImageLoader;

import java.util.List;

/**
 * Created by Administrator on 2016/12/28.
 */

public class PictureAdapter extends BaseAdapter<String> {

    public PictureAdapter(Context context, List<String> datas, boolean isOpenLoadMore) {
        super(context, datas, isOpenLoadMore);
    }

    @Override
    protected void convert(ViewHolder holder, String data) {
        ImageView imageView = holder.getView(R.id.img_cover);
        ImageLoader.getInstance().load(App.getInstance(), data, imageView);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_picture;
    }
}
