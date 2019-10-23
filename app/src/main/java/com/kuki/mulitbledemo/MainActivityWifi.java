package com.kuki.mulitbledemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.kuki.mulitbledemo.view.PullRefreshListView;
import com.kuki.mulitbledemo.view.PullToRefreshFrameLayout;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivityWifi extends AppCompatActivity implements View.OnClickListener {


    public static final int PERMISSION_LOCATION = 100;
    private static final int REQUEST_CODE_SCAN = 111;
    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private FloatingActionButton fab;
    private TextView tv_result;
    private Button btnLocker1Connect;
    private Button btnLocker1Disconnect;
    private Button btnLocker1ChangePassword;
    private Button btnLocker1Unlock;
    private Button btnLocker1Open;
    private Button btnLocker1Close;
    private Button btnLocker1Stop;
    private Button btnSearchDevice;
    private Button btnDeviceStatus;
    private Button btnDeviceBle;

    private Toolbar toolbar;
    private PullToRefreshFrameLayout mRefreshLayout;
    private PullRefreshListView mListView;
    private DeviceListAdapter mAdapter;

    private List<SearchResult> mDevices;

    private boolean isScanning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wifi);

        mDevices = new ArrayList<SearchResult>();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("龙科多wifi测试");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        btnLocker1Connect = (Button) findViewById(R.id.btn_locker1_connect);
        btnLocker1Connect.setOnClickListener(this);

        btnLocker1Disconnect = (Button) findViewById(R.id.btn_locker1_disconnect);
        btnLocker1Disconnect.setOnClickListener(this);

        btnLocker1ChangePassword = (Button) findViewById(R.id.btn_locker1_cha);
        btnLocker1ChangePassword.setOnClickListener(this);

        btnLocker1Unlock = (Button) findViewById(R.id.btn_locker1_unlock);
        btnLocker1Unlock.setOnClickListener(this);

        btnLocker1Open = (Button) findViewById(R.id.btn_locker1_open);
        btnLocker1Open.setOnClickListener(this);

        btnLocker1Close = (Button) findViewById(R.id.btn_locker1_close);
        btnLocker1Close.setOnClickListener(this);

        btnLocker1Stop = (Button) findViewById(R.id.btn_locker1_stop);
        btnLocker1Stop.setOnClickListener(this);

        btnSearchDevice = (Button) findViewById(R.id.btn_search_device);
        btnSearchDevice.setOnClickListener(this);

        btnDeviceStatus = (Button) findViewById(R.id.btn_device_status);
        btnDeviceStatus.setOnClickListener(this);

        btnDeviceBle = (Button) findViewById(R.id.btn_device_ble);
        btnDeviceBle.setOnClickListener(this);

        tv_result = (TextView) findViewById(R.id.result);
        tv_result.setMovementMethod(ScrollingMovementMethod.getInstance());

        mRefreshLayout = (PullToRefreshFrameLayout) findViewById(R.id.pulllayout);

        mListView = mRefreshLayout.getPullToRefreshListView();
        mAdapter = new DeviceListAdapter(this);
        mListView.setAdapter(mAdapter);

        mListView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                
            }

        });
        BluetoothLog.v(String.format("%s onCreate", this.getClass().getSimpleName()));

        requestPermission();

        //searchDevice();

        ClientManager.getClient().registerBluetoothStateListener(new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                BluetoothLog.v(String.format("onBluetoothStateChanged %b", openOrClosed));
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_LOCATION);
        }
    }

    private boolean isLocationOpen(final Context context){
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //gps定位
        boolean isGpsProvider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        //网络定位
        boolean isNetWorkProvider = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGpsProvider|| isNetWorkProvider;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= 23 && !isLocationOpen(MainActivityWifi.this)) {
                        Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(enableLocate);
                    }
                } else {
                    Toast.makeText(this,"读取位置权限被禁用", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.fab:
                AndPermission.with(this)
                        .permission(Permission.CAMERA, Permission.READ_EXTERNAL_STORAGE)
                        .onGranted(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Intent intent = new Intent(MainActivityWifi.this, CaptureActivity.class);
                                /*ZxingConfig是配置类
                                 *可以设置是否显示底部布局，闪光灯，相册，
                                 * 是否播放提示音  震动
                                 * 设置扫描框颜色等
                                 * 也可以不传这个参数
                                 * */
                                ZxingConfig config = new ZxingConfig();
                                // config.setPlayBeep(false);//是否播放扫描声音 默认为true
                                //  config.setShake(false);//是否震动  默认为true
                                // config.setDecodeBarCode(false);//是否扫描条形码 默认为true
//                                config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
//                                config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
//                                config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
                                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                                startActivityForResult(intent, REQUEST_CODE_SCAN);
                            }
                        })
                        .onDenied(new Action() {
                            @Override
                            public void onAction(List<String> permissions) {
                                Uri packageURI = Uri.parse("package:" + getPackageName());
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);

                                Toast.makeText(MainActivityWifi.this, "没有权限无法扫描呦", Toast.LENGTH_LONG).show();
                            }
                        }).start();
                break;
            case R.id.btn_search_device:
                    if(!isScanning) {

                    }else{

                    }

                break;
            case R.id.btn_locker1_connect:

                break;
            case R.id.btn_locker1_open:

                break;
            case R.id.btn_locker1_close:

                break;
            case R.id.btn_locker1_unlock:

                break;
            case R.id.btn_locker1_stop:

                break;
            case R.id.btn_locker1_disconnect:

                break;
            case R.id.btn_locker1_cha:

                break;
            case R.id.btn_device_status:

                break;
            case R.id.btn_device_ble:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        BluetoothLog.v(String.format("%s onResume", this.getClass().getSimpleName()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        BluetoothLog.v(String.format("%s onStart", this.getClass().getSimpleName()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        BluetoothLog.v(String.format("%s onPause", this.getClass().getSimpleName()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        BluetoothLog.v(String.format("%s onStop", this.getClass().getSimpleName()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothLog.v(String.format("%s onDestroy", this.getClass().getSimpleName()));
    }


    private String getTime(){
        SimpleDateFormat sdf= new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    private  void AppendText(final String str){
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

