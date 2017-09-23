package com.panotech.ble_master_system_webconnect;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qianxun on 2017/07/25.
 */

public class Visitors {
    public static Map<String, Map<String, Visitor>> visitormap = new HashMap<>();
    public static Map<String, String> settingsMap = new HashMap<>();
    private static Integer ble_standard = 0;

    public static Integer getBle_standard() {
        return ble_standard;
    }

    private static void setBle_standard(Integer ble_standard) {
        Visitors.ble_standard = ble_standard;
    }

    public static void setBleStandard(String tag){
        switch (tag){
            case "内":
                setBle_standard(0);
                break;
            case "遠":
                setBle_standard(2);
                break;
            default:
                setBle_standard(1);
        }
    }

    public static void clearBleStandard(){
        setBle_standard(0);
    }

}
