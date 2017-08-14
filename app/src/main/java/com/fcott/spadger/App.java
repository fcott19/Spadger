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
