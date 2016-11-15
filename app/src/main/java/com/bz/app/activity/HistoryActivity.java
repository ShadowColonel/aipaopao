package com.bz.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.bz.app.R;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView mHistoryRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mHistoryRecycler = (RecyclerView) findViewById(R.id.history_recycler_view);


    }
}
