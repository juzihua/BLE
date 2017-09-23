package com.panotech.ble_master_system_webconnect;

/**
 * Created by qianxun on 2017/07/25.
 */

public class Visitor {
    public String name;
    public String seat;
    public String feature;

    public Integer getFeatureID(){
        switch(feature){
            case "老男":
                return 1;
            case "中男":
                return 2;
            case "若男":
                return 3;
            case "子男":
                return 4;
            case "老女":
                return 5;
            case "中女":
                return 6;
            case "若女":
                return 7;
            case "子女":
                return 8;
            case "海外":
                return 9;
            default:
                return 0;
        }
    }

    public Integer getSeatID(){
        switch(seat){
            case "A-1":
                return 1;
            case "A-2":
                return 2;
            case "A-3":
                return 3;
            case "A-4":
                return 4;
            case "A-5":
                return 5;
            case "B-1":
                return 6;
            case "B-2":
                return 7;
            case "B-3":
                return 8;
            case "B-4":
                return 9;
            case "B-5":
                return 10;
            default:
                return 0;
        }
    }
}
