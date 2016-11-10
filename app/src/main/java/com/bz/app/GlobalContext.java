package com.bz.app;

import android.app.Application;

/**
 * Created by ThinkPad User on 2016/11/7.
 */

public class GlobalContext extends Application {

    private static GlobalContext context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static GlobalContext getInstance() {
        return context;
    }
}
