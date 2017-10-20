package com.ckt.d22400.sensortest_lp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ckt.d22400.sensortest_lp.R;

public class PSensorTestService extends Service {

    private NotificationManager mNotificationManager;
    private SensorManager mSensorManager;
    private Sensor mProximity;

    public PSensorTestService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(mPSensorListener, mProximity, SensorManager.SENSOR_DELAY_FASTEST);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getClass().getSimpleName())
                .setContentText("测试中...")
                .build();
        mNotificationManager.notify(1, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PSTestBinder();
    }

    public class PSTestBinder extends Binder {

    }

    private SensorEventListener mPSensorListener = new SensorEventListener() {
        //PSensor事件监听器
        @Override
        public void onSensorChanged(SensorEvent event) {
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        mSensorManager.unregisterListener(mPSensorListener, mProximity);

    }
}
