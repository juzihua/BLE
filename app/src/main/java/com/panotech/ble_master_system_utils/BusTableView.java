package com.panotech.ble_master_system_utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.panotech.ble_master_system.CheckActivity;
import com.panotech.ble_master_system.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sylar on 2017/07/19.
 */

public class BusTableView extends TableLayout {

    protected int m_ColumnN=5;
    protected List<TableRow> m_Rows;
    protected List<List<View>> m_Views;

    public int getM_ColumnN() {
        return m_ColumnN;
    }

    public BusTableView(Context context) {
        super(context);
        m_Rows=new ArrayList<TableRow>();
        m_Views=new ArrayList<List<View>>();
        this.setWillNotDraw(false);
    }
    public BusTableView(Context context, int n) {
        super(context);
        m_Rows=new ArrayList<TableRow>();
        m_Views=new ArrayList<List<View>>();
        if(n>0) m_ColumnN=n;
        else m_ColumnN=5;
        this.setWillNotDraw(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int addRow(String[] st1, String[] st2)
    {
        if(st1==null) return 0;

        List<View> CRowViews;
        int i,nRows;
        TableRow CRow;
        View v1=null;
        View v2;

        m_Rows.add(new TableRow(this.getContext()));
        m_Views.add(new ArrayList<View>());
        nRows=m_Rows.size();
        CRowViews=m_Views.get(nRows-1);
        CRow=m_Rows.get(nRows-1);

        for(i=0;i<m_ColumnN;i++)
        {
            if (st2[i] != null) v1 = createCellView(st1[i], st2[i]);
            if (v1 == null) v1 = new View(getContext());
            v1.setPadding(10, 10, 10, 10);
            CRow.addView(v1);
            CRowViews.add(v1);
            if(i == (CheckActivity.COL / 2 - 1)){
                v2 = createBlankView();
                CRow.addView(v2);
                CRowViews.add(v2);
            }
        }
        CRow.setPadding(0,10,0,10);
        this.addView(CRow);

        return nRows;
    }

    public View GetCellView(int row,int column)
    {
        if(row<0||row>=m_Rows.size()) return null;
        else
        {
            if(column<0||column>=m_Views.get(row).size()) return null;
            else return m_Views.get(row).get(column);
        }
    }

    protected View createCellView(String st1, String st2)
    {
        View rView;
        TextView tView=new TextView(getContext());
        tView.setGravity(Gravity.CENTER);
        tView.setText(st1 + "\n" + st2);
        tView.setTextSize(20);
        TextPaint paint = tView.getPaint();
        paint.setFakeBoldText(true);
        tView.setTextColor(getResources().getColor(R.color.colorBlack));
        rView=tView;
        return rView;
    }

    protected View createBlankView(){
        View rView;
        TextView textView = new TextView(getContext());
        textView.setText("   ");
        rView = textView;
        return rView;
    }

}
