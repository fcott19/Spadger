package com.fcott.spadger;

import android.app.Activity;
import android.app.Application;

import com.fcott.spadger.utils.netstatus.NetStateReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/27.
 */

public class App extends Application {
    private static App instance;
    private List<Activity> activityList;

    @Override
    public void onCreate() {
        super.onCreate();
        NetStateReceiver.registerNetworkStateReceiver(this);
        instance = this;
        activityList = new ArrayList<>();

//        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
//
//            @Override
//            public void onViewInitFinished(boolean arg0) {
//                // TODO Auto-generated method stub
//                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
//                Log.d("app", " onViewInitFinished is " + arg0);
//            }
//
//            @Override
//            public void onCoreInitFinished() {
//                // TODO Auto-generated method stub
//            }
//        };
        //x5内核初始化接口
//        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        NetStateReceiver.unRegisterNetworkStateReceiver(this);
    }

    public static App getInstance(){
        return instance;
    }

    public void addActivity(Activity activity){
        activityList.add(activity);
    }

    public void cleanActivity(){
        for(Activity activitie:activityList){
            if(activitie != null){
                activitie.finish();
            }
        }
    }
}
