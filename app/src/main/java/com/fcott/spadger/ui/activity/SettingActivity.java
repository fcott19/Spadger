package com.fcott.spadger.ui.activity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.fcott.spadger.R;
import com.fcott.spadger.utils.GeneralSettingUtil;

import butterknife.Bind;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.switch_movie_mode)
    public Switch switchMovieMode;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_setting;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {

        switchMovieMode.setChecked(GeneralSettingUtil.isOpenWebMovieMode());
        switchMovieMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GeneralSettingUtil.setOpenWebMoviewMode(isChecked);
            }
        });
    }
}
