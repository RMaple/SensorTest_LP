package com.ckt.d22400.sensortest_lp.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ckt.d22400.sensortest_lp.R;
import com.ckt.d22400.sensortest_lp.adapter.RecordsListAdapter;
import com.ckt.d22400.sensortest_lp.service.PSensorTestService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PSensorTestActivity extends AppCompatActivity {

    public static final String TAG = "PST.";

    //表示服务是否异常终止的布尔值
    private boolean mIsServiceCrashed = false;

    private RecordsListAdapter mRecordsListAdapter;

    @BindView(R.id.toolBar)
    Toolbar mToolBar;
    @BindView(R.id.tv_average)
    TextView mAverageTextView;
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
            mRecordsListAdapter = new RecordsListAdapter(PSensorTestActivity.this, mService.getRecords());
            mRecyclerView.setAdapter(mRecordsListAdapter);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(PSensorTestActivity.this,
                    DividerItemDecoration.VERTICAL_LIST));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsServiceCrashed = true;
            Toast.makeText(PSensorTestActivity.this, "测试服务异常关闭，测试已停止", Toast.LENGTH_SHORT).show();
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
        setSupportActionBar(mToolBar);
        mToolBar.setTitle("P-Sensor测试");
        mStopButton.setEnabled(false);
        mClearButton.setEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsServiceCrashed) {
            //如果服务已崩溃，禁用“停止测试”按钮
            mStopButton.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pst,menu);
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

    @OnClick({R.id.btn_start, R.id.btn_stop, R.id.btn_get_records, R.id.btn_clear})
    public void onButtonClicked(Button button) {
        switch (button.getId()) {
            case R.id.btn_start:
                mStartButton.setEnabled(false);
                mStopButton.setEnabled(true);
//                isTesting = true;
                Intent service = new Intent(this, PSensorTestService.class);
                bindService(service, mConnection, BIND_AUTO_CREATE);
                //开始测试后自动跳转到拨号
                Intent toDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"));
                startActivity(toDial);
                break;
            case R.id.btn_stop:
                mStartButton.setEnabled(true);
                mStopButton.setEnabled(false);
                unbindService(mConnection);
                break;
            case R.id.btn_get_records:
                if (!mIsServiceCrashed && mService.getIsRecordsUpdate()) {
                    mRecordsListAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(mService.getRecords().size());
                    mAverageTextView.setText(getString(R.string.average_time,
                            mService.getAverageOffTime(), mService.getAverageOnTime()));
                    mClearButton.setEnabled(true);//有记录显示后才能点击“清除记录”按钮
                    mService.setIsRecordsUpdate(false);//更新记录完毕
                } else {
                    Toast.makeText(this, "记录未更新或无记录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_clear:
                mService.clearRecords();
                mRecordsListAdapter.notifyDataSetChanged();
                mAverageTextView.setText("");
                mClearButton.setEnabled(false);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
