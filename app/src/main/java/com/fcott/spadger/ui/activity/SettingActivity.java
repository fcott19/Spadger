package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.fcott.spadger.R;
import com.fcott.spadger.utils.FileUtil;
import com.fcott.spadger.utils.GeneralSettingUtil;

import butterknife.Bind;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.switch_movie_mode)
    public Switch switchMovieMode;
    @Bind(R.id.switch_prohibit_no_wifi)
    public Switch switchProhibitNoWifi;
    @Bind(R.id.switch_window_movie)
    public Switch switchWindowMovie;
    @Bind(R.id.switch_perload)
    public Switch switchPerLoad;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_setting;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void initViews() {

        ActionBar mActionBar=getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle("设置");

        switchMovieMode.setChecked(GeneralSettingUtil.isOpenWebMovieMode());
        switchMovieMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralSettingUtil.setOpenWebMoviewMode(isChecked);
                if(isChecked)
                    switchWindowMovie.setVisibility(View.VISIBLE);
                else
                    switchWindowMovie.setVisibility(View.GONE);
            }
        });
        if(GeneralSettingUtil.isOpenWebMovieMode())
            switchWindowMovie.setVisibility(View.VISIBLE);
        else
            switchWindowMovie.setVisibility(View.GONE);

        switchProhibitNoWifi.setChecked(GeneralSettingUtil.isProhibitNoWifi());
        switchProhibitNoWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralSettingUtil.setProhibitNoWifi(isChecked);
            }
        });

        switchWindowMovie.setChecked(GeneralSettingUtil.isWindowMovie());
        switchWindowMovie.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralSettingUtil.setWindowMovie(isChecked);
            }
        });

        switchPerLoad.setChecked(GeneralSettingUtil.isPerLoad());
        switchPerLoad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralSettingUtil.setPerLoad(isChecked);
            }
        });
    }

    public void clearVideoCache(View view){
        FileUtil.clearVideoCache();
    }
}
