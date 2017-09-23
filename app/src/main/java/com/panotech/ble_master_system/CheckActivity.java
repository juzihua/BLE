package com.panotech.ble_master_system;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.panotech.ble_master_system_bluetooth.BLE;
import com.panotech.ble_master_system_bluetooth.CommonData;
import com.panotech.ble_master_system_bluetooth.ScannedDevice;
import com.panotech.ble_master_system_utils.BusTableView;
import com.panotech.ble_master_system_utils.DateUtil;
import com.panotech.ble_master_system_utils.LimitedSizeQueue;
import com.panotech.ble_master_system_utils.Signal;
import com.panotech.ble_master_system_webconnect.Visitor;
import com.panotech.ble_master_system_webconnect.Visitors;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.panotech.ble_master_system_bluetooth.CommonData.WholePeople;
import static com.panotech.ble_master_system_webconnect.SeatNumber.CheckSeat2;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CheckActivity extends Activity {
    private BusTableView mTestTableView;
    private TextView mWholeTextView, mPresentTextView, mAbsentTextView, mTimerTextView;
    private Button mDis1Button, mDis2Button;
    private LinearLayout mainLayout;
    private List<ScannedDevice> list;
    private int PeopleCount = 0;
    public static int COL = 2;
    public int ROW = 5;
    private Timer mTimer;
    private HashMap seatMap = new HashMap();
    private Handler mHandler = new Handler();

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        mWholeTextView = (TextView)findViewById(R.id.check_whole_people);
        mPresentTextView = (TextView)findViewById(R.id.check_present_people);
        mAbsentTextView = (TextView)findViewById(R.id.check_absent_people);
        mDis1Button = (Button)findViewById(R.id.check_btn_dis1);
        mDis2Button = (Button)findViewById(R.id.check_btn_dis2);
        mTimerTextView = (TextView) findViewById(R.id.timmer);
        initCheckUI();
        list = CommonData.mDeviceAdapter.getList();
        mainLayout = (LinearLayout)findViewById(R.id.test_layout_container);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        mTestTableView = new BusTableView(getApplicationContext(), COL);
        BusTableView.LayoutParams lp = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        mTestTableView.setLayoutParams(lp);
        mTestTableView.setColumnStretchable(0,true);
        mTestTableView.setColumnStretchable(2,true);
        mTestTableView.setPadding(5, 10, 5 , 10);
        AddCustomersRows(mTestTableView);
        initUI(mTestTableView);
        seatPainting(mTestTableView);
        initCheckUI();
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Log.i("**********","###################################################################################################");
                PeopleCount = 0;
                seatPainting(mTestTableView);
                initCheckUI();
                Log.i("CHECKupdate", "UPDATED!");
            }
        };
        mainLayout.addView(mTestTableView);
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        },CommonData.RefreshTime,CommonData.RefreshTime);

        mDis1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "近１を設定しました！", Toast.LENGTH_SHORT).show();
                Visitors.setBleStandard("内");
                if(mTimer != null){
                    mTimer.cancel();
//                    mTimer.purge();
                    mTimer = null;
                }
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(0);
                    }
                },0,CommonData.RefreshTime);
            }
        });

        mDis2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "近２を設定しました！", Toast.LENGTH_SHORT).show();
                Visitors.setBleStandard("中");
                if(mTimer != null){
                    mTimer.cancel();
//                    mTimer.purge();
                    mTimer = null;
                }
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(0);
                    }
                },0,CommonData.RefreshTime);
            }
        });
    }

    private void AddCustomersRows(BusTableView view) {
        int rowNumber = 0;
        int CustomerPosition = 1;
        int column = view.getM_ColumnN();

        Iterator iter1 = Visitors.visitormap.entrySet().iterator();
        while (iter1.hasNext()) {
            HashMap.Entry entry1 = (HashMap.Entry) iter1.next();
            HashMap val1 = (HashMap)entry1.getValue();
            Iterator iter2 = val1.entrySet().iterator();
            while (iter2.hasNext()){
                HashMap.Entry entry2 = (HashMap.Entry) iter2.next();
                Visitor visitor = (Visitor)entry2.getValue();
                String st1 = visitor.seat;
//                String st2 = visitor.name;
                seatMap.put(st1, visitor);
            }
        }
        //
        while (COL * ROW - column * rowNumber > 0) {
            String name1;
            String name2;
            String seat1 = CheckSeat2(CustomerPosition++);
            if(seatMap.containsKey(seat1)){
                Visitor vis = (Visitor) seatMap.get(seat1);
                name1 = vis.name;
                seat1 = seat1 + " " + vis.feature;
            }
            else{
                name1 = "                         ";
            }
            String seat2 = CheckSeat2(CustomerPosition++);
            if(seatMap.containsKey(seat2)){
                Visitor vis = (Visitor) seatMap.get(seat2);
                name2 = vis.name;
                seat2 = seat2 + " " + vis.feature;
            }
            else{
                name2 = "                         ";
            }
            view.addRow(
                    new String[]{seat1, seat2},
                    new String[]{name1, name2} );
            rowNumber++;
        }
    }

    private void seatPainting(BusTableView view){
        int check;
        int position = 0;
        HashMap seatPaintMap = new HashMap();
        check = 0;
        for(int i=0; i< COL*ROW; i++){
            seatPaintMap.put(i+1, check);
        }
        check = 1;
        Iterator iter3 = seatMap.entrySet().iterator();
        while (iter3.hasNext()){
            HashMap.Entry entry3 = (HashMap.Entry) iter3.next();
            String seat = (String) entry3.getKey();
            int num = seatToNum(seat);
            seatPaintMap.put(num, check);
        }

        while (position < list.size()) {
            ScannedDevice device = list.get(position++);
            int intMajor = device.getBLE().getMajor();
            int intMinor = device.getBLE().getMinor();
            String stMajor = Integer.toString(intMajor);
            String stMinor = Integer.toString(intMinor);
            Log.i("PRINTBEFORE", Integer.toString(device.getBLE().getMajor()) +"  "+ Integer.toString(device.getBLE().getMinor()));
            if (Visitors.visitormap.containsKey(stMajor) && Visitors.visitormap.get(stMajor).containsKey(stMinor)) {
                    String stringSeat = (String) Visitors.visitormap.get(stMajor).get(stMinor).seat;
                    int intSeat = seatToNum(stringSeat);
                    double doubleAveAccuracy = device.getAveAccuracy();
                    LimitedSizeQueue<Signal> store = device.rssiStore;
                    String logstr = "";
                    for(Signal signal : store) {
                        logstr += DateUtil.get_yyyyMMddHHmmssSSS(signal.timestamp) + "      "+ signal.rssi + "___";
                    }
                    Log.i("******queue:", logstr);
                    Log.i("******","#Major:" + stMajor + "#Minor:"+stMinor+"#doubleAveAccuracy:"+String.valueOf(doubleAveAccuracy));
                    int intAveAccuracy = BLE.calculateProximity(doubleAveAccuracy);
                    if(intAveAccuracy == BLE.PROXIMITY_NEAR){
                        seatPaintMap.put(intSeat, 2);
                    }
            }
        }

        for(int i=0; i<COL*ROW; i++){
            int checked = (int) seatPaintMap.get(i+1);
            int halfCOL = COL/2;
            int column = i%COL;
            int row = (i - column) / COL;
            if(column<halfCOL){
                View v = view.GetCellView(row, column);
                if(checked == 1){
                    v.setBackground(getDrawable(R.drawable.rectanglebackgroundred));
                }
                else if(checked == 2){
                    PeopleCount++;
                    v.setBackground(getDrawable(R.drawable.rectanglebackgroundblue));
                }
            }
            else{
                View v = view.GetCellView(row, column+1);
                if(checked == 1){
                    v.setBackground(getDrawable(R.drawable.rectanglebackgroundred));
                }
                else if(checked == 2){
                    PeopleCount++;
                    v.setBackground(getDrawable(R.drawable.rectanglebackgroundblue));
                }
            }
        }
    }

    private void initCheckUI(){
        int AbsentPeople = WholePeople - PeopleCount;
        mWholeTextView.setText(String.valueOf(WholePeople));
        mPresentTextView.setText(String.valueOf(PeopleCount));
        mAbsentTextView.setText(String.valueOf(AbsentPeople));
        SimpleDateFormat   formatter   =   new SimpleDateFormat("HH:mm:ss");
        Date curDate   =   new   Date(System.currentTimeMillis());//获取当前时间
        String   str   =   formatter.format(curDate);
        mTimerTextView.setText(str);
    }

    private void initUI(BusTableView view){
        for(int i=0; i<COL*ROW; i++){
            int halfCOL = COL/2;
            int column = i%COL;
            int row = (i - column) / COL;
            if(column<halfCOL){
                View v = view.GetCellView(row, column);
                v.setBackground(getDrawable(R.drawable.rectanglebackgroundwhite));
            }
            else{
                View v = view.GetCellView(row, column+1);
                v.setBackground(getDrawable(R.drawable.rectanglebackgroundwhite));
            }
        }
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

    @Override
    protected void onPause(){
        super.onPause();
        if(mTimer != null) {
            mTimer.cancel();
//            mTimer.purge();
            mTimer = null;
        }
    }
}