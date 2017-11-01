package com.ckt.d22400.sensortest_lp.ui;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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
import com.ckt.d22400.sensortest_lp.model.LSensorTestRecords;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LSensorTestActivity extends AppCompatActivity {

    public static final String TAG = "LST.";
    //表示是否正在监听LCD亮度值的布尔值
    private boolean mIsStart = false;
    //表示是否遮盖L-Sensor的布尔值
    private boolean mIsCoverLSensor = false;
    private int mLcdBrightness = -1;
    private long mStartTime;
    private long mTempTime;//用来计算暗亮时间的缓存时间
    private float mLux;

    private List<LSensorTestRecords> mRecords;
    private SensorManager mSensorManager;
    private Sensor mLight;//光照传感器

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lsensor_test);
        ButterKnife.bind(this);
        initView();
        initVariable();
    }

    private void initView() {
        setSupportActionBar(mToolBar);
        mToolBar.setTitle("L-Sensor测试");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initVariable() {
        mRecords = new ArrayList<>();
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
            mLux = event.values[0];
            mLuxTextView.setText(getString(R.string.show_lux, mLux));
            if (mIsStart && mLux == 0.0f) {
                mIsCoverLSensor = true;
                mRecords.add(new LSensorTestRecords(
                        mRecords.size() + 1, "暗", (int) mLux, mLcdBrightness + "", System.currentTimeMillis()));
            }
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
                int temp;
                while (mIsStart) {
                    try {
                        temp = LcdBrightnessGetter.getLcdBrightness();
                        if (mLcdBrightness != temp) {
                            mTempTime = System.currentTimeMillis();
                            mLcdBrightness = temp;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(TAG, mLcdBrightness+"");
                                    mBrightnessTextView.setText(getString(R.string.brightness, mLcdBrightness));
                                }
                            });
                        } else {
                            if (mIsCoverLSensor) {
                                if ((System.currentTimeMillis() - mTempTime) > 2000) {
                                    Log.i(TAG, "2");
//                                    LSensorTestRecords records = mRecords.get(mRecords.size() - 1);
//                                    records.setRange(records.getRange() + "->" + mLcdBrightness);
//                                    records.setTime(System.currentTimeMillis() - records.getTime());
//                                    Log.i(TAG,
//                                            "次数: " + mRecords.get(mRecords.size() - 1).getNo() + "\n"
//                                                    + "测试项: " + mRecords.get(mRecords.size() - 1).getTestProject() + "\n"
//                                                    + "光照强度: " + mRecords.get(mRecords.size() - 1).getLux() + "\n"
//                                                    + "范围: " + mRecords.get(mRecords.size() - 1).getRange() + "\n"
//                                                    + "时间: " + mRecords.get(mRecords.size() - 1).getTime() + "\n"
//                                    );
                                    mIsCoverLSensor = false;
                                }
                            }
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
        mIsStart = true;
        listenLcdBrightness();
    }

    private void stopTest() {
        mBrightnessTextView.setVisibility(View.INVISIBLE);
        mIsStart = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsStart) {
            stopTest();
        }
    }
}
