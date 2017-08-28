package com.fcott.spadger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.view.View;
import android.widget.Toast;

import com.fcott.spadger.App;
import com.fcott.spadger.R;
import com.fcott.spadger.ui.activity.kv.KvMainActivity;
import com.fcott.spadger.ui.activity.look.TokenCheckActivity;
import com.fcott.spadger.utils.GeneralSettingUtil;
import com.fcott.spadger.utils.LogUtil;
import com.fcott.spadger.utils.netstatus.NetChangeObserver;
import com.fcott.spadger.utils.netstatus.NetStateReceiver;
import com.fcott.spadger.utils.netstatus.NetUtils;

import java.io.IOException;

public class MainActivity extends BaseActivity implements NetChangeObserver {

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void initViews() {
        //注册网络监听
        NetStateReceiver.registerObserver(this);
        try {
            LogUtil.log(Environment.getExternalStorageDirectory().getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goKv(View view){
        startActivity(new Intent(MainActivity.this,KvMainActivity.class));
    }

    public void goMovie(View view){
        startActivity(new Intent(MainActivity.this,TokenCheckActivity.class));
    }

    public void goSetting(View view){
        startActivity(new Intent(MainActivity.this,SettingActivity.class));
    }

    public void goTest(View view){
        startActivity(new Intent(MainActivity.this,TestActivity.class));
    }

    @Override
    protected void onDestroy() {
        //反注册网络监听
        NetStateReceiver.removeRegisterObserver(this);
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }

    @Override
    public void onNetConnected(NetUtils.NetType type) {
        if(type != NetUtils.NetType.WIFI){
            if(GeneralSettingUtil.isProhibitNoWifi()){
                Toast.makeText(MainActivity.this,getString(R.string.prohibit_no_wifi),Toast.LENGTH_SHORT).show();
                App.getInstance().cleanActivity();
            }
        }
    }

    @Override
    public void onNetDisConnect() {

    }
}
