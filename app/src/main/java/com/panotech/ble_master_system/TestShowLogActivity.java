package com.panotech.ble_master_system;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.panotech.ble_master_system_bluetooth.CommonData;
import com.panotech.ble_master_system_bluetooth.LogData;
import com.panotech.ble_master_system_utils.LimitedSizeQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sylar on 2017/07/30.
 */

public class TestShowLogActivity extends Activity {
    private ListView mListView;
    private Button mRefreshButton;
    private myAdapter mAdapter;
    private List<LogData> mStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showlog);
        mListView = (ListView) findViewById(R.id.log_listview);
        mRefreshButton = (Button) findViewById(R.id.log_btn_refresh);
        try {
            mStrings = update(CommonData.TestLogQueue);
            mAdapter = new myAdapter(getApplicationContext(), R.layout.test_list_logdata, mStrings);
            mAdapter.notifyDataSetChanged();
            mListView.setAdapter(mAdapter);
            mRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(
                            getApplicationContext(), "Refreshing success!", Toast.LENGTH_SHORT
                    ).show();
                    mStrings = update(CommonData.TestLogQueue);
                    mAdapter = new myAdapter(getApplicationContext(), R.layout.test_list_logdata, mStrings);
                    mAdapter.notifyDataSetChanged();
                    mListView.setAdapter(mAdapter);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    private class myAdapter extends ArrayAdapter<LogData> {
        private List<LogData> mList;
        private LayoutInflater mInflater;
        private int mResId;
        private TextView mDataTextView;

        public myAdapter(Context context, int resID, List<LogData> list) {
            super(context, resID, list);
            mResId = resID;
            mList = list;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LogData logData = mList.get(mList.size() - position - 1);
            if (convertView == null) {
                convertView = mInflater.inflate(mResId, null);
            }
            mDataTextView = (TextView) convertView.findViewById(R.id.list_logdata);
            mDataTextView.setText("No." + String.valueOf(position + 1) + "    " + logData.logData);
            return convertView;
        }
    }

    public List update(LimitedSizeQueue rawData){
        List<LogData> newList = new ArrayList<>();
        newList.addAll(rawData);

        List<LogData> returnList = new ArrayList<>();

        Map<Integer, List<Integer>> logMap = new HashMap<Integer, List<Integer>>();
        if(newList.size()>0) {
            for (int i = newList.size() - 1; i >= 0; i--) {
                LogData log = newList.get(i);
                if (logMap.containsKey(log.major)) {
                    List<Integer> list = logMap.get(log.major);
                    if (list.contains(log.minor)) {
                        continue;
                    } else {
                        returnList.add(log);
                        logMap.get(log.major).add(log.minor);
                        Log.i("bbbbbbbbbbbb",log.major + "#"+log.minor+"#"+log.logData);
                    }
                } else {
                    returnList.add(log);
                    List<Integer> addList = new ArrayList<>();
                    addList.add(log.minor);
                    logMap.put(log.major, addList);
                    Log.i("bbbbbbbbbbbb",log.major + "#"+log.minor+"#"+log.logData);
                }
            }
        }
        return returnList;
    }
}
