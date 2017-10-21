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
import android.util.Log;

import com.ckt.d22400.sensortest_lp.R;
import com.ckt.d22400.sensortest_lp.ui.PSensorTestActivity;

public class PSensorTestService extends Service {

    private NotificationManager mNotificationManager;
    private SensorManager mSensorManager;
    private Sensor mProximity;

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
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
            Log.i(PSensorTestActivity.TAG, "PSensor感应值: "+event.values[0]);
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


    public class PSTestBinder extends Binder {

    }
}
