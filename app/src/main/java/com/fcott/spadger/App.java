package com.fcott.spadger;

import android.app.Activity;
import android.app.Application;

import com.hly.easyretrofit.retrofit.NetWorkRequest;

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
        instance = this;
        activityList = new ArrayList<>();
        NetWorkRequest.getInstance().init(this, "http://test.kuaikuaikeji.com/kas/");
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
