package com.bz.app.utils;

/**
 * Created by ThinkPad User on 2016/11/16.
 */

public class Utils {

    public static String getTimeStr(int time) {

        int hour = time / 3600;
        String hourStr = String.valueOf(hour);
        int min = time % 3600 / 60;
        String minStr = String.valueOf(min);
        int sec = time % 3600 % 60;
        String secStr = String.valueOf(sec);

        if (hour < 10) hourStr = "0" + hourStr;
        if (min < 10) minStr = "0" + minStr;
        if (sec < 10) secStr = "0" + secStr;

        return hourStr + ":" + minStr + ":" + secStr;
    }
}
