package com.ckt.d22400.sensortest_lp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ckt.d22400.sensortest_lp.R;

import butterknife.ButterKnife;

public class LSensorTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lsensor_test);
        ButterKnife.bind(this);
    }
}
