package com.panotech.ble_master_system_utils;

import java.util.ArrayList;

/**
 * Created by sylar on 2017/07/22.
 */

public class LimitedSizeQueue<K> extends ArrayList<K> {

    private int maxSize;

    public LimitedSizeQueue(int size){
        this.maxSize = size;
    }

    public boolean add(K k){
        boolean r = super.add(k);
        if (size() > maxSize){
            removeRange(0, size() - maxSize - 1);
        }
        return r;
    }

    public K getYongest() {
        if(size() != 0) {
            return get(size() - 1);
        }
        else {
            return null;
        }
    }

    public K getOldest() {
        return get(0);
    }

    public LimitedSizeQueue copy(LimitedSizeQueue<K> otherList){
        int count = otherList.size();
        LimitedSizeQueue<K> newList = new LimitedSizeQueue(count);
        int position = 0;
        while (position < count){
            K k = otherList.get(position++);
            newList.add(k);
        }
        return newList;
    }

}
