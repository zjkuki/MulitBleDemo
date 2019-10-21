package com.kuki.mulitbledemo.lkd;


import android.os.Handler;

import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleMtuResponse;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.kuki.mulitbledemo.ClientManager;
import com.kuki.mulitbledemo.CommonUtils;
import com.kuki.mulitbledemo.StringUtils;

import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_FAILED;
import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static java.security.CryptoPrimitive.MAC;

public class BleLocker {
    private IBleLockerListener mIBleLockerListener;

    public void setBleLockerCallBack(IBleLockerListener iBleLockerListener){
        this.mIBleLockerListener = iBleLockerListener;
    }

    private boolean mNoRssi = false;
    public void setmNoRssi(boolean noRssi) {
        this.mNoRssi = noRssi;
    }


    private boolean mConnected = false;
    public boolean getmConnected() {
        return mConnected;
    }

    private boolean mIsResetted = false;
    public boolean getIsResetted() {
        return mIsResetted;
    }

    private boolean mIsLocked = false;
    public boolean getIsLocked() {
        return mIsLocked;
    }

    private boolean mIsReday = false;
    public boolean getIsReday() {
        return mIsReday;
    }

    private Bluetooth mBluetooth;
    public Bluetooth getmBluetooth() { return mBluetooth; }
    public void setmBluetooth(Bluetooth bluetooth) {
        this.mBluetooth = bluetooth;
    }

    private String mBleName;
    public String getmBleName() {
        return mBleName;
    }
    public void setmBleName(String mBleName) {
        this.mBleName = mBleName;
    }

    private String mMac;
    public String getmMac() {
        return mMac;
    }
    public void setmMac(String mMac) {
        this.mMac = mMac;
    }

    private UUID mService;
    public String getmService() {
        return mService.toString();
    }
    public void setmService(String mService) {
        this.mService = UUID.fromString(mService);
    }

    private UUID mNotifitesCharacter;
    public String getmNotifitesCharacter() {
        return mNotifitesCharacter.toString();
    }
    public void setmNotifitesCharacter(String mNotifitesCharacter) {
        this.mNotifitesCharacter = UUID.fromString(mNotifitesCharacter);
    }

    private UUID mWriteCharacter;
    public String getmWriteCharacter() {
        return mWriteCharacter.toString();
    }
    public void setmWriteCharacter(String mWriteCharacter) {
        this.mWriteCharacter = UUID.fromString(mWriteCharacter);
    }

    public BleGattProfile getmBleGattProfile() {
        return mBleGattProfile;
    }

    public void setmBleGattProfile(BleGattProfile mBleGattProfile) {
        this.mBleGattProfile = mBleGattProfile;
    }
    private String mPassword;

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    private BleLockerStatus mLastStatus;
    public BleLockerStatus getmLastStatus() {
        return mLastStatus;
    }
    /*public void setmCurrStatus(BleLockerStatus mCurrStatus) {
        this.mCurrStatus = mCurrStatus;
    }*/

    private int mHeartBeatInterval = 2000;
    private int mTimeOut;

    private boolean mAutoConnect = false;

    private BleGattProfile mBleGattProfile;

    private Handler mHandler = new Handler();


    private String mBleNotifyValue = "";

    public BleLocker(){

    }

    public BleLocker(Bluetooth bleMode, Boolean isAutoConnect, int heartBeatInterval, IBleLockerListener callBack){
        this.mBluetooth = bleMode;
        this.mMac = bleMode.mac;
        this.mHeartBeatInterval = heartBeatInterval;
        this.mIBleLockerListener = callBack;
        this.mService = UUID.fromString(bleMode.serviceUuid);
        this.mNotifitesCharacter = UUID.fromString(bleMode.notifyUuid);
        this.mWriteCharacter = UUID.fromString(bleMode.writeUuid);
        this.mPassword = bleMode.password;
        this.mAutoConnect= isAutoConnect;
        if(mAutoConnect){
            this.connect();
        }
    }

