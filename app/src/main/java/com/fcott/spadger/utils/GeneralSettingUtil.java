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

    public static void setOpenWebMoviewMode(boolean openWebMovieMode){
        SharedPreferences.Editor sharedata = App.getInstance().getSharedPreferences(Config.SP_GENERAL_SETTING, Context.MODE_PRIVATE).edit();
        sharedata.putBoolean(MOVIEMODE,openWebMovieMode);
        sharedata.commit();
    }

    public static boolean isOpenWebMovieMode(){
        SharedPreferences pref = App.getInstance().getSharedPreferences(Config.SP_GENERAL_SETTING, Context.MODE_PRIVATE);
        return pref.getBoolean(MOVIEMODE, false);
    }
}
