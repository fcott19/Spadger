package com.fcott.spadger.ui.activity.kv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fcott.spadger.R;
import com.fcott.spadger.ui.activity.BaseActivity;
import com.fcott.spadger.ui.widget.HackyViewPager;

import java.util.ArrayList;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2016/9/29.
 */
public class PictureSinglelActivity extends BaseActivity {
    public static final String URL_TAG = "IMG_URL_TAG";
    public static final String POSITION_TAG = "POSITION_TAG";

    @Bind(R.id.vp_img_detial)
    HackyViewPager viewPager;

    private String url;
    private ArrayList<String> imagCollectionBeenList;
    private int position;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_picture_single;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {
        imagCollectionBeenList = bundle.getStringArrayList(URL_TAG);
        position = bundle.getInt(POSITION_TAG);
    }

    @Override
    protected void initViews() {

        viewPager.setPageMargin(75);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new SamplePagerAdapter());
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int p) {
                position = p;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imagCollectionBeenList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
//            ImageLoader.getInstance().load(mContext, imagCollectionBeenList.get(position), photoView);
            Glide.with(mContext)
                    .load(imagCollectionBeenList.get(position))
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
                            return false;
                        }
                    })
                    .into(photoView);

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // 这里指定了被共享的视图元素
//            ViewCompat.setTransitionName(photoView, "image");

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.putExtra("position", position); //将计算的值回传回去
            setResult(2, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
