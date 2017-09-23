package com.panotech.ble_master_system_webconnect;

/**
 * Created by sylar on 2017/07/20.
 */

public class SeatNumber {
    public static String CheckColumn4(int CustomerPosition){
        String seat= new String();
        switch (CustomerPosition%4){
            case 1: seat = "A"; break;
            case 2: seat = "B"; break;
            case 3: seat = "C"; break;
            case 0: seat = "D"; break;
        }
        return seat;
    }

    public static String CheckColumn2(int CustomerPosition){
        String seat= new String();
        switch (CustomerPosition%2){
            case 1: seat = "A"; break;
            case 0: seat = "B"; break;
        }
        return seat;
    }

    public static int CheckRow4(int CustomerPosition){
        int col, row;
        col = (CustomerPosition-1)%4 + 1;
        row = (CustomerPosition-col)/4 + 1;
        return row;
    }

    public static int CheckRow2(int CustomerPosition){
        int col, row;
        col = (CustomerPosition-1)%2 + 1;
        row = (CustomerPosition-col)/2 + 1;
        return row;
    }

    public static String CheckSeat4(int CustomerPosition){
        StringBuilder sb = new StringBuilder();
        sb.append(CheckColumn4(CustomerPosition));
        sb.append("-");
        sb.append(Integer.toString(CheckRow4(CustomerPosition)));
        return sb.toString();
    }

    public static String CheckSeat2(int CustomerPosition){
        StringBuilder sb = new StringBuilder();
        sb.append(CheckColumn2(CustomerPosition));
        sb.append("-");
        sb.append(Integer.toString(CheckRow2(CustomerPosition)));
        return sb.toString();
    }
}
