package com.kuki.mulitbledemo;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.kuki.mulitbledemo.lkd.BleLocker;
import com.kuki.mulitbledemo.lkd.BleLockerStatus;
import com.kuki.mulitbledemo.lkd.Bluetooth;


import java.text.SimpleDateFormat;
import java.util.Date;

public class BleLockerCallBack implements BleLocker.IBleLockerListener {
    private boolean mIsTaost = false;
    private Context context;
    private TextView tvresult;

    public BleLockerCallBack(Context context, TextView tvResurl){

        this.context = context;
        this.tvresult = tvResurl;
    }

    @Override
    public void onPasswordChanged(Bluetooth bluetooth, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onPasswordChanged：" + status.getmStatusMsg());
    }

    @Override
    public void onClosed(Bluetooth bluetooth, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onClose：" + status.getmStatusMsg());
    }

    @Override
    public void onStoped(Bluetooth bluetooth, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onStop：" + status.getmStatusMsg());
    }

    @Override
    public void onLock(Bluetooth bluetooth, BleLockerStatus status) {

        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onLock：" + status.getmStatusMsg());

    }

    @Override
    public void onOpened(Bluetooth bluetooth, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "..\n   onOpen：" + status.getmStatusMsg());

    }

    @Override
    public void onBleReadResponse(Bluetooth bluetooth,  byte[] data, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onBleReadResponse：" + String.format("read: %s", ByteUtils.byteToString(data))+"\n"
                +"\n Status:"+ status.getmStatusMsg());
    }

    @Override
    public void onBleWriteResponse(Bluetooth bluetooth, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onBleWriteResponse：" + status.getmStatusMsg()
        );
    }

    @Override
    public void onBleNotifyResponse(Bluetooth bluetooth, String NotifyValue, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onNotifyResponse：" +  NotifyValue
                +"\n  Status: " + status.getmStatusMsg());
    }

    @Override
    public void onConnected(Bluetooth bluetooth, BleLockerStatus status) {
            Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onConnected：" + status.getmStatusMsg());
    }

    @Override
    public void onDisconnected(Bluetooth bluetooth, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onDisconnected：" + status.getmStatusMsg());
    }

    @Override
    public void onHeartBeatting(Bluetooth bluetooth, BleLockerStatus status) {

    }

    @Override
    public void onReday(Bluetooth bluetooth, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onReday feed back：" + status.getmStatusMsg());

    }

    @Override
    public void onGetRssi(Bluetooth bluetooth, int Rssi, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onGetRssi feed back：" + status.getmStatusMsg());
    }
    @Override
    public void onPasswdError(Bluetooth bluetooth, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onPasswdError：" + status.getmStatusMsg());
    }
    @Override
    public void onResetted(Bluetooth bluetooth, BleLockerStatus status) {
        Util.AppendText(tvresult, Util.getPrintTime() + " 设备：" + bluetooth.name + "...\n   onResetted：" + status.getmStatusMsg());
    }
    private void AppendText(String text) {
        if(mIsTaost){
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
    }
    private String getTime() {
        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    private  void AppendText(final TextView tv_result, final String str){
        tv_result.post(new Runnable() {
            @Override
            public void run() {
                tv_result.append(str);
                int scrollAmount = tv_result.getLayout().getLineTop(tv_result.getLineCount())
                        - tv_result.getHeight();
                if (scrollAmount > 0)
                    tv_result.scrollTo(0, scrollAmount);
                else
                    tv_result.scrollTo(0, 0);
            }
        });
    }

}
