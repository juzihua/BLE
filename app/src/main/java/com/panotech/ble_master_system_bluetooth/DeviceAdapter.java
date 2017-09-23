package com.panotech.ble_master_system_bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.panotech.ble_master_system.R;
import com.panotech.ble_master_system_utils.Signal;
import com.panotech.ble_master_system_webconnect.Visitors;
import com.panotech.ble_master_system_utils.DateUtil;

import java.text.DecimalFormat;
import java.util.List;

import static com.panotech.ble_master_system_bluetooth.BLE.calculateProximity;

/**
 * Created by sylar on 2017/07/17.
 */

public class DeviceAdapter extends ArrayAdapter<ScannedDevice> {
    private static final String PREFIX_LASTUPDATED = "Last Udpated:";
    private List<ScannedDevice> mList;
    private LayoutInflater mInflater;
    private int mResId;

    public DeviceAdapter(Context context, int resId, List<ScannedDevice> deviceList) {
        super(context, resId, deviceList);
        mResId = resId;
        mList = deviceList;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScannedDevice item = (ScannedDevice) getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(mResId, null);
        }
        if(item != null) {
            TextView name = (TextView) convertView.findViewById(R.id.device_name);
            TextView address = (TextView) convertView.findViewById(R.id.device_address);
            TextView rssi = (TextView) convertView.findViewById(R.id.device_rssi);
            TextView lastupdated = (TextView) convertView.findViewById(R.id.device_lastupdated);
            TextView scanRecord = (TextView) convertView.findViewById(R.id.device_scanrecord);
            TextView ibeaconInfo = (TextView) convertView.findViewById(R.id.device_ble_info);
            //Resources res = convertView.getContext().getResources();

            if(name != null){ name.setText(item.getDisplayName()); }
            if(address != null){ address.setText(item.getDevice().getAddress()); }
            if(rssi != null){ rssi.setText(Integer.toString(item.getRssi())); }
            if(lastupdated != null){ lastupdated.setText(PREFIX_LASTUPDATED + DateUtil.get_yyyyMMddHHmmssSSS(item.getLastUpdatedMs())); }
            if(scanRecord != null){ scanRecord.setText(item.getScanRecordHexString()); }
            if (item.getBLE() != null) {
                ibeaconInfo.setText(/*res.getString(R.string.label_ble)*/"This is my BLE" + "\n"
                        + item.getBLE().toString());
                if(rssi != null){
                    double a = item.getAveAccuracy();
                    rssi.setText(calculateDistance(a));
                }
            } else {
                ibeaconInfo.setText(/*res.getString(R.string.label_not_ble)*/ "This is not my BLE");
            }

        }

        return convertView;
    }

    public String update(BluetoothDevice newDevice, int rssi, byte[] scanRecord) {
        if ((newDevice == null) || (newDevice.getAddress() == null)) {
            Log.i("update failed", "1111111111111111111111111111");
            return "";
        }
        long now = System.currentTimeMillis();

        boolean contains = false;
        DecimalFormat df = new DecimalFormat("#.00");
        for (ScannedDevice device : mList) {
            if (newDevice.getAddress().equals(device.getDevice().getAddress())) {
                contains = true;
                Signal signal = new Signal();
                signal.timestamp = System.currentTimeMillis();
                signal.rssi = rssi;
                device.rssiStore.add(signal);
                if(device.getBLE() != null) {
                    long time = System.currentTimeMillis();
                    String logData = device.getBLE().toString() + " \nRssi=" + String.valueOf(rssi)
                            + "  \nDistanceQueueAve=" + String.valueOf(df.format(device.getAveAccuracy()))
                            + " \nDDM:" + device.showScanData().substring(0, 59) + "  \n" + DateUtil.get_yyyyMMddHHmmssSSS(time);
                    Log.i("DALOG", logData);

                    LogData log = new LogData();
                    log.major = device.getBLE().major;
                    log.minor = device.getBLE().minor;
                    log.logData = logData;

                    CommonData.TestLogQueue.add(log);
                }
                device.setLastUpdatedMs(now);
                device.setScanRecord(scanRecord);
                break;
            }
        }
        if (!contains) {
            ScannedDevice device = new ScannedDevice(newDevice, rssi, scanRecord, now);
            if(device.getBLE() != null) {
                Log.i("UpdatedDevice---", newDevice.getAddress());
                mList.add(device);
            }
        }
        int totalCount = 0;
        int BLECount = 0;
        if (mList != null) {
            totalCount = mList.size();
            for (ScannedDevice device : mList) {
                totalCount++;
                if(Visitors.visitormap.containsKey(Integer.toString(device.getBLE().getMajor()))
                        && Visitors.visitormap.get(Integer.toString(device.getBLE().getMajor())).containsKey(Integer.toString(device.getBLE().getMinor()))){
                    BLECount++;
                }
            }
            Log.i("totalCount", Integer.toString(totalCount));
        }

        notifyDataSetChanged();
        String summary = Integer.toString(BLECount);
        return summary;
    }

    public List<ScannedDevice> getList() {
        return mList;
    }

    private String calculateDistance(Double accuracy) {
        StringBuilder sb = new StringBuilder();
        if (accuracy != null) {
            int d = calculateProximity(accuracy);
            switch (d) {
                case 1:
                    sb.append("圏内");
                    break;
                case 2:
                    sb.append("近隣");
                    break;
//                case 3:
//                    sb.append("FAR");
//                    break;
                case 0:
                    sb.append("圏外");
                    break;
            }
        } else {
            sb.append("圏外");
        }
        return sb.toString();
    }

}
