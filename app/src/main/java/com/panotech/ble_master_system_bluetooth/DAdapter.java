package com.panotech.ble_master_system_bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.panotech.ble_master_system.R;
import com.panotech.ble_master_system_utils.Signal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static com.panotech.ble_master_system_bluetooth.BLE.calculateProximity;

/**
 * Created by sylar on 2017/07/23.
 */

public class DAdapter extends ArrayAdapter<ScannedDevice> {
    private List<ScannedDevice> mList;
    private LayoutInflater mInflater;
    private int mResId;
    int mCurrentTouchedIndex = -1;
    public static final int UUIDNUM   = 1;
    public static final int MAJORNUM  = 2;
    public static final int MINORNUM  = 3;
    public static final int NAMEMUM   = 4;
    public static final int SEATNUM   = 5;
    public static final int APPEARNUM = 6;

    public DAdapter(Context context, int resId, List<ScannedDevice> deviceList) {
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

        if(item != null){
            TextView tMajor = (TextView)convertView.findViewById(R.id.textview_settings_major);
            TextView tMinor = (TextView)convertView.findViewById(R.id.textview_settings_minor);
            TextView tDistance = (TextView)convertView.findViewById(R.id.textview_settings_distance);
            TextView tName = (EditText)convertView.findViewById(R.id.textview_settings_name);
            TextView tSeat = (EditText)convertView.findViewById(R.id.textview_settings_seat);
            TextView tAppear = (EditText)convertView.findViewById(R.id.textview_settings_appear);

            tMajor.setText(Integer.toString(item.getBLE().getMajor()));
            tMinor.setText(Integer.toString(item.getBLE().getMinor()));
            double a = item.getAveAccuracy();
            tDistance.setText(calculateDistance(a));
            tName.setText(item.getName());
            tSeat.setText(item.getSeat());
            tAppear.setText(item.getAppear());
            tName.setOnTouchListener(new OnEditTextTouched(position));
            tName.clearFocus();
            if (position == mCurrentTouchedIndex) {
                // 如果该项中的EditText是要获取焦点的
                tName.requestFocus();
            }
            tSeat.setOnTouchListener(new OnEditTextTouched(position));
            tSeat.clearFocus();
            if (position == mCurrentTouchedIndex) {
                tSeat.requestFocus();
            }
            tAppear.setOnTouchListener(new OnEditTextTouched(position));
            tAppear.clearFocus();
            if (position == mCurrentTouchedIndex) {
                tAppear.requestFocus();
            }
        }
        return convertView;
    }

    private String calculateDistance(Double accuracy) {
        StringBuilder sb = new StringBuilder();
        if (accuracy != null) {
            int d = calculateProximity(accuracy);
            switch (d) {
                case 1:
                    sb.append("NEAR");
                    break;
                case 2:
                    sb.append("MIDDLE");
                    break;
                case 3:
                    sb.append("FAR");
                    break;
                case 0:
                    sb.append("UNKNOWN");
                    break;
            }
        } else {
            sb.append("UNKNOWN");
        }
        return sb.toString();
    }

    public String update(BluetoothDevice newDevice, int rssi, byte[] scanRecord) {
        if ((newDevice == null) || (newDevice.getAddress() == null)) {
            return "";
        }
        long now = System.currentTimeMillis();

        boolean contains = false;
        for (ScannedDevice device : mList) {
            if (newDevice.getAddress().equals(device.getDevice().getAddress())) {
                contains = true;
                // update
//                device.setRssi(rssi);
                Signal signal = new Signal();
                signal.timestamp = System.currentTimeMillis();
                signal.rssi = rssi;
                device.rssiStore.add(signal);
                device.setLastUpdatedMs(now);
                device.setScanRecord(scanRecord);
                break;
            }
        }
        if (!contains) {
            // add new BluetoothDevice
            mList.add(new ScannedDevice(newDevice, rssi, scanRecord, now));
        }

        int totalCount = 0;
        int BLECount = 0;
        if (mList != null) {
            totalCount = mList.size();
            for (ScannedDevice device : mList) {
                if (device.getBLE() != null) {
                    BLECount++;
                }
                else{
                    mList.remove(device);
                }
            }
        }

        notifyDataSetChanged();

        String summary = Integer.toString(BLECount) + ":" + Integer.toString(totalCount);
        return summary;
    }

    public void updateProperties() {
        if(mList != null){
            int i = 1;
            for(ScannedDevice device : mList){
                Log.d("updateProperties", Integer.toString(device.getBLE().getMajor()));
                writeProperties(i, device.getUUID(), device.getBLE().getMajor(), device.getBLE().getMinor(), device.getName(), device.getSeat(), device.getAppear());
                i++;
            }
        }
        return;
    }

    public void updateCustomers(int position, String name, String seat, String appear){
        ScannedDevice device = mList.get(position);
        if(device != null){
            device.setName(name);
            device.setSeat(seat);
            device.setAppear(appear);
        }
    }

    public List<ScannedDevice> getList() {
        return mList;
    }

    public void writeProperties(int i, UUID uuid, int major, int minor, String name, String seat, String appear) {
        Properties properties = new Properties();
        OutputStream output = null;
        try {
            output = new FileOutputStream("BLE.properties");
            properties.setProperty(Integer.toString(i*UUIDNUM), String.valueOf(uuid));
            properties.setProperty(Integer.toString(i*MAJORNUM),  Integer.toString(major));
            properties.setProperty(Integer.toString(i*MINORNUM),  Integer.toString(minor));
            properties.setProperty(Integer.toString(i*NAMEMUM),   name);
            properties.setProperty(Integer.toString(i*SEATNUM),   seat);
            properties.setProperty(Integer.toString(i*APPEARNUM), appear);//保存键值对到内存
            properties.store(output, "Date: " + new Date().toString());// 保存键值对到文件中
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class OnEditTextTouched implements View.OnTouchListener {
        private int position;
        public OnEditTextTouched(int position) {
            this.position = position;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mCurrentTouchedIndex = position;
            }
            return false;
        }
    }

}
