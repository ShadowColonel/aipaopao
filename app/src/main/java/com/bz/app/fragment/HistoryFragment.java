package com.bz.app.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bz.app.R;
import com.bz.app.adapter.HistoryRecyclerAdapter;
import com.bz.app.database.DBAdapter;
import com.bz.app.entity.RunningRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad User on 2016/12/29.
 */

public class HistoryFragment extends Fragment {

    private RecyclerView mHistoryRecycler;
    private HistoryRecyclerAdapter mAdapter;
    private List<RunningRecord> list = new ArrayList<>();
    private DBAdapter mDBAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDBAdapter = new DBAdapter(getActivity());
        mDBAdapter.open();
        list = mDBAdapter.queryAllRecord();
        mHistoryRecycler = (RecyclerView) view.findViewById(R.id.history_recycler);
        mHistoryRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new HistoryRecyclerAdapter(list, getActivity());
        mHistoryRecycler.setAdapter(mAdapter);
    }
}
