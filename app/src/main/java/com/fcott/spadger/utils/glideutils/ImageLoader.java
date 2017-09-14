package com.fcott.spadger.utils.glideutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
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
import com.fcott.spadger.model.http.MainPageService;
import com.fcott.spadger.model.http.YirenService;
import com.fcott.spadger.model.http.utils.RetrofitUtils;
import com.fcott.spadger.utils.ACache;
import com.fcott.spadger.utils.JsoupUtil;
import com.fcott.spadger.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by fcott on 2016/9/19.
 */
public class ImageLoader {
    public static ImageLoader getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        private static final ImageLoader INSTANCE = new ImageLoader();
    }

    private ImageLoader() {
    }

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

    public void loadCircle(Context context, String url,ImageView iv){
        Glide.with(context)
                .load(url)
                .transform(new GlideCircleTransform(context))
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
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

    public Target preLoad(Context context, String url) {
        return Glide.with(context)
                .load(url)
                .asBitmap()
                .priority(Priority.NORMAL)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .preload();
    }

    //设置错误监听
    RequestListener<String, Bitmap> errorListener = new RequestListener<String, Bitmap>() {
        @Override
        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {

            Log.e("onException", e.toString() + "  model:" + model + " isFirstResource: " + isFirstResource);

            return false;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
            Log.e("onResourceReady", "isFromMemoryCache:" + isFromMemoryCache + "  model:" + model + " isFirstResource: " + isFirstResource);
            return false;
        }
    };

    public String getImageCachePath(String url) {
        FutureTarget<File> future = Glide.with(App.getInstance().getApplicationContext()).load(url).downloadOnly(100, 100);
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

    private YirenService yirenService;

    public void perLoadYirenImage(final String url, final ArrayList<Target> targetList) {
        if (yirenService == null)
            yirenService = RetrofitUtils.getInstance().create1(YirenService.class);
        ACache mCache = ACache.get(App.getInstance().getApplicationContext());
        String value = mCache.getAsString(url);//取出缓存
        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            ArrayList<String> dataList = JsoupUtil.parseYirenPic(value);
            for (String string : dataList) {
                Target target = ImageLoader.getInstance().preLoad(App.getInstance().getApplicationContext(), string);
                if (targetList != null)
                    targetList.add(target);
            }
            return;
        }
        yirenService
                .getData(url)
                .map(new Func1<String, ArrayList<String>>() {
                    @Override
                    public ArrayList<String> call(String s) {
                        ACache aCache = ACache.get(App.getInstance().getApplicationContext());
                        aCache.put(url, s);
                        ArrayList<String> dataList = JsoupUtil.parseYirenPic(s);
                        return dataList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("response---", e.toString());
                    }

                    @Override
                    public void onNext(ArrayList<String> dataList) {
                        for (String string : dataList) {
                            targetList.add(ImageLoader.getInstance().preLoad(App.getInstance().getApplicationContext(), string));
                        }
                    }
                });
    }

    private MainPageService mainPageService;
    public void perLoadImage(final String url, final ArrayList<Target> targetList) {
        if (mainPageService == null)
            mainPageService = RetrofitUtils.getInstance().create1(MainPageService.class);
        ACache mCache = ACache.get(App.getInstance().getApplicationContext());
        String value = mCache.getAsString(url);//取出缓存
        //显示缓存
        if (!TextUtils.isEmpty(value)) {
            ArrayList<String> dataList = JsoupUtil.parsePictureDetial(value);
            for (String string : dataList) {
                Target target = preLoad(App.getInstance().getApplicationContext(), string);
                if (targetList != null)
                    targetList.add(target);
            }
            return;
        }
        mainPageService
                .getData(url)
                .map(new Func1<String, ArrayList<String>>() {
                    @Override
                    public ArrayList<String> call(String s) {
                        ACache aCache = ACache.get(App.getInstance().getApplicationContext());
                        aCache.put(url, s);
                        ArrayList<String> dataList = JsoupUtil.parsePictureDetial(s);
                        return dataList;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("onerror", e.toString()+"--");
                    }

                    @Override
                    public void onNext(ArrayList<String> dataList) {
                        for (String string : dataList) {
                            LogUtil.log(string);
                            Target target = preLoad(App.getInstance().getApplicationContext(), string);
                            if (targetList != null)
                                targetList.add(target);
                        }
                    }
                });
    }
}
