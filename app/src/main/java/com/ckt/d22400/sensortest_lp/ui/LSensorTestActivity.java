package com.ckt.d22400.sensortest_lp.ui;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.d22400.sensortest_lp.LcdBrightnessGetter;
import com.ckt.d22400.sensortest_lp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LSensorTestActivity extends AppCompatActivity {

    public static final String TAG = "LST.";
    //表示是否正在监听LCD亮度值的布尔值
    private boolean isListening = false;


    @BindView(R.id.toolBar)
    Toolbar mToolBar;
    @BindView(R.id.tv_lux)
    TextView mLuxTextView;
    @BindView(R.id.tv_brightness)
    TextView mBrightnessTextView;
    @BindView(R.id.btn_start)
    Button mStartButton;
    @BindView(R.id.btn_stop)
    Button mStopButton;
    @BindView(R.id.rv_records)
    RecyclerView mRecyclerView;
    private SensorManager mSensorManager;
    private Sensor mLight;//光照传感器
    private int mLcdBrightness = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lsensor_test);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        mToolBar.setTitle("L-Sensor测试");
        initVariable();
    }

    private void initVariable() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pst, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.btn_save_excel) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorEventListener, mLight, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mLuxTextView.setText(getString(R.string.show_lux, event.values[0]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @OnClick({R.id.btn_start, R.id.btn_stop})
    public void onButtonClicked(Button button) {
        switch (button.getId()) {
            case R.id.btn_start:
                try {
                    int mode = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
                    if (mode != Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                        Toast.makeText(this, "请开启自动亮度模式后再测试", Toast.LENGTH_SHORT).show();
                    } else {
                        startTest();
                    }
                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_stop:
                stopTest();
                break;
        }
    }

    /**
     * 通过不断访问节点来获得并显示当前LCD亮度值
     */
    private void listenLcdBrightness() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int temp = -1;
                while (isListening) {
                    Log.i(TAG, "run: ");
                    try {
                        temp = LcdBrightnessGetter.getLcdBrightness();
                        if (mLcdBrightness != temp) {
                            mLcdBrightness = temp;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBrightnessTextView.setText(getString(R.string.brightness, mLcdBrightness));
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void startTest() {
        mBrightnessTextView.setVisibility(View.VISIBLE);
        isListening = true;
        listenLcdBrightness();
    }

    private void stopTest() {
        mBrightnessTextView.setVisibility(View.INVISIBLE);
        isListening = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isListening) {
            stopTest();
        }
    }
}
