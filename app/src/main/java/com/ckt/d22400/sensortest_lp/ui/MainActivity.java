package com.ckt.d22400.sensortest_lp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.ckt.d22400.sensortest_lp.R;
import com.ckt.d22400.sensortest_lp.ui.LSensorTestActivity;
import com.ckt.d22400.sensortest_lp.ui.PSensorTestActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_psensor_test)
    Button mPSensorTestButton;
    @BindView(R.id.btn_lsensor_test)
    Button mLSensorTestButton;
    @BindView(R.id.toolBar)
    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        initView();
    }

    private void initView() {
        mToolBar.setTitle(R.string.app_name);
    }

    @OnClick({R.id.btn_psensor_test, R.id.btn_lsensor_test})
    public void onButtonClicked(Button button) {
        Intent start = new Intent();
        switch (button.getId()) {
            case R.id.btn_psensor_test:
                start.setClass(this, PSensorTestActivity.class);
                startActivity(start);
                break;
            case R.id.btn_lsensor_test:
                start.setClass(this, LSensorTestActivity.class);
                startActivity(start);
                break;
        }
    }
}
