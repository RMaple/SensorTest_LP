package com.ckt.d22400.sensortest_lp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ckt.d22400.sensortest_lp.R;
import com.ckt.d22400.sensortest_lp.model.PSensorTestRecords;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by D22400 on 2017/10/20.
 */

public class RecordsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<PSensorTestRecords> mRecords;

    public RecordsListAdapter(Context context, List<PSensorTestRecords> records) {
        mContext = context;
        mRecords = records;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PSensorTestViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_psensor_records, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PSensorTestViewHolder) holder).bind(position);
    }

    @Override
    public int getItemCount() {
        return mRecords.size();
    }


    class PSensorTestViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_no)
        TextView mNoTextView;
        @BindView(R.id.tv_off_time)
        TextView mOffTimeTextView;
        @BindView(R.id.tv_on_time)
        TextView mOnTimeTextView;

        PSensorTestViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        void bind(int position) {
            mNoTextView.setText(String.valueOf(position + 1));
            mOffTimeTextView.setText(String.valueOf(mRecords.get(position).getScreenOffTime()));
            mOnTimeTextView.setText(String.valueOf(mRecords.get(position).getScreenOnTime()));
        }
    }
}