    public BleLocker(String BleMacAddr, Boolean isAutoConnect, String ServiceUUID, String NotifitesCharacterUUID, String WriteCharacterUUID, String Password, int heartBeatInterval, IBleLockerListener callBack) {
        this.mMac = BleMacAddr;
        this.mHeartBeatInterval = heartBeatInterval;
        this.mIBleLockerListener = callBack;
        this.mService = UUID.fromString(ServiceUUID);
        this.mNotifitesCharacter = UUID.fromString(NotifitesCharacterUUID);
        this.mWriteCharacter = UUID.fromString(WriteCharacterUUID);
        this.mPassword = Password;
        this.mAutoConnect= isAutoConnect;
        if(mAutoConnect){
            this.connect();
        }

        mBluetooth = new Bluetooth();
        mBluetooth.name = BleMacAddr;
        mBluetooth.mac=BleMacAddr;
        mBluetooth.password = Password;
        mBluetooth.serviceUuid = ServiceUUID;
        mBluetooth.notifyUuid = NotifitesCharacterUUID;
        mBluetooth.writeUuid = WriteCharacterUUID;


    }

    Runnable Heartbeat=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub

            sendDataByString(getmPassword());
            BluetoothLog.v(String.format("发送密码"));

            //要做的事情
            if(mIBleLockerListener!=null){
                mConnected = true;
                mIBleLockerListener.onHeartBeatting(mBluetooth, BleLockerStatus.HEARTBEAT_SUCCESS);
            }


