package com.bz.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bz.app.fragment.DataFragment;

/**
 * Created by ThinkPad User on 2016/11/22.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String[] tabTitles = new String[] {"日", "周", "月", "年"};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new DataFragment();
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
