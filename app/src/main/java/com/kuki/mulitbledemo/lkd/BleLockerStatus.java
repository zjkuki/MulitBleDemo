package com.kuki.mulitbledemo.lkd;

public enum BleLockerStatus {

    ERROR_PASS(0, "密码错误\n"),
    ERROR_COMM(1, "密码正确，命令错误\n"),
    SET_ERROR(2, "密码正确，命令正确，操作码不正确\n"),
    CHA_OK(3, "更改密码成功\n"),
    SET_OPEN(4,"控制开\n"),
    SET_CLOSE(5,"控制关\n"),
    SET_LOCK(6,"控制锁\n"),
    SET_STOP(7,"控制停\n"),
    DISCONNECTED(7,"已断开\n"),
    CONNECTED(8,"已连接\n"),
    NOTIFY_SUCCESS(9,"消息通知\n"),
    WRITE_SUCCESS(10,"发送成功\n"),
    WRITE_FAIL(11,"发送失败\n"),
    HEARTBEAT_SUCCESS(12,"心跳包\n"),
    READ_RESPONSE_SUCCESS(13,"读取回复成功\n"),
    READ_RESPONSE_FAIL(14,"读取回复失败\n"),
    DEVICE_REDAY(15,"已准备\n"),
    DEVICE_RSSI_GETTING(16,"获取Rssi\n"),
    LOCKED(17,"已锁\n"),
    UNLOCKED(18,"无锁\n"),
    REDAY(19,"设备已就绪\n"),
    RESETTED(20,"设备已重置为出厂状态\n");


    private int mStatusId;
    private int mStatusResId;
    private String mStatusMsg;

    BleLockerStatus(int id, String message) {
        mStatusId = id;
        //mStatusResId = resId;
        mStatusMsg = message;
    }

    public static BleLockerStatus getStatus(int id) {
        for ( BleLockerStatus streamType : BleLockerStatus.values() ) {
            if ( streamType.getSatusId() == id ) {
                return streamType;
            }
        }
        return null;
    }

    public int getSatusId() {
        return mStatusId;
    }

    public int getStatusResId() {
        return mStatusResId;
    }

    public String getmStatusMsg() {
        return mStatusMsg;
    }
}
