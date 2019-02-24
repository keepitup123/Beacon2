package com.example.breeziness.beacon2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;


public class BeaconManager {

    private final static String TAG = "BeaconManager";
    private ArrayList<Double> dataList = new ArrayList<>();//数据list

    private ArrayList<Double> dataList_1 = new ArrayList<>();//数据list      第1个beacon的数据
    private ArrayList<Double> dataList_2 = new ArrayList<>();//数据list    第2个beacon的数据
    private ArrayList<Double> dataList_3 = new ArrayList<>();//数据list      第3个beacon的数据

    private ArrayList<Beacon> beacons = new ArrayList<>();

    private BluetoothAdapter bluetoothAdapter;

    private BeaconListener beaconListener;//单次对象返回监听
    private BeaconsListener beaconsListener;//对象集合返回监听


    public void setBeaconListener(BeaconListener beaconListener) {
        this.beaconListener = beaconListener;
    }

    public void setBeaconsListener(BeaconsListener beaconsListener) {
        this.beaconsListener = beaconsListener;
    }


    public BeaconManager(Context context) {
        dataList.clear();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

    }


    public void startScanBeacon() {
        bluetoothAdapter.startLeScan(leScanCallback);


    }

    public void stopScan() {
        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothdeivce, int rssi, byte[] scandata) {
            if (scandata[5] == 0x4C && scandata[6] == 0x00 && scandata[7] == 0x02 && scandata[8] == 0x15) {

                byte[] uuidValue = new byte[16];
                System.arraycopy(scandata, 9, uuidValue, 0, 16);
                String uuid = "";
                String hexStr = BytesToHexString(uuidValue);
                uuid = hexStr.substring(0, 8);
                uuid += "-";
                uuid += hexStr.substring(8, 12);
                uuid += "-";
                uuid += hexStr.substring(12, 16);
                uuid += "-";
                uuid += hexStr.substring(16, 20);
                uuid += "-";
                uuid += hexStr.substring(20, 32);
                int major = buildUint16(scandata[25], scandata[26]);//  UUID major  minor  measuredPower 这四个数据都是不变的
                int minor = buildUint16(scandata[27], scandata[28]);
                int measuredPower = scandata[29];


                /*判断minor 区分每个beancon的数据*/
                if (minor == 7) {
                    dataList_1.add((double) rssi);
                    if (dataList_1.size() == 15) {
                        Collections.sort(dataList_1);//排序
                        Log.e(TAG, "新的datalist_1：" + dataList_1.toString());
                        dataList_1.remove(0);
                        dataList_1.remove(dataList_1.size() - 1);


                        KalmanFliter kalmanFliter = new KalmanFliter(dataList_1);//获取卡尔曼滤波器的实例
                        dataList_1 = kalmanFliter.calc();//计算滤波后的rssi
                        Log.e(TAG, "卡尔曼滤波后的list_1: " + dataList_1.toString());


                        for (int i = 0; i < dataList_1.size(); i++) {
                            double distance = calculDistance(dataList_1.get(i), measuredPower);//计算距离
                            //这是一个iBeacon设备。
                            //注意这里004C的判断，在广播里厂商id这2个字节的数据是颠倒的
                            //这里是解析uuid,major,minor代码，省略
                            Beacon beacon = new Beacon(uuid, major, minor, rssi, distance, measuredPower);

                            // beacons.add(beacon);//把对象加入集合
//                        if (beaconsListener != null) {
//                            //beaconListener.onScanBeacon(beacon);//将数据通过接口方法回调给实现对象
//                            beaconsListener.onScanBeacon(beacons);
//                        }


                            if (beaconsListener != null) {
                                beaconListener.onScanBeacon(beacon);//将数据通过接口方法回调给实现对象
                            }
                        }

                        /*留下一半上次数据，减少突变,增加相关性，过渡平缓*/
                        for (int j = 0; j < dataList_1.size(); j++) {
                            if (j % 2 != 0)
                                dataList_1.remove(j);
                        }
                        Log.e(TAG, "偶数元素保留：" + dataList_1.toString());
                    }
                } else if (minor == 8) {
                    dataList_2.add((double) rssi);//每20次数据作为一个卡尔曼滤波数组

                    if (dataList_2.size() == 15) {
                        Collections.sort(dataList_2);//排序
                        Log.e(TAG, "新的datalist_2：" + dataList_2.toString());
                        dataList_2.remove(0);
                        dataList_2.remove(dataList_2.size() - 1);


                        KalmanFliter kalmanFliter = new KalmanFliter(dataList_2);//获取卡尔曼滤波器的实例
                        dataList_2 = kalmanFliter.calc();//计算滤波后的rssi
                        Log.e(TAG, "卡尔曼滤波后的list_2: " + dataList_2.toString());


                        for (int i = 0; i < dataList_2.size(); i++) {
                            double distance = calculDistance(dataList_2.get(i), measuredPower);//计算距离
                            //这是一个iBeacon设备。
                            //注意这里004C的判断，在广播里厂商id这2个字节的数据是颠倒的
                            //这里是解析uuid,major,minor代码，省略
                            Beacon beacon = new Beacon(uuid, major, minor, rssi, distance, measuredPower);

                            // beacons.add(beacon);//把对象加入集合
//                        if (beaconsListener != null) {
//                            //beaconListener.onScanBeacon(beacon);//将数据通过接口方法回调给实现对象
//                            beaconsListener.onScanBeacon(beacons);
//                        }


                            if (beaconsListener != null) {
                                beaconListener.onScanBeacon(beacon);//将数据通过接口方法回调给实现对象
                            }
                        }

                        /*留下一半上次数据，减少突变*/
                        for (int j = 0; j < dataList_2.size(); j++) {
                            if (j % 2 != 0)
                                dataList_2.remove(j);
                        }
                        Log.e(TAG, "偶数元素保留：" + dataList_2.toString());
                    }
                } else if (minor == 9) {
                    dataList_3.add((double) rssi);//每20次数据作为一个卡尔曼滤波数组

                    if (dataList_3.size() == 15) {
                        Collections.sort(dataList_3);//排序
                        Log.e(TAG, "新的datalist_3：" + dataList_3.toString());
                        dataList_3.remove(0);
                        dataList_3.remove(dataList_3.size() - 1);


                        KalmanFliter kalmanFliter = new KalmanFliter(dataList_3);//获取卡尔曼滤波器的实例
                        dataList_3 = kalmanFliter.calc();//计算滤波后的rssi
                        Log.e(TAG, "卡尔曼滤波后的list_3: " + dataList_3.toString());


                        for (int i = 0; i < dataList_3.size(); i++) {
                            double distance = calculDistance(dataList_3.get(i), measuredPower);//计算距离
                            //这是一个iBeacon设备。
                            //注意这里004C的判断，在广播里厂商id这2个字节的数据是颠倒的
                            //这里是解析uuid,major,minor代码，省略
                            Beacon beacon = new Beacon(uuid, major, minor, rssi, distance, measuredPower);

                            // beacons.add(beacon);//把对象加入集合
//                        if (beaconsListener != null) {
//                            //beaconListener.onScanBeacon(beacon);//将数据通过接口方法回调给实现对象
//                            beaconsListener.onScanBeacon(beacons);
//                        }


                            if (beaconsListener != null) {
                                beaconListener.onScanBeacon(beacon);//将数据通过接口方法回调给实现对象
                            }
                        }

                        /*留下一半上次数据，减少突变*/
                        for (int j = 0; j < dataList_3.size(); j++) {
                            if (j % 2 != 0)
                                dataList_3.remove(j);
                        }
                        Log.e(TAG, "偶数元素保留：" + dataList_3.toString());
                    }
                }
            }
        }
    };

    /**
     * 从这个接口接收一个beacon对象
     * Beacon回调接口 BeaconListener
     */
    public interface BeaconListener {
        void onScanBeacon(Beacon beacon);
    }

    /**
     * 从这个接口接收一个beacon对象集合
     * Beacons回调接口 BeaconListener
     */
    public interface BeaconsListener {
        void onScanBeacon(ArrayList<Beacon> beacons);
    }


    private final static byte[] hex = "0123456789ABCDEF".getBytes();

    /**
     * 字节数组转十六进制字符串
     *
     * @param b: byte[] bytes_1=new byte[]{(byte) 0xA0,(byte) 0xB1,2}
     * @return "A0B102"
     */
    private static String BytesToHexString(byte[] b) {
        if (b == null) {
            return null;
        }
        byte[] buff = new byte[2 * b.length];
        for (int i = 0; i < b.length; i++) {
            buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
            buff[2 * i + 1] = hex[b[i] & 0x0f];
        }
        return new String(buff);
    }

    /**
     * 高低位 组成int，
     *
     * @param hi
     * @param lo
     * @return
     */
    private static int buildUint16(byte hi, byte lo) {
        return (int) ((hi << 8) + (lo & 0xff));
    }

    /**
     * 计算距离的方法，参考AltBeacon的计算方式，这里参考了N5的手机参数
     *
     * @param rssi
     * @param txPower
     * @return
     */
    private static double calculDistance(double rssi, int txPower) {

        int realTxPower = txPower;
        double primiryData;
        if (rssi == 0) {
            return -1;
        }
        double ratio = rssi * 1.0 / realTxPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            //primiryData = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            primiryData = (0.42093) * Math.pow(ratio, 6.9476) + 0.54992;
            return primiryData;
        }
    }

}
