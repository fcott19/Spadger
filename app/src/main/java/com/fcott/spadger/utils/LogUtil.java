package com.fcott.spadger.utils;

import android.util.Log;

import com.fcott.spadger.Config;

/**
 * Created by Administrator on 2016/12/14.
 */
public class LogUtil {
    private static final String TAG = "---TAG---";

    public static void log(String msg){
        if(Config.NEED_LOG){
            Log.w(TAG,msg);
        }
    }

    public static void log(String tag,String msg){
        if(Config.NEED_LOG){
            Log.w(tag,msg);
        }
    }
}
