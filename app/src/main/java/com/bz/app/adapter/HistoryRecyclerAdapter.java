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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Created by ThinkPad User on 2016/11/15.
 */

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RunningRecord> historyList = null;
    private Context mContext;
    private LayoutInflater mInflater;

    public void addALl(List<RunningRecord> data) {
        historyList.addAll(data);
        notifyDataSetChanged();
    }

    public void clear() {
        historyList.clear();
        notifyDataSetChanged();
    }

    public HistoryRecyclerAdapter(List<RunningRecord> historyList, Context mContext) {
        this.historyList = historyList;
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_running_record, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RunningRecord record = historyList.get(position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.distance.setText(String.format(
                mContext.getResources().getString(R.string.distance), record.getDistance()));
        itemViewHolder.date.setText(getCurrentDate(Long.parseLong(record.getDate())));
        itemViewHolder.duration.setText(record.getDuration());
        itemViewHolder.speed.setText(String.format(
                mContext.getResources().getString(R.string.speed), record.getAverageSpeed()));

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecordDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("record", record);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    //格式化当前日期
    private String getCurrentDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy");
        Date curDate = new Date(time);
        String date = format.format(curDate);
        return date;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView distance;
        TextView date;
        TextView duration;
        TextView speed;


        public ItemViewHolder(View itemView) {
            super(itemView);

            distance = (TextView) itemView.findViewById(R.id.item_history_distance);
            date = (TextView) itemView.findViewById(R.id.item_history_date);
            duration = (TextView) itemView.findViewById(R.id.item_history_duration);
            speed = (TextView) itemView.findViewById(R.id.item_history_average_speed);

        }
    }
}
