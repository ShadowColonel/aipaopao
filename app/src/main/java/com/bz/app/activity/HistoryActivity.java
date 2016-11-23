package com.bz.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.bz.app.R;
import com.bz.app.adapter.HistoryRecyclerAdapter;
import com.bz.app.database.DBAdapter;
import com.bz.app.entity.RunningRecord;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView mHistoryRecycler;
    private HistoryRecyclerAdapter mAdapter;
    private List<RunningRecord> list = new ArrayList<>();
    private DBAdapter mDBAdapter;
    private static final String LOG_TAG = "HistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initToolBar();

        mDBAdapter = new DBAdapter(this);
        mDBAdapter.open();
        list = mDBAdapter.queryAllRecord();
        Log.v(LOG_TAG, "list.size--->" + list.size());
        mHistoryRecycler = (RecyclerView) findViewById(R.id.history_recycler_view);
        mHistoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HistoryRecyclerAdapter(list, this);
        mHistoryRecycler.setAdapter(mAdapter);

    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.all_toolbar);
        toolbar.setTitle("历史记录");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