            mHandler.postDelayed(Heartbeat, mHeartBeatInterval);//每n秒执行一次runnable.
        }
    };

    Runnable GetBluetoothRssi=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //要做的事情
            if(mIBleLockerListener!=null && mIsReday) {
                ClientManager.getClient().readRssi(mMac, new BleReadRssiResponse() {
                    @Override
                    public void onResponse(int i, Integer integer) {
                        BluetoothLog.v(String.format("获取rssi="+integer));
                        mIBleLockerListener.onGetRssi(mBluetooth, integer, BleLockerStatus.DEVICE_RSSI_GETTING);
                    }
                });
            }
            if(!mNoRssi) {
                mHandler.postDelayed(GetBluetoothRssi, 800);//每n秒执行一次runnable.
            }
        }
    };
    public void connect(){
        mIsReday=false;
        BluetoothLog.v(String.format("onBluetooth Connecting... %s", mMac));

        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();

        ClientManager.getClient().registerConnectStatusListener(mMac, mConnectStatusListener);

        ClientManager.getClient().connect(mMac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile profile) {
                BluetoothLog.v(String.format("profile:\n%s", profile));

                if (code == REQUEST_SUCCESS) {
                    //mAdapter.setGattProfile(profile);
                    mBleGattProfile = profile;
                    BluetoothLog.v(String.format("mBleGattProfile:\n%s", mBleGattProfile));
                    ClientManager.getClient().notify(mMac, mService, mNotifitesCharacter, mNotifyRsp);

                    if(mIBleLockerListener!=null){
                        mConnected = true;
                        mIBleLockerListener.onConnected(mBluetooth, BleLockerStatus.CONNECTED);

                        sendDataByString(getmPassword());
                        //mHandler.postDelayed(Heartbeat,0);

                        if(!mNoRssi) {
                            mHandler.postDelayed(GetBluetoothRssi, 800);//每n秒执行一次runnable.
                        }
                    }
                }
            }
        });
    }


    public void disconnect(){
        mConnected = false;
        ClientManager.getClient().disconnect(mMac);
        ClientManager.getClient().unregisterConnectStatusListener(mMac, mConnectStatusListener);
        mHandler.removeCallbacks(Heartbeat);

        if(mIBleLockerListener!=null){
            mIBleLockerListener.onDisconnected(mBluetooth, BleLockerStatus.DISCONNECTED);
        }
    }

    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            BluetoothLog.v(String.format("onConnectStatusChanged %d in %s",
                    status, Thread.currentThread().getName()));

            if(status == STATUS_CONNECTED){
                mConnected=true;
            }else{
                mConnected=false;
            }
            connectIfNeeded();
        }
    };

    private void connectIfNeeded() {
        if (!mConnected && mAutoConnect) {
            mHandler.removeCallbacks(Heartbeat);
            connect();
        }
    }

    public void sta(){
        sendDataByString("STA");
        BluetoothLog.v(String.format("查询蓝牙设备状态：STA"));
    }

    public void lock(){
        sendDataByString(getmPassword()+"SETL");
        BluetoothLog.v(String.format("发送锁指令：SETL"));
    }

    public void open(){
        sendDataByString(getmPassword()+"SETO");
        BluetoothLog.v(String.format("发送开指令：SETO"));
    }

    public void close(){
        sendDataByString(getmPassword()+"SETC");
        BluetoothLog.v(String.format("发送关指令：SETC"));
    }

    public void stop(){
        sendDataByString(getmPassword()+"SETS");
        BluetoothLog.v(String.format("发送停指令：SETS"));
    }

    public void changePassword(String newPass){
        sendDataByString(getmPassword()+"CHA"+newPass);
        BluetoothLog.v(String.format("修改密码指令：CHA"));

    }

    public void sendDataByString(String content){
        if(ClientManager.getClient().getConnectStatus(mMac)== Constants.STATUS_DEVICE_CONNECTED) {
            /*ClientManager.getClient().write(mMac, mService, mWriteCharacter,
                    ByteUtils.stringToBytes(content), mWriteRsp);*/
            ClientManager.getClient().write(mMac, mService, mWriteCharacter,
                    content.getBytes(), mWriteRsp);
        }else{
            BluetoothLog.v(String.format("设备未连接"));
        }
    }

    private final BleReadResponse mReadRsp = new BleReadResponse() {
        @Override
        public void onResponse(int code, byte[] data) {
            String rtv ="";
            if (code == REQUEST_SUCCESS) {
                BluetoothLog.v(String.format("read: %s", ByteUtils.byteToString(data)));
                rtv=ByteUtils.byteToString(data);

                //CommonUtils.toast("success");
                if(mIBleLockerListener!=null){ mIBleLockerListener.onBleReadResponse(mBluetooth, data, BleLockerStatus.READ_RESPONSE_SUCCESS); }
            } else {
                //CommonUtils.toast("failed");
                rtv="bluetooth read failed";
                BluetoothLog.v(String.format("read: "));
                if(mIBleLockerListener!=null){ mIBleLockerListener.onBleReadResponse(mBluetooth, data, BleLockerStatus.READ_RESPONSE_FAIL); }
            }

        }
    };

    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            String rtv ="";
            if (code == REQUEST_SUCCESS) {
                BluetoothLog.v(String.format("write success"));
                //CommonUtils.toast("success");
                rtv="发送成功";
                if(mIBleLockerListener!=null){ mIBleLockerListener.onBleWriteResponse(mBluetooth, BleLockerStatus.WRITE_SUCCESS); }
            } else {
                rtv="发送失败";
                BluetoothLog.v(String.format("write failed"));
                if(mIBleLockerListener!=null){ mIBleLockerListener.onBleWriteResponse(mBluetooth, BleLockerStatus.WRITE_FAIL); }
                //CommonUtils.toast("failed");
            }
        }
    };

    private final BleNotifyResponse mNotifyRsp = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            mBleNotifyValue = "";
            if (service.equals(mService) && character.equals(mNotifitesCharacter)) {
                //mBleNotifyValue = String.format("%s", ByteUtils.byteToString(value));
                mBleNotifyValue = String.format("%s", StringUtils.asciiToString(value));
                BluetoothLog.v(String.format("%s", mBleNotifyValue));
            }

            String rtvMsg="";

            if(mBleNotifyValue.contains("STA")){
                //检查锁标志 0-无锁，1-有锁
                String stat = mBleNotifyValue.substring(3,4);
                if(stat.contains("1")) {
                    if (mIBleLockerListener != null) {
                        mLastStatus = BleLockerStatus.LOCKED;
                        mIBleLockerListener.onLock(mBluetooth, BleLockerStatus.LOCKED);
                        mIsLocked = true;
                    }
                }else{
                    if (mIBleLockerListener != null) {
                        mLastStatus = BleLockerStatus.UNLOCKED;
                        mIBleLockerListener.onLock(mBluetooth, BleLockerStatus.UNLOCKED);
                        mIsLocked = false;
                    }
                }
                //检查重置标志 0-正常  1-已重置
                stat = mBleNotifyValue.substring(4,5);
                if(stat.contains("1")) {
                    if (mIBleLockerListener != null) {
                        mLastStatus = BleLockerStatus.RESETTED;
                        mIBleLockerListener.onResetted(mBluetooth, BleLockerStatus.RESETTED);
                        mIsResetted = true;
                    }else{
                        mIsResetted = false;
                    }
                }
            }

            switch (mBleNotifyValue) {
                case "ERROR PASS":
                    rtvMsg = "密码错误";
                    mLastStatus = BleLockerStatus.ERROR_PASS;
                    mIsReday = false;
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onPasswdError(mBluetooth, BleLockerStatus.ERROR_PASS);}
                    break;
                case "ERROR COMM":
                    mIsReday = true;
                    rtvMsg = "密码正确，命令错误";
                    mLastStatus = BleLockerStatus.ERROR_COMM;
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onReday(mBluetooth, BleLockerStatus.ERROR_COMM);}
                    break;
                case "SET ERROR":
                    mIsReday = true;
                    mLastStatus = BleLockerStatus.SET_ERROR;
                    rtvMsg = "密码正确，命令正确，操作码不正确";
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onReday(mBluetooth, BleLockerStatus.SET_ERROR);}
                    break;
                case "CHA OK":
                    mLastStatus = BleLockerStatus.CHA_OK;
                    rtvMsg = "更改密码成功";
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onPasswordChanged(mBluetooth, BleLockerStatus.CHA_OK);}
                    break;
                case "SET OPEN":
                    mLastStatus = BleLockerStatus.SET_OPEN;
                    rtvMsg = "控制开";
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onOpened(mBluetooth, BleLockerStatus.SET_OPEN);}
                    break;
                case "SET CLOSE":
                    mLastStatus = BleLockerStatus.SET_CLOSE;
                    rtvMsg = "控制关";
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onClosed(mBluetooth, BleLockerStatus.SET_CLOSE);}
                    break;
                case "SET LOCK":
                    rtvMsg = "控制锁";
                    mLastStatus = BleLockerStatus.SET_LOCK;
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onLock(mBluetooth, BleLockerStatus.SET_LOCK);}
                    break;
                case "SET STOP":
                    rtvMsg = "控制停";
                    mLastStatus = BleLockerStatus.SET_STOP;
                    if(mIBleLockerListener!=null){ mIBleLockerListener.onStoped(mBluetooth, BleLockerStatus.SET_STOP);}
                    break;
            }

            if(mIBleLockerListener!=null){ mIBleLockerListener.onBleNotifyResponse(mBluetooth,  mBleNotifyValue, BleLockerStatus.NOTIFY_SUCCESS); }
        }

        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                CommonUtils.toast("Ble Notify success");
                BluetoothLog.v("Ble Notify success");
            } else {
                CommonUtils.toast("Ble Notify failed");
                BluetoothLog.v("Ble Notify failed");
            }

            if(mIBleLockerListener!=null){ mIBleLockerListener.onBleNotifyResponse(mBluetooth, "设备服务注册成功", BleLockerStatus.REDAY); }
        }
    };

    private final BleUnnotifyResponse mUnnotifyRsp = new BleUnnotifyResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                BluetoothLog.v("Ble UnnotifyRespionse success");
                CommonUtils.toast("UnnotifyRespionse success");
            } else {
                BluetoothLog.v("Ble UnnotifyRespionse failed");
                CommonUtils.toast("UnnofityResponse failed");
            }
        }
    };

    private final BleMtuResponse mMtuResponse = new BleMtuResponse() {
        @Override
        public void onResponse(int code, Integer data) {
            if (code == REQUEST_SUCCESS) {
                CommonUtils.toast("request mtu success,mtu = " + data);
            } else {
                CommonUtils.toast("request mtu failed");
            }
        }
    };


    public interface IBleLockerListener {
        void onPasswordChanged(Bluetooth bluetooth, BleLockerStatus status);

        void onClosed(Bluetooth bluetooth, BleLockerStatus status);

        void onStoped(Bluetooth bluetooth, BleLockerStatus status);

        void onLock(Bluetooth bluetooth, BleLockerStatus status);

        void onOpened(Bluetooth bluetooth, BleLockerStatus status);

        void onBleReadResponse(Bluetooth bluetooth, byte[] data, BleLockerStatus status);

        void onBleWriteResponse(Bluetooth bluetooth, BleLockerStatus status);

        void onBleNotifyResponse(Bluetooth bluetooth, String NotifyValue, BleLockerStatus status);

        void onConnected(Bluetooth bluetooth, BleLockerStatus status);

        void onDisconnected(Bluetooth bluetooth,BleLockerStatus status);

        void onHeartBeatting(Bluetooth bluetooth, BleLockerStatus status);

        void onReday(Bluetooth bluetooth, BleLockerStatus status);

        void onGetRssi(Bluetooth bluetooth, int Rssi, BleLockerStatus status);

        void onPasswdError(Bluetooth bluetooth, BleLockerStatus status);

        void onResetted(Bluetooth bluetooth, BleLockerStatus status);
    }
}
