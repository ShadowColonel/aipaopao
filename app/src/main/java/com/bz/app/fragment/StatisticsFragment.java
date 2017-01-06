package com.bz.app.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bz.app.R;
import com.bz.app.adapter.ViewPagerAdapter;

/**
 * Created by ThinkPad User on 2016/12/29.
 */

public class StatisticsFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTabLayout = (TabLayout) view.findViewById(R.id.statistics_tab_layout);
        mViewPager = (ViewPager) view.findViewById(R.id.statistics_view_pager);
        mAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
