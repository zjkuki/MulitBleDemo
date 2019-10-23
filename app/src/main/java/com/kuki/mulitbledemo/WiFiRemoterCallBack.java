package com.kuki.mulitbledemo;

import android.util.Log;

import io.fogcloud.sdk.mqtt.api.MQTT;
import io.fogcloud.sdk.mqtt.helper.ListenDeviceCallBack;

public class WiFiRemoterCallBack extends ListenDeviceCallBack {
    private String TAG = "WiFiRemoterCallBack";

    public WiFiRemoterCallBack() {
    };

    @Override
    public void onSuccess(int code, String message) {
        Log.d(TAG, message);
        //send2handler(_EL_S, message);
    }

    @Override
    public void onFailure(int code, String message) {
        Log.d(TAG, code + " - " + message);
        //send2handler(_EL_F, message);
    }

    @Override
    public void onDeviceStatusReceived(int code, String messages) {
        Log.d(TAG + code, messages);
        //send2handler(_EL_S, messages);
    }
}
