package com.bz.app.utils;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;

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

    public static LatLng parseLatLng (String latLngStr) {
        if (latLngStr == null || latLngStr.equals("") || latLngStr.equals("[]")) {
            return null;
        }
        String[] loc = latLngStr.split(",");
        LatLng latLng = new LatLng(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));

        return latLng;
    }

    public static ArrayList<LatLng> parseLatLngs(String latLngStr) {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        String[] latLngStrs = latLngStr.split(";");
        for (int i = 0; i < latLngStrs.length; i++) {
            LatLng latLng = Utils.parseLatLng(latLngStrs[i]);
            if (latLng != null) latLngs.add(latLng);
        }
        return latLngs;
    }

}
