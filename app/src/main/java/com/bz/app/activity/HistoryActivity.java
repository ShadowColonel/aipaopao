package com.bz.app.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bz.app.R;
import com.bz.app.adapter.HistoryRecyclerAdapter;
import com.bz.app.database.DBAdapter;
import com.bz.app.entity.RunningRecord;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {

    private RecyclerView mHistoryRecycler;
    private HistoryRecyclerAdapter mAdapter;
    private List<RunningRecord> list = new ArrayList<>();
    private DBAdapter mDBAdapter;
    private static final String LOG_TAG = "HistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history, "历史记录");

        mDBAdapter = new DBAdapter(this);
        mDBAdapter.open();
        list = mDBAdapter.queryAllRecord();
        mHistoryRecycler = (RecyclerView) findViewById(R.id.history_recycler_view);
        mHistoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HistoryRecyclerAdapter(list, this);
        mHistoryRecycler.setAdapter(mAdapter);

    }

}
