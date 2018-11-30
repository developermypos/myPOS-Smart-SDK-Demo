package com.mypos.mypospaymentdemo;

import android.app.Application;

import com.mypos.mypospaymentdemo.util.PersistentDataManager;

public class PosApplication extends Application {
    public void onCreate() {
        super.onCreate();
        PersistentDataManager.setContext(this);
    }
}
