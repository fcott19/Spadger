package com.fcott.spadger.model.bean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/12/30.
 */

public class M3u8Bean {
    private ArrayList<M3u8Item> dataList = new ArrayList<>();
    private double totalTime;

    public ArrayList<M3u8Item> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<M3u8Item> dataList) {
        this.dataList = dataList;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }
}
