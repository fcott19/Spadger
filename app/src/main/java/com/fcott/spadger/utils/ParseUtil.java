package com.fcott.spadger.utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/12/27.
 */

public class ParseUtil {
    public static String parseUrl(String url) throws UnsupportedEncodingException {
        char[] c = url.toCharArray();
        StringBuffer sb = new StringBuffer();
        for(int i = 0;i < c.length;i++){
            Log.w("ccc","--"+String.valueOf(c[i])+"--");
            if (c[i] == '/' || c[i] == '.' || c[i] == ':'){
                sb.append(String.valueOf(c[i]));
            }else if(c[i] == ' '){
                sb.append("%20");
            }else{
                sb.append(new String(java.net.URLEncoder.encode(String.valueOf(c[i]),"utf-8").getBytes()));
            }
        }
        return sb.toString();
    }
}
