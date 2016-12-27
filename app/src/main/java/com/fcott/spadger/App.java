package com.fcott.spadger;

import android.app.Activity;
import android.app.Application;

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
