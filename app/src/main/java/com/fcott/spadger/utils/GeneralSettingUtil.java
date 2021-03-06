package com.fcott.spadger.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fcott.spadger.App;
import com.fcott.spadger.Config;

/**
 * Created by Administrator on 2017/8/17.
 */

public class GeneralSettingUtil {
    public static final String MOVIEMODE = "movieMode";
    public static final String PROHIBIT_NO_WIFI = "PROHIBIT_NO_WIFI";
    public static final String WINDOW_MOVIE = "WINDOW_MOVIE";
    public static final String PER_LOAD = "PER_LOAD";

    public static void setOpenWebMoviewMode(boolean openWebMovieMode){
        SharedPreferences.Editor sharedata = App.getInstance().getSharedPreferences(Config.SP_GENERAL_SETTING, Context.MODE_PRIVATE).edit();
        sharedata.putBoolean(MOVIEMODE,openWebMovieMode);
        sharedata.commit();
    }

    public static boolean isOpenWebMovieMode(){
        SharedPreferences pref = App.getInstance().getSharedPreferences(Config.SP_GENERAL_SETTING, Context.MODE_PRIVATE);
        return pref.getBoolean(MOVIEMODE, false);
    }

    public static void setProhibitNoWifi(boolean prohibitNoWifi){
        SharedPreferences.Editor sharedata = App.getInstance().getSharedPreferences(Config.SP_GENERAL_SETTING, Context.MODE_PRIVATE).edit();
        sharedata.putBoolean(PROHIBIT_NO_WIFI,prohibitNoWifi);
        sharedata.commit();
    }

    public static boolean isProhibitNoWifi(){
        SharedPreferences pref = App.getInstance().getSharedPreferences(Config.SP_GENERAL_SETTING, Context.MODE_PRIVATE);
        return pref.getBoolean(PROHIBIT_NO_WIFI, false);
    }

    public static void setWindowMovie(boolean windowMovie){
        SharedPreferences.Editor sharedata = App.getInstance().getSharedPreferences(Config.SP_GENERAL_SETTING, Context.MODE_PRIVATE).edit();
        sharedata.putBoolean(WINDOW_MOVIE,windowMovie);
        sharedata.commit();
    }

    public static boolean isWindowMovie(){
        SharedPreferences pref = App.getInstance().getSharedPreferences(Config.SP_GENERAL_SETTING, Context.MODE_PRIVATE);
        return pref.getBoolean(WINDOW_MOVIE, false);
    }

    public static void setPerLoad(boolean perLoad){
        SharedPreferences.Editor sharedata = App.getInstance().getSharedPreferences(Config.SP_GENERAL_SETTING, Context.MODE_PRIVATE).edit();
        sharedata.putBoolean(PER_LOAD,perLoad);
        sharedata.commit();
    }

    public static boolean isPerLoad(){
        SharedPreferences pref = App.getInstance().getSharedPreferences(Config.SP_GENERAL_SETTING, Context.MODE_PRIVATE);
        return pref.getBoolean(PER_LOAD, false);
    }
}
