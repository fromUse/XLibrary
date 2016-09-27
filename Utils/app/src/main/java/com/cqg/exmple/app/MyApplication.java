package com.cqg.exmple.app;

import android.app.Application;

import com.library.Tools;

/**
 * Created by chen on 2016/9/27.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Tools.init(getApplicationContext());
    }
}
