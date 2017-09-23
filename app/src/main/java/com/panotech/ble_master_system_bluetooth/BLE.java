package com.panotech.ble_master_system_bluetooth;

import android.util.Log;

import com.panotech.ble_master_system_webconnect.Visitor;
import com.panotech.ble_master_system_webconnect.Visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sylar on 2017/07/17.
 */

public class BLE {
    public static final int PROXIMITY_NEAR = 1;
    public static final int PROXIMITY_IMMEDIATE = 2;
    public static final int PROXIMITY_UNKNOWN = 0;


    public static final Integer STANDARD_INNER=0;
    public static final Integer STANDARD_NEAR=1;
    public static final Integer STANDARD_FAR=2;

    public static Map<Integer, ArrayList<Double>> STANDARD_DISTANCE_MAP = new HashMap<Integer, ArrayList<Double>>();
    static {
        STANDARD_DISTANCE_MAP.put(STANDARD_INNER, new ArrayList<Double>(Arrays.asList(5.0, 30.0)));//内
        STANDARD_DISTANCE_MAP.put(STANDARD_NEAR, new ArrayList<Double>(Arrays.asList(10.0, 50.0)));//近
        STANDARD_DISTANCE_MAP.put(STANDARD_FAR, new ArrayList<Double>(Arrays.asList(15.0, 50.0)));//遠
    }

    final private static char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String TAG = "BLE";
    protected String proximityUuid;
    protected int major;
    protected int minor;
    protected Integer proximity;
    protected Double accuracy;
    protected int rssi;
    protected int txPower;
    protected Double runningAverageRssi = null;
    protected int sd; //for test
    protected String Distance;

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    protected Visitor visitor;

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRssi() {
        return rssi;
    }

    public int getTxPower() {
        return txPower;
    }

    public String getProximityUuid() {
        return proximityUuid;
    }

    public void setRunningAverageRssi(Double aveRssi){
        runningAverageRssi = aveRssi;
    }

    @Override
    public int hashCode(){
        return minor;
    }

    public Double getAccuracy() {
        if (accuracy == null) {
            accuracy = calculateAccuracy(txPower, runningAverageRssi != null ? runningAverageRssi : rssi);
        }
        return accuracy;
    }

    public static double calculateAccuracy(int txPower, double rssi) {
        if(rssi == 0){
            return 100.0;
        }
        else{
            return Math.pow(10, (txPower - rssi)/20);
        }
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof BLE)) {
            return false;
        }
        BLE thatBLE = (BLE) that;
        return (thatBLE.getMajor() == this.getMajor() && thatBLE.getMinor() == this.getMinor() && thatBLE.getProximityUuid().equals(this.getProximityUuid()));
    }

    public static BLE fromScanData(byte[] scanData, int rssi) {
        int startByte = 0;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int)scanData[startByte] & 0xff) == 0x4c &&
                    ((int)scanData[startByte+1] & 0xff) == 0x00 &&
                    ((int)scanData[startByte+2] & 0xff) == 0x02 &&
                    ((int)scanData[startByte+3] & 0xff) == 0x15
//                    && ((int)scanData[startByte-1] & 0xff) == 0xff
)  {
                patternFound = true;
                break;
            }
//            else if (((int)scanData[startByte] & 0xff) == 0x2d &&
//                    ((int)scanData[startByte+1] & 0xff) == 0x24 &&
//                    ((int)scanData[startByte+2] & 0xff) == 0xbf &&
//                    ((int)scanData[startByte+3] & 0xff) == 0x16) {
//                BLE ble = new BLE();
//                ble.major = 0;
//                ble.minor = 0;
//                ble.proximityUuid = "00000000-0000-0000-0000-000000000000";
//                ble.txPower = -55;
//                return ble;
//            }
            startByte++;
        }

        if (patternFound == false) {
            Log.d(TAG, "This is not a ble device (no (4c000215) seen in bytes 2-5).  The bytes I see are: "+bytesToHex(scanData));
            return null;
        }

        BLE ble = new BLE();

        ble.sd = (int)scanData[startByte-1] & 0xff;
        ble.major = (scanData[startByte+20] & 0xff) * 0x100 + (scanData[startByte+21] & 0xff);
        ble.minor = (scanData[startByte+22] & 0xff) * 0x100 + (scanData[startByte+23] & 0xff);
        ble.txPower = (int)scanData[startByte+24]; // this one is signed
        ble.rssi = rssi;

        byte[] proximityUuidBytes = new byte[16];
        System.arraycopy(scanData, startByte+4, proximityUuidBytes, 0, 16);
        String hexString = bytesToHex(proximityUuidBytes);
        StringBuilder sb = new StringBuilder();
        sb.append(hexString.substring(0,8));
        sb.append("-");
        sb.append(hexString.substring(8,12));
        sb.append("-");
        sb.append(hexString.substring(12,16));
        sb.append("-");
        sb.append(hexString.substring(16,20));
        sb.append("-");
        sb.append(hexString.substring(20,32));
        ble.proximityUuid = sb.toString();

        return ble;
    }
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int calculateProximity(Double accuracy) {
        double a0 =  STANDARD_DISTANCE_MAP.get(Visitors.getBle_standard()).get(0);
        double a1 =  STANDARD_DISTANCE_MAP.get(Visitors.getBle_standard()).get(1);
        Log.i("aaaaaaaaaaaa0",String.valueOf(a0));
        Log.i("aaaaaaaaaaaa1",String.valueOf(a1));

        if (accuracy > STANDARD_DISTANCE_MAP.get(Visitors.getBle_standard()).get(1)) {
            return PROXIMITY_UNKNOWN;
        }
        else if (accuracy < STANDARD_DISTANCE_MAP.get(Visitors.getBle_standard()).get(0)) {
            return BLE.PROXIMITY_NEAR;
        }
        return BLE.PROXIMITY_IMMEDIATE;

    }

    protected BLE(BLE otherble) {
        this.major = otherble.major;
        this.minor = otherble.minor;
        this.accuracy = otherble.accuracy;
        this.proximity = otherble.proximity;
        this.rssi = otherble.rssi;
        this.proximityUuid = otherble.proximityUuid;
        this.txPower = otherble.txPower;
    }

    protected BLE() {

    }

    protected BLE(String proximityUuid, int major, int minor, int txPower, int rssi) {
        this.proximityUuid = proximityUuid;
        this.major = major;
        this.minor = minor;
        this.rssi = rssi;
        this.txPower = txPower;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UUID=").append(this.proximityUuid.toUpperCase());
        sb.append(" \nMajor=").append(this.major);
        sb.append(" \nMinor=").append(this.minor);
        sb.append(" \nTxPower=").append(this.txPower);

        return sb.toString();
    }

}
