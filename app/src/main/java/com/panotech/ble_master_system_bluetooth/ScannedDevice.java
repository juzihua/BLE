package com.panotech.ble_master_system_bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.panotech.ble_master_system_utils.Signal;
import com.panotech.ble_master_system_utils.DateUtil;
import com.panotech.ble_master_system_utils.LimitedSizeQueue;

import java.util.UUID;

/**
 * Created by sylar on 2017/07/17.
 */

public class ScannedDevice {
    private static final String UNKNOWN = "Unknown";
    private BluetoothDevice mDevice;
    private int mRssi;
    private String mDisplayName;
    private byte[] mScanRecord;
    private BLE mBLE;
    private long mLastUpdatedMs;
    private long mLogLastUpdatedMs = 0;
    private Double aveAccuracy;
    private UUID mUUID;
    private String mName, mSeat, mAppear;

    public ScannedDevice(BluetoothDevice device, int rssi, byte[] scanRecord, long now) {
        if (device == null) {
            throw new IllegalArgumentException("BluetoothDevice is null");
        }
        mLastUpdatedMs = now;
        mDevice = device;
        mDisplayName = device.getName();
        if ((mDisplayName == null) || (mDisplayName.length() == 0)) {
            mDisplayName = UNKNOWN;
        }
        mRssi = rssi;
        mScanRecord = scanRecord;
        mUUID = UUID.randomUUID();
        mName = "";
        mSeat = "";
        mAppear = "";
        checkIBeacon();
    }

    private void checkIBeacon() {
        if (mScanRecord != null) {
            mBLE = BLE.fromScanData(mScanRecord, mRssi);
        }
    }

    public UUID getUUID() {
        return mUUID;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public int getRssi() {
        if(rssiStore.size() != 0) {
            return rssiStore.getYongest().rssi;
        }
        else {
            return 0;
        }
    }

    public void setRssi(int rssi) {
        mRssi = rssi;
    }

    public long getLastUpdatedMs() {
        return mLastUpdatedMs;
    }

    public void setLastUpdatedMs(long lastUpdatedMs) {
        mLastUpdatedMs = lastUpdatedMs;
    }

    public byte[] getScanRecord() {
        return mScanRecord;
    }

    public String getScanRecordHexString() {
        return ScannedDevice.asHex(mScanRecord);
    }

    public Signal getSignal(){
        return rssiStore.getYongest();
    }
    public void setScanRecord(byte[] scanRecord) {
        mScanRecord = scanRecord;
        checkIBeacon();
    }

    public long getLogLastUpdatedMs() {
        return mLogLastUpdatedMs;
    }

    public void setLogLastUpdatedMs(long logLastUpdatedMs) {
        mLogLastUpdatedMs = logLastUpdatedMs;
    }

    public BLE getBLE() {
        return mBLE;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(String displayName) {
        mDisplayName = displayName;
    }

    public Double getAveAccuracy(){
        aveAccuracy = calculateDistance(rssiStore);
        return aveAccuracy;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSeat() {
        return mSeat;
    }

    public void setSeat(String seat) {
        mSeat = seat;
    }

    public String getAppear() {
        return mAppear;
    }

    public void setAppear(String appear) {
        mAppear = appear;
    }

    public static String asHex(byte bytes[]) {
        if ((bytes == null) || (bytes.length == 0)) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (int index = 0; index < bytes.length; index++) {
            int bt = bytes[index] & 0xff;
            if (bt < 0x10) {
                sb.append("0");
            }

            sb.append(Integer.toHexString(bt).toUpperCase());
        }

        return sb.toString();
    }

    public LimitedSizeQueue<Signal> rssiStore = new LimitedSizeQueue(30);

    private Double calculateDistance(LimitedSizeQueue<Signal> rssiStore){
        StringBuilder sb = new StringBuilder();
        Long now = System.currentTimeMillis();
        int i=1;
        if(rssiStore.size() != 0) {
            i = rssiStore.size();
        }
        double sum = 0.;
        for(Signal signal : rssiStore){
            if (now < signal.timestamp + 3200) {
                sb.append(signal.rssi + "now  ");
                double accuracy = BLE.calculateAccuracy(getBLE().getTxPower(), signal.rssi);
                Log.i("####Accuracy calculated", Double.toString(accuracy));
                sum += accuracy;
            } else {
                sb.append(signal.rssi + "past ");
                i--;

            }
        }
        String logstr = "";
        for(Signal signal : rssiStore) {
            logstr += DateUtil.get_yyyyMMddHHmmssSSS(signal.timestamp) + "      "+ signal.rssi + "___";
        }
        Log.d("####properties saved:", logstr);
        sb.append(100.0);
        Log.i("####caculatingAccuracy", sb.toString());
        Log.i("####aveaccuracy", Double.toString(sum*(1.0)/i));
        if(i == 0) return 100.;
        return sum*(1.0)/i;
    }

    public String timeToString(){
        StringBuilder sb = new StringBuilder();
        sb.append(DateUtil.get_yyyyMMddHHmmssSSS(mLastUpdatedMs));
        return sb.toString();
    }

    public String showScanData(){
        return BLE.bytesToHex(mScanRecord);
    }
}
