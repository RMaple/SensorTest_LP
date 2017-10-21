package com.ckt.d22400.sensortest_lp.model;

/**
 * Created by qdfy4 on 2017/10/21.
 */

public class PSensorTestRecords {

    private int mScreenOffTime;
    private int mScreenOnTime;

    public PSensorTestRecords(int screenOffTime) {
        mScreenOffTime = screenOffTime;
    }

    public int getScreenOffTime() {
        return mScreenOffTime;
    }

    public void setScreenOffTime(int screenOffTime) {
        mScreenOffTime = screenOffTime;
    }

    public int getScreenOnTime() {
        return mScreenOnTime;
    }

    public void setScreenOnTime(int screenOnTime) {
        mScreenOnTime = screenOnTime;
    }
}
