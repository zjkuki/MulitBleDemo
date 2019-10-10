package com.kuki.mulitbledemo;

import android.app.Application;

import com.inuker.bluetooth.library.BluetoothContext;

/**
 * Created by kuki.
 */
public class MyApplication extends Application {

    private static MyApplication instance;

    public static Application getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        BluetoothContext.set(this);
    }
}
