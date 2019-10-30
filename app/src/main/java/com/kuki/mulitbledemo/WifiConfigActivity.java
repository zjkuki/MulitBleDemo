package com.kuki.mulitbledemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import io.fogcloud.sdk.easylink.api.EasyLink;
import io.fogcloud.sdk.easylink.helper.EasyLinkCallBack;
import io.fogcloud.sdk.easylink.helper.EasyLinkParams;
import io.fogcloud.sdk.mdns.api.MDNS;
import io.fogcloud.sdk.mdns.helper.SearchDeviceCallBack;

//import io.fogcloud.sdk.easylink.api.EasylinkP2P;

public class WifiConfigActivity extends AppCompatActivity {

    private String TAG = "---main---";
    private Context mContext;// 上下文
    private EditText log_view;
    private int countno;
    private MDNS mdns;
    private EditText psw;
    private EditText ssid;
    private EasyLink el;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_config);
        //实时更新ssid
        listenwifichange();

        mContext = this;

        el = new EasyLink(WifiConfigActivity.this);
        //final EasylinkP2P elp2p = new EasylinkP2P(mContext);


        final TextView easylinktest = (TextView) findViewById(R.id.easylinktest);
//        TextView easylinkstop = (TextView) findViewById(R.id.easylinkstop);

        psw = (EditText) findViewById(R.id.psw);
        ssid = (EditText) findViewById(R.id.ssid);
        /*if(ssid!=null) {
            ssid.setText(el.getSSID());
        }*/

        log_view = (EditText) findViewById(R.id.log);
        if(log_view != null) {
            log_view.setText("");
        }

        mdns = new MDNS(this);

        if(easylinktest != null & ssid != null & psw != null) {
            easylinktest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (easylinktest.getText().toString().equalsIgnoreCase("发送配网")) {
                        easylinktest.setText("关闭配网");
                        easylinktest.setBackgroundColor(Color.rgb(255, 0, 0));
                        EasyLinkParams elp = new EasyLinkParams();
                        elp.ssid = ssid.getText().toString().trim();
                        elp.password = psw.getText().toString().trim();
                        elp.sleeptime = 50;
                        elp.runSecond = 60000;
                        Toast.makeText(mContext, "open easylink", Toast.LENGTH_SHORT).show();

                        el.startEasyLink(elp, new EasyLinkCallBack() {
                            @Override
                            public void onSuccess(int code, String message) {
//                                Log.d(TAG,">>>>>>>>>>");
                                Log.d(TAG, message);
                                send2handler(1, message);
                                finish();
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Log.d(TAG, message);
                                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        easylinktest.setText("发送配网");
                        easylinktest.setBackgroundColor(Color.rgb(63, 81, 181));
                        Toast.makeText(mContext, "stop easylink", Toast.LENGTH_SHORT).show();
                        //elp2p.stopEasyLink(new EasyLinkCallBack() {
                        mdns.stopSearchDevices(new SearchDeviceCallBack() {
                            public void onSuccess(int code, String message) {
                                Log.d("---mdns---", message);
                            };
                            @Override
                            public void onFailure(int code, String message) {
                                Log.d("---mdns---", message);
                            }
                        });

                        el.stopEasyLink(new EasyLinkCallBack() {
                            @Override
                            public void onSuccess(int code, String message) {

                                Log.d(TAG, message);
                                send2handler(2, message);
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Log.d(TAG, message);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastReceiver);
    }

    private void send2handler(int code, String message) {
        Message msg = new Message();
        msg.what = code;
        msg.obj = message;
        LHandler.sendMessage(msg);
    }

    Handler LHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                log_view.setText(msg.obj.toString().trim() + "\r\n");
            }
            if (msg.what == 2) {
                log_view.setText("");
            }
        }
    };

    private void listenwifichange() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    Log.d(TAG, "---heiheihei---");
                    ssid.setText(el.getSSID());
                }
            }
        }
    };
}

