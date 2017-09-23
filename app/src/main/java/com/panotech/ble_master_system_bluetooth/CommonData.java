package com.panotech.ble_master_system_bluetooth;

import com.panotech.ble_master_system_utils.LimitedSizeQueue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by qianxun on 2017/07/25.
 */

public class CommonData {
    public static DeviceAdapter mDeviceAdapter;

    public static LimitedSizeQueue<LogData> TestLogQueue = new LimitedSizeQueue<>(99);

    public static int WholePeople = 0;

    public static final long RefreshTime = 3000;

    public static boolean isOver() throws ParseException {


        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String date1="20170930";

        Date beginTime=formatter.parse(date1);

        if (curDate.after(beginTime)) {
            return true;
        } else {
            return false;
        }
    };
}
