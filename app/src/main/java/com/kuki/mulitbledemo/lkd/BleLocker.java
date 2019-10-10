package com.kuki.mulitbledemo.lkd;


import android.os.Handler;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.kuki.mulitbledemo.ClientManager;

import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;

public class BleLocker {
    private IBleLockerListener mIBleLockerListener;

    private String mMac;
    private UUID mService;
    private UUID mCharacter;

    private int mHeartBeatInterval = 2000;
    private int mTimeOut;

    private boolean mConnected = false;
    private BleGattProfile mBleGattProfile;

    private Handler mHandler = new Handler();

    public BleLocker(String BleMacAddr, Boolean isAutoConnect, int heartBeatInterval, IBleLockerListener callBack) {
        this.mMac = BleMacAddr;
        this.mHeartBeatInterval = heartBeatInterval;
        this.mIBleLockerListener = callBack;

        if(isAutoConnect){
            this.connect();
        }
    }

    public BleLocker(String BleName, String UUID) {

    }

    Runnable Heartbeat=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            mHandler.postDelayed(this, mHeartBeatInterval);
        }
    };

    public void connect(){
        BluetoothLog.v(String.format("onBluetooth Connecting... %s", mMac));

        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();

        ClientManager.getClient().connect(mMac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                BluetoothLog.v(String.format("profile:\n%s", profile));

                if (code == REQUEST_SUCCESS) {
                    //mAdapter.setGattProfile(profile);
                    mBleGattProfile = profile;
                    BluetoothLog.v(String.format("mBleGattProfile:\n%s", mBleGattProfile));

                    if(mIBleLockerListener!=null){
                        mConnected = true;
                        mIBleLockerListener.onConnected(code, "连接成功，开始心跳检测......");
                        mHandler.postDelayed(Heartbeat, mHeartBeatInterval);//每n秒执行一次runnable.
                    }
                }
            }
        });
    }

    public void disconnect(){
        ClientManager.getClient().disconnect(mMac);
        ClientManager.getClient().unregisterConnectStatusListener(mMac, mConnectStatusListener);
        mHandler.removeCallbacks(Heartbeat);

        if(mIBleLockerListener!=null){
            mIBleLockerListener.onDisconnected(REQUEST_SUCCESS, "设备已断开连接。");
        }
    }

    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            BluetoothLog.v(String.format("onConnectStatusChanged %d in %s",
                    status, Thread.currentThread().getName()));

            mConnected = (status == STATUS_CONNECTED);
            connectIfNeeded();
        }
    };

    private void connectIfNeeded() {
        if (!mConnected) {
            connect();
        }
    }

    public void lock(){

    }

    public void unlock(){

    }

    public void changePassword(String newPass){

    }

    public void sendDataByHex(String content){

    }

    public void sendDataByAscii(String content){

    }


    public interface IBleLockerListener {
        void onPasswordChanged(int code, String rtvMsg);

        void onClosed(int code, String rtvMsg);

        void onStoped(int code, String rtvMsg);

        void onLocked(int code, String rtvMsg);

        void onUnlock(int code, String rtvMsg);

        void onBleResponed(int code, String rtvMsg);

        void onConnected(int code, String rtvMsg);

        void onDisconnected(int code, String rtvMsg);
    }
}
