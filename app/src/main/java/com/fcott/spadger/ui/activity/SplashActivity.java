package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.fcott.spadger.R;
import com.fcott.spadger.ui.activity.look.TokenCheckActivity;
import com.fcott.spadger.utils.UserManager;

public class SplashActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_splash;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {
        if(UserManager.getCurrentUser() == null){
            startActivity(new Intent(SplashActivity.this,LoginActivity.class));
        }else {
            startActivity(new Intent(SplashActivity.this,TokenCheckActivity.class));
        }
        finish();
    }
}
