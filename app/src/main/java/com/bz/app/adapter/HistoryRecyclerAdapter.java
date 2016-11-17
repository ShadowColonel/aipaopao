package com.bz.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bz.app.R;
import com.bz.app.entity.RunningRecord;

import org.w3c.dom.Text;

import java.util.List;


/**
 * Created by ThinkPad User on 2016/11/15.
 */

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<RunningRecord> historyList = null;
    private Context mContext;
    private LayoutInflater mInflater;

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
        RunningRecord record = historyList.get(position);
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.date.setText(record.getDate());
        itemViewHolder.record.setText(record.toString());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView record;

        public ItemViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.item_history_date);
            record = (TextView) itemView.findViewById(R.id.item_history_record);
        }
    }
}
