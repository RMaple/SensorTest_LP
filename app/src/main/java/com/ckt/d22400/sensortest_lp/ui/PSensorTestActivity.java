package com.ckt.d22400.sensortest_lp.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;

import com.ckt.d22400.sensortest_lp.R;
import com.ckt.d22400.sensortest_lp.service.PSensorTestService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PSensorTestActivity extends AppCompatActivity {

    public static final String TAG = "PST.";
    //表示是否正在测试的布尔值
    private boolean isTesting = false;
    //表示服务是否异常终止的布尔值
    private boolean mIsServiceCrashed = false;

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


    private PSensorTestService.PSTestBinder mService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (PSensorTestService.PSTestBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsServiceCrashed = true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psensor_test);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        mStopButton.setEnabled(false);
    }

    private void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @OnClick({R.id.btn_start, R.id.btn_stop})
    public void onButtonClicked(Button button) {
        switch (button.getId()) {
            case R.id.btn_start:
                mStartButton.setEnabled(false);
                mStopButton.setEnabled(true);
                isTesting = true;
                Intent service = new Intent(this, PSensorTestService.class);
                bindService(service, mConnection, BIND_AUTO_CREATE);
                //开始测试后自动跳转到拨号
                Intent toDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
                startActivity(toDial);
                break;
            case R.id.btn_stop:
                mStartButton.setEnabled(true);
                mStopButton.setEnabled(false);
                if (!mIsServiceCrashed) {
                    unbindService(mConnection);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
