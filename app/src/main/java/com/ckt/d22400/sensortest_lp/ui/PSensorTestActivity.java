package com.ckt.d22400.sensortest_lp.ui;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.d22400.sensortest_lp.LcdBrightnessGetter;
import com.ckt.d22400.sensortest_lp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PSensorTestActivity extends AppCompatActivity {

    public static final String TAG = "PST.";
    //表示是否正在测试的布尔值
    private boolean isTesting = false;

    @BindView(R.id.toolBar)
    Toolbar mToolBar;
    @BindView(R.id.tv_proximity)
    TextView mProximityTextView;
    @BindView(R.id.btn_start)
    Button mStartButton;
    @BindView(R.id.btn_stop)
    Button mStopButton;
    @BindView(R.id.rv_records)
    RecyclerView mRecyclerView;

    private SensorManager mSensorManager;
    private Sensor mProximity;
    private ServiceConnection mConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psensor_test);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSensorManager.registerListener(mPSensorListener, mProximity, SensorManager.SENSOR_DELAY_FASTEST);
    }

    private SensorEventListener mPSensorListener = new SensorEventListener() {
        //PSensor事件监听器
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.i(TAG, "onSensorChanged: " + event.values[0]);
            mProximityTextView.setText(getString(R.string.proximity_str, event.values[0]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @OnClick({R.id.btn_start, R.id.btn_stop})
    public void onButtonClicked(Button button) {
        switch (button.getId()) {
            case R.id.btn_start:
                isTesting = true;
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Message message = Message.obtain();
//                            message.what = 1;
//                            message.arg1 = LcdBrightnessGetter.getLcdBrightness();
//                            mHandler.sendMessage(message);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
                break;
            case R.id.btn_stop:
                break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Toast.makeText(PSensorTestActivity.this, ""+msg.arg1, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mPSensorListener, mProximity);
    }


}
