package com.fcott.spadger.utils.glideutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fcott.spadger.App;
import com.fcott.spadger.R;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by fcott on 2016/9/19.
 */
public class ImageLoader {
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
                .priority(Priority.NORMAL)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .preload();
    }

    //设置错误监听
    RequestListener<String,Bitmap> errorListener=new RequestListener<String, Bitmap>() {
        @Override
        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {

            Log.e("onException",e.toString()+"  model:"+model+" isFirstResource: "+isFirstResource);

            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
            Log.e("onResourceReady","isFromMemoryCache:"+isFromMemoryCache+"  model:"+model+" isFirstResource: "+isFirstResource);
            return false;
        }
    } ;

    public String getImageCachePath(String url){
        FutureTarget<File> future = Glide.with(App.getInstance().getApplicationContext()).load(url).downloadOnly(100,100);
        try {
            File cacheFile = future.get();
            String path = cacheFile.getAbsolutePath();
            return path;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
