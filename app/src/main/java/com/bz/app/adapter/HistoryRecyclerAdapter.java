package com.bz.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bz.app.R;
import com.bz.app.activity.RecordDetailsActivity;
import com.bz.app.entity.RunningRecord;
import com.google.gson.internal.LinkedTreeMap;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by ThinkPad User on 2016/11/15.
 */

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Map<String, List<RunningRecord>> historyList = new LinkedTreeMap<>();
    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_RECORD = 1;
    private static final String LOG_TAG = "HistoryRecyclerAdapter";
    private Context mContext;
    private LayoutInflater mInflater;

    public void addALl(List<RunningRecord> data) {
        for (RunningRecord record : data) {
            String key = getCurrentDate(record.getDate());
            if (historyList.containsKey(key)) {
                List<RunningRecord> value = historyList.get(key);
                value.add(record);
            } else {
                List<RunningRecord> value = new ArrayList<>();
                value.add(record);
                historyList.put(key, value);
            }
        }
        notifyDataSetChanged();
    }

    public void clear() {
        historyList.clear();
        notifyDataSetChanged();
    }

    public HistoryRecyclerAdapter(Context mContext) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_RECORD) {
            View view = mInflater.inflate(R.layout.item_history_record, parent, false);
            return new RecordViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.item_history_date, parent, false);
            return new DateViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_DATE) {
            DateViewHolder dateHolder = (DateViewHolder) holder;
            int sum = 0;
            for (String key : historyList.keySet()) {
                if (sum == position) {
                    dateHolder.date.setText(key);
                    break;
                }
                sum++;
                sum += historyList.get(key).size();
            }
        } else {
            RecordViewHolder recordHolder = (RecordViewHolder) holder;
            int sum = 0;
            boolean isFinish = false;
            for (String key : historyList.keySet()) {
                sum++;
                for (final RunningRecord record : historyList.get(key)) {
                    if (position == sum) {
                        recordHolder.distance.setText(record.getDistance());
                        DecimalFormat df = new DecimalFormat("0.0");
                        String min = df.format(Double.parseDouble(record.getDuration()) / 60.0);
                        recordHolder.duration.setText(min);
                        recordHolder.startTime.setText(getStartTime(record.getDate()));
                        recordHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(mContext, RecordDetailsActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("record", record);
                                intent.putExtras(bundle);
                                mContext.startActivity(intent);
                            }
                        });
                        isFinish = true;
                        break;
                    }
                    sum++;
                }
                if (isFinish) break;
            }
        }
    }

    @Override
    public int getItemCount() {
        int sum = 0;
        for (String key : historyList.keySet()) {
            sum++;
            sum += historyList.get(key).size();
        }
        return sum;
    }

    @Override
    public int getItemViewType(int position) {
        int sum = 0;
        for (String key : historyList.keySet()) {
            if (position == sum) {
                return VIEW_TYPE_DATE;
            }
            sum++;
            sum += historyList.get(key).size();
        }
        return VIEW_TYPE_RECORD;
    }

    class RecordViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_history_record_distance) TextView distance;
        @BindView(R.id.item_history_record_duration) TextView duration;
        @BindView(R.id.item_history_record_start_time) TextView startTime;

        public RecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_history_date) TextView date;

        public DateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private String getCurrentDate(String date) {
        long time = Long.parseLong(date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  EEEE");
        return format.format(time);
    }

    private String getStartTime(String date) {
        long time = Long.parseLong(date);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(time);
    }
}
