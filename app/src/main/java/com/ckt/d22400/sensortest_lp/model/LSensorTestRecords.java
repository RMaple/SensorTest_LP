package com.ckt.d22400.sensortest_lp.model;

/**
 * Created by D22400 on 2017/10/24.
 * Email:danfeng.qiu@ck-telecom.com
 * Describe:
 */

public class LSensorTestRecords {

    private int mNo;

    private String mTestProject;

    private int mLux;

    private String mRange;

    private long mTime;

    public LSensorTestRecords(int no, String testProject, int lux, String range, long time) {
        mNo = no;
        mTestProject = testProject;
        mLux = lux;
        mRange = range;
        mTime = time;
    }

    public int getNo() {
        return mNo;
    }

    public void setNo(int no) {
        mNo = no;
    }

    public String getTestProject() {
        return mTestProject;
    }

    public void setTestProject(String testProject) {
        mTestProject = testProject;
    }

    public int getLux() {
        return mLux;
    }

    public void setLux(int lux) {
        mLux = lux;
    }

    public String getRange() {
        return mRange;
    }

    public void setRange(String range) {
        mRange = range;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }
}
