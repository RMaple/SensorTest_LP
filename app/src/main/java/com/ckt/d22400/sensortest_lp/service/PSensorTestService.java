package com.ckt.d22400.sensortest_lp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ckt.d22400.sensortest_lp.LcdBrightnessGetter;
import com.ckt.d22400.sensortest_lp.R;
import com.ckt.d22400.sensortest_lp.model.PSensorTestRecords;
import com.ckt.d22400.sensortest_lp.ui.PSensorTestActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 进行P-Sensor测试的后台服务。
 * 因为要在拨号时进行测试，所以需要在服务中进行操作。
 * <p>
 * 在PSensorActivity中点击“开始测试”按钮后即会绑定服务，点击“停止服务”后会解绑。
 */
public class PSensorTestService extends Service {

    private final String TAG = PSensorTestActivity.TAG;
    //判断屏幕是否熄灭的布尔值
    private boolean mIsScreenOff = false;
    //判断记录是否更新（存在）的布尔值
    private boolean mIsRecordsUpdate = false;

    private List<PSensorTestRecords> mRecords;
    //线程池
    private ExecutorService mThreadPool;
    private NotificationManager mNotificationManager;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private TelephonyManager mTelephonyManager;

    @Override
    public void onCreate() {
        super.onCreate();
        initVariable();
    }


    @Override
    public IBinder onBind(Intent intent) {
        //绑定的同时，注册PSensor监听器并且发送通知告知测试已开始。
        mSensorManager.registerListener(mPSensorListener, mProximity, SensorManager.SENSOR_DELAY_FASTEST);
        sendNotification();
        return new PSTestBinder();
    }

    private void initVariable() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mThreadPool = Executors.newSingleThreadExecutor();
        mRecords = new ArrayList<>();
    }

    /**
     * 发送Notification的方法
     * 表示目前正处于测试状态
     */
    private void sendNotification() {
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getClass().getSimpleName())
                .setContentText("测试中...")
                .build();
        if (mNotificationManager != null) {
            mNotificationManager.notify(1, notification);
            startForeground(1, notification);
        }
    }

    private SensorEventListener mPSensorListener = new SensorEventListener() {
        //PSensor事件监听器
        @Override
        public void onSensorChanged(SensorEvent event) {
            float sensorValue = event.values[0];
            Log.i(PSensorTestActivity.TAG, "PSensor感应值: " + sensorValue);
            Log.i(TAG, "是否在正在通话中？  " + isTelCalling());
            //通话中且屏幕未灭时才可测试灭屏时间，P-Sensor状态为接近时算作开始
            if (isTelCalling() && !mIsScreenOff && sensorValue <= 1.0) {
                //灭屏开始时间
                final long startTime = System.currentTimeMillis();
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int brightness;
                            while (true) {
                                brightness = LcdBrightnessGetter.getLcdBrightness();
                                Log.i(TAG, "LCD亮度值(D): " + brightness);
                                if (brightness == 0) {
                                    long endTime = System.currentTimeMillis();
                                    mIsScreenOff = true;
                                    Log.i(TAG, "灭屏时间: " + (endTime - startTime));
                                    mRecords.add(new PSensorTestRecords((int) (endTime - startTime)));
                                    mIsRecordsUpdate = true;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else if (isTelCalling() && mIsScreenOff && sensorValue > 1.0) {
                final long startTime = System.currentTimeMillis();
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int brightness;
                            while (true) {
                                brightness = LcdBrightnessGetter.getLcdBrightness();
                                Log.i(TAG, "LCD亮度值(L): " + brightness);
                                if (brightness > 0) {
                                    long endTime = System.currentTimeMillis();
                                    mIsScreenOff = false;
                                    Log.i(TAG, "亮屏时间: " + (endTime - startTime));
                                    mRecords.get(mRecords.size() - 1).setScreenOnTime((int) (endTime - startTime));
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    @Override
    public boolean onUnbind(Intent intent) {
        mSensorManager.unregisterListener(mPSensorListener, mProximity);
        return super.onUnbind(intent);
    }

    /**
     * 判断是否正在通话中的方法
     *
     * @return 是否正在通话中的布尔值
     */
    public boolean isTelCalling() {
        boolean isCalling = false;
        if (TelephonyManager.CALL_STATE_OFFHOOK == mTelephonyManager.getCallState()
                || TelephonyManager.CALL_STATE_RINGING == mTelephonyManager.getCallState()) {
            isCalling = true;
        }
        return isCalling;
    }

    public class PSTestBinder extends Binder {
        public List<PSensorTestRecords> getRecords() {
            return mRecords;
        }

        public boolean getIsRecordsUpdate() {
            return mIsRecordsUpdate;
        }

        public void setIsRecordsUpdate(boolean isRecordsUpdate){
            mIsRecordsUpdate = isRecordsUpdate;
        }
    }
}
