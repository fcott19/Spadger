package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;

import com.fcott.spadger.R;

public class MainActivity extends BaseActivity {

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {

    }

    public void goKv(View view){
        startActivity(new Intent(MainActivity.this,KvMainActivity.class));
    }

    public void goMovie(View view){
        startActivity(new Intent(MainActivity.this,TokenCheckActivity.class));
    }

    @Override
    protected void onDestroy() {
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }
}
