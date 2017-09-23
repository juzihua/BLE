package com.panotech.ble_master_system_bluetooth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.panotech.ble_master_system.R;
import com.panotech.ble_master_system_webconnect.Visitors;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sylar on 2017/07/29.
 */

public class PickupAdapter extends ArrayAdapter<ScannedDevice> {
    private List<ScannedDevice> mList;
    private LayoutInflater mInflater;
    private int mResId;

    public PickupAdapter(Context context, int resId, List<ScannedDevice> deviceList) {
        super(context, resId, deviceList);
        mResId = resId;
        mList = deviceList;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScannedDevice device = getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(mResId, null);
        }
        String major = Integer.toString(device.getBLE().getMajor());
        String minor = Integer.toString(device.getBLE().getMinor());
        String name = Visitors.visitormap.get(major).get(minor).name;
        String seat = Visitors.visitormap.get(major).get(minor).seat;
        String feature = Visitors.visitormap.get(major).get(minor).feature;
        TextView mNameTextView = (TextView) convertView.findViewById(R.id.list_customer_name);
        mNameTextView.setText(name);
        TextView mSeatTextView = (TextView) convertView.findViewById(R.id.list_seat_number);
        mSeatTextView.setText(seat);
        TextView mSexTextView = (TextView) convertView.findViewById(R.id.list_customer_sex);
        mSexTextView.setText(feature);
        TextView mDistanceTextView = (TextView) convertView.findViewById(R.id.list_customer_distance);
        String stringProximity = calculateDistance(device.getAveAccuracy());
        mDistanceTextView.setText(stringProximity);
        return convertView;
    }

    public int updatePickup(List<ScannedDevice> devices){
        int pos = 0;
        int PeopleCount = 0;
        while (pos < devices.size()){
            ScannedDevice device = devices.get(pos++);
            if(Visitors.visitormap.containsKey(Integer.toString(device.getBLE().getMajor()))
                    && Visitors.visitormap.get(Integer.toString(device.getBLE().getMajor())).containsKey(Integer.toString(device.getBLE().getMinor()))
                    && BLE.calculateProximity(device.getAveAccuracy()) != BLE.PROXIMITY_NEAR) {
                mList.add(device);
                PeopleCount++;
            }
        }
        Collections.sort(mList, new Comparator<ScannedDevice>() {
            @Override
            public int compare(ScannedDevice lhs, ScannedDevice rhs) {
                if(BLE.calculateProximity(lhs.getAveAccuracy()) != BLE.PROXIMITY_NEAR && BLE.calculateProximity(rhs.getAveAccuracy()) == 1){
                    return -1;
                }
                else if(BLE.calculateProximity(lhs.getAveAccuracy()) == 1 && BLE.calculateProximity(rhs.getAveAccuracy()) != BLE.PROXIMITY_NEAR){
                    return 1;
                }
                else{
                    String lhsSeat = Visitors.visitormap.get(Integer.toString(lhs.getBLE().getMajor())).get(Integer.toString(lhs.getBLE().getMinor())).seat;
                    String rhsSeat = Visitors.visitormap.get(Integer.toString(rhs.getBLE().getMajor())).get(Integer.toString(rhs.getBLE().getMinor())).seat;
                    if(seatToNum(lhsSeat) < seatToNum(rhsSeat)){
                        return -1;
                    }
                    else {
                        return 1;
                    }
                }
            }
        });
        return PeopleCount;
    }

    private int seatToNum(String seat){
        int num = 0;
        switch (seat){
            case "A-1": num=1; break;
            case "B-1": num=2; break;
            case "A-2": num=3; break;
            case "B-2": num=4; break;
            case "A-3": num=5; break;
            case "B-3": num=6; break;
            case "A-4": num=7; break;
            case "B-4": num=8; break;
            case "A-5": num=9; break;
            case "B-5": num=10; break;
        }
        return num;
    }

    private String calculateDistance(Double accuracy) {
        StringBuilder sb = new StringBuilder();
        if (accuracy != null) {
            int d = BLE.calculateProximity(accuracy);
            switch (d) {
                case 1:
                    sb.append("圏内");
                    break;
                case 2:
                    sb.append("近隣");
                    break;
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
