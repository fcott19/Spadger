package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.fcott.spadger.R;
import com.fcott.spadger.ui.widget.HackyViewPager;
import com.fcott.spadger.utils.glideutils.ImageLoader;

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
        viewPager.setAdapter(new SamplePagerAdapter());
        viewPager.setCurrentItem(position);
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imagCollectionBeenList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            ImageLoader.getInstance().load(mContext,imagCollectionBeenList.get(position),photoView);

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

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
}
