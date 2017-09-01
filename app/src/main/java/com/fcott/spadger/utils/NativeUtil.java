package com.fcott.spadger.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fcott.spadger.App;
import com.fcott.spadger.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fcott on 2016/12/29.
 */

public class NativeUtil {
    /**
     * 与当前时间比较早晚
     *
     * @param endTime
     *            需要比较的时间
     * @return 输入的时间比现在时间晚则返回true
     */
    public static boolean isBeforEndTime(String endTime) {
        boolean isDayu = false;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try {
            Date parse = dateFormat.parse(endTime);
            Date parse1 = new Date();

            long diff = parse1.getTime() - parse.getTime();
            if (diff <= 0) {
                isDayu = true;
            } else {
                isDayu = false;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return isDayu;
    }


    /**
     * prefix of ascii string of native character
     */
    private static String PREFIX = "\\u";

    /**
     * NativeUtil to ascii string. It's same as execut native2ascii.exe.
     *
     * @param str native string
     * @return ascii string
     */
    public static String native2Ascii(String str) {
        char[] chars = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            sb.append(char2Ascii(chars[i]));
        }
        return sb.toString();
    }

    /**
     * NativeUtil character to ascii string.
     *
     * @param c native character
     * @return ascii string
     */
    private static String char2Ascii(char c) {
        if (c > 255) {
            StringBuilder sb = new StringBuilder();
            sb.append(PREFIX);
            int code = (c >> 8);
            String tmp = Integer.toHexString(code);
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
            code = (c & 0xFF);
            tmp = Integer.toHexString(code);
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
            return sb.toString();
        } else {
            return Character.toString(c);
        }
    }

    /**
     * Ascii to native string. It's same as execut native2ascii.exe -reverse.
     *
     * @param str ascii string
     * @return native string
     */
    public static String ascii2Native(String str) {
        StringBuilder sb = new StringBuilder();
        int begin = 0;
        int index = str.indexOf(PREFIX);
        while (index != -1) {
            sb.append(str.substring(begin, index));
            sb.append(ascii2Char(str.substring(index, index + 6)));
            begin = index + 6;
            index = str.indexOf(PREFIX, begin);
        }
        sb.append(str.substring(begin));
        return sb.toString();
    }

    /**
     * Ascii to native character.
     *
     * @param str ascii string
     * @return native character
     */
    private static char ascii2Char(String str) {
        if (str.length() != 6) {
            throw new IllegalArgumentException(
                    "Ascii string of a native character must be 6 character.");
        }
        if (!PREFIX.equals(str.substring(0, 2))) {
            throw new IllegalArgumentException(
                    "Ascii string of a native character must start with \"\\u\".");
        }
        String tmp = str.substring(2, 4);
        int code = Integer.parseInt(tmp, 16) << 8;
        tmp = str.substring(4, 6);
        code += Integer.parseInt(tmp, 16);
        return (char) code;
    }

    public static boolean needUpdate(String tag) {

        Calendar c = Calendar.getInstance();
        int mHour = 0;//时c.get(Calendar.HOUR_OF_DAY)
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
        SharedPreferences pref = App.getInstance().getSharedPreferences(Config.SP_TIME+tag, Context.MODE_PRIVATE);
        String time = pref.getString("time", "");//第二个参数为默认值
        if(!time.equals(mDay+""+mHour)){
            SharedPreferences.Editor sharedata = App.getInstance().getSharedPreferences(Config.SP_TIME+tag, Context.MODE_PRIVATE).edit();
            sharedata.putString("token", mDay+""+mHour);
            sharedata.commit();
            return true;
        }else {
            return false;
        }

    }
}

