package com.example.breeziness.beacon2;


import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements BeaconManager.BeaconListener, BeaconManager.BeaconsListener {

    private static final String TAG = "MainActivity";

    private TextView tv_Uuid;
    private TextView tv_Major;
    private TextView tv_Minor;
    private TextView tv_Rssi;
    private TextView tv_Distance;
    private TextView tv_TxPower;

    private TextView tv2_Uuid;
    private TextView tv2_Major;
    private TextView tv2_Minor;
    private TextView tv2_Rssi;
    private TextView tv2_Distance;
    private TextView tv2_TxPower;

    private TextView tv3_Uuid;
    private TextView tv3_Major;
    private TextView tv3_Minor;
    private TextView tv3_Rssi;
    private TextView tv3_Distance;
    private TextView tv3_TxPower;

    BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_Uuid = findViewById(R.id.tv_Uuid);
        tv_Major = findViewById(R.id.tv_Major);
        tv_Minor = findViewById(R.id.tv_Minor);
        tv_Rssi = findViewById(R.id.tv_Rssi);
        tv_Distance = findViewById(R.id.tv_Distance);
        tv_TxPower = findViewById(R.id.tv_TxPower);

        tv2_Uuid = findViewById(R.id.tv2_Uuid);
        tv2_Major = findViewById(R.id.tv2_Major);
        tv2_Minor = findViewById(R.id.tv2_Minor);
        tv2_Rssi = findViewById(R.id.tv2_Rssi);
        tv2_Distance = findViewById(R.id.tv2_Distance);
        tv2_TxPower = findViewById(R.id.tv2_TxPower);

        tv3_Uuid = findViewById(R.id.tv3_Uuid);
        tv3_Major = findViewById(R.id.tv3_Major);
        tv3_Minor = findViewById(R.id.tv3_Minor);
        tv3_Rssi = findViewById(R.id.tv3_Rssi);
        tv3_Distance = findViewById(R.id.tv3_Distance);
        tv3_TxPower = findViewById(R.id.tv3_TxPower);

        tv_Uuid.setText("0.0");
        tv_Major.setText("0.0");
        tv_Minor.setText("0.0");
        tv_Rssi.setText("0.0");
        tv_Distance.setText("0.0");
        tv_TxPower.setText("0.0");



        /*动态权限申请，6.0后必须进行权限申请*/
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        beaconManager = new BeaconManager(this);//获取BeaconManager实例
        beaconManager.setBeaconListener(this);//设置接口，当检测到beacon就回调到接口方法中
        beaconManager.setBeaconsListener(this);
        beaconManager.startScanBeacon();//测试写法，调通后可以用封装的方法开启扫描，可以方便绑定相应的控件
        //scanAction();
    }

    /**
     * 权限请求结果查询方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean grantedLocation = true;
        if (requestCode == 1) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    grantedLocation = false;
                }
            }
        }
        if (!grantedLocation) {
            Toast.makeText(this, "Permission error !!!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 启动扫描
     *
     * @param v
     */
    public void scanAction(View v) {
        beaconManager.startScanBeacon();
    }

    /**
     * //Beacon对象通过接口回调测量的数据
     *
     * @param beacon
     */
    @Override
    public void onScanBeacon(Beacon beacon) {
        //Log.e(TAG, "  beacon设备：" + beacon.toString());
        switch (beacon.getMinor()) {
            case "7":
                tv_Uuid.setText(beacon.getUuid());
                tv_Major.setText(beacon.getMajor());
                tv_Minor.setText(beacon.getMinor());
                tv_Rssi.setText(beacon.getRssi());
                tv_Distance.setText(beacon.getDistance());
                tv_TxPower.setText(beacon.getTxPower());
                break;
            case "8":
                tv2_Uuid.setText(beacon.getUuid());
                tv2_Major.setText(beacon.getMajor());
                tv2_Minor.setText(beacon.getMinor());
                tv2_Rssi.setText(beacon.getRssi());
                tv2_Distance.setText(beacon.getDistance());
                tv2_TxPower.setText(beacon.getTxPower());
                break;
            case "9":
                tv3_Uuid.setText(beacon.getUuid());
                tv3_Major.setText(beacon.getMajor());
                tv3_Minor.setText(beacon.getMinor());
                tv3_Rssi.setText(beacon.getRssi());
                tv3_Distance.setText(beacon.getDistance());
                tv3_TxPower.setText(beacon.getTxPower());
                break;
                default:
        }
    }


    @Override
    public void onScanBeacon(ArrayList<Beacon> beacons) {
        //Log.e(TAG, "  beacon设备：" + beacon.toString());
        for (int i = 0; i < beacons.size(); i++) {
            tv_Uuid.setText(beacons.get(i).getUuid());
            tv_Major.setText(beacons.get(i).getMajor());
            tv_Minor.setText(beacons.get(i).getMinor());
            tv_Rssi.setText(beacons.get(i).getRssi());
            tv_Distance.setText(beacons.get(i).getDistance());
            tv_TxPower.setText(beacons.get(i).getTxPower());
        }

    }
}
