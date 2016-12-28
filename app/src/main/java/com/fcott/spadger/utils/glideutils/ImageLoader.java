package com.fcott.spadger.utils.glideutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.PreloadTarget;
import com.bumptech.glide.request.target.Target;
import com.fcott.spadger.R;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by fcott on 2016/9/19.
 */
public class ImageLoader {
    final PreloadTarget<Bitmap> target = PreloadTarget.obtain(0, 0);
    public static ImageLoader getInstance(){
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder{
        private static final ImageLoader INSTANCE = new ImageLoader();
    }

    private ImageLoader(){}

    public void load(Context context, String url, ImageView iv) {
        Glide.with(context)
                .load(url)
                .thumbnail(0.1f)
                .override(500,600)
                .priority(Priority.IMMEDIATE)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//让Glide既缓存全尺寸图片，下次在任何ImageView中加载图片的时候，全尺寸的图片将从缓存中取出，重新调整大小，然后缓存
                .crossFade()
                .into(iv);
    }
    public void load(Context context, String url, ImageView iv,int width,int height) {
        Glide.with(context)
                .load(url)
                .thumbnail(0.1f)
                .override(width,height)
                .priority(Priority.IMMEDIATE)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)//让Glide既缓存全尺寸图片，下次在任何ImageView中加载图片的时候，全尺寸的图片将从缓存中取出，重新调整大小，然后缓存
                .crossFade()
                .into(iv);
    }

    public void load(Context context, int resId, ImageView iv) {
        Glide.with(context)
                .load(resId)
                .crossFade()
                .into(iv);
    }

    /**
     * 需要在子线程执行
     *
     * @param context
     * @param url
     * @return
     */
    public Bitmap load(Context context, String url) {
        try {
            return Glide.with(context)
                    .load(url)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void preLoad(Context context,String url){
        Glide.with(context)
                .load(url)
                .asBitmap()
                .override(500,400)
                .into(target);
    }

    public File downloadOriginalImage(Context context,String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        FutureTarget<File> future = Glide.with(context)
                .load(url)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        try {
            return future.get(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }
}
