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
import android.widget.Toast;

import com.ckt.d22400.sensortest_lp.R;
import com.ckt.d22400.sensortest_lp.model.PSensorTestRecords;
import com.ckt.d22400.sensortest_lp.service.PSensorTestService;

import java.util.List;

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
    @BindView(R.id.btn_get_records)
    Button mGetRecordsButton;
    @BindView(R.id.btn_clear)
    Button mClearButton;
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
    private List<PSensorTestRecords> mRecords;

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


    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_get_records, R.id.btn_clear})
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
            case R.id.btn_get_records:
                if (!mIsServiceCrashed) {
                    mRecords = mService.getRecords();
                    mService.setIsRecordsUpdate(false);//更新数据完毕
                }else {
                    Toast.makeText(this, "记录未更新或无记录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_clear:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
