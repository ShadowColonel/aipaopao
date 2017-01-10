package com.bz.app.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.bz.app.R;
import com.bz.app.fragment.AMapFragment;
import com.bz.app.fragment.AboutFragment;
import com.bz.app.fragment.HelpFragment;
import com.bz.app.fragment.HistoryFragment;
import com.bz.app.fragment.SettingFragment;
import com.bz.app.fragment.StatisticsFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("开始跑步");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        addMapFragment();
    }

    private long tempTime;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (System.currentTimeMillis() - tempTime > 2000) {
                Toast.makeText(MainActivity.this, "再点一次，退出", Toast.LENGTH_SHORT).show();
                tempTime = System.currentTimeMillis();
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_start) {
            toolbar.setTitle("开始跑步");
            replaceFragment(mapFragment);

        } else if (id == R.id.nav_data) {
            toolbar.setTitle("数据统计");
            if (mStatisticsFragment == null) {
                mStatisticsFragment = new StatisticsFragment();
            }
            replaceFragment(mStatisticsFragment);
        } else if (id == R.id.nav_history) {
            toolbar.setTitle("历史记录");
            mHistoryFragment = new HistoryFragment();
            replaceFragment(mHistoryFragment);
        } else if (id == R.id.nav_setting) {
            toolbar.setTitle("设置");
            if (mSettingFragment == null) {
                mSettingFragment = new SettingFragment();
            }
            replaceFragment(mSettingFragment);
        } else if (id == R.id.nav_help) {
            toolbar.setTitle("帮助");
            if (mHelpFragment == null) {
                mHelpFragment = new HelpFragment();
            }
            replaceFragment(mHelpFragment);
        } else if (id == R.id.nav_about) {
            toolbar.setTitle("关于");
            if (mAboutFragment == null) {
                mAboutFragment = new AboutFragment();
            }
            replaceFragment(mAboutFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private AMapFragment mapFragment;
    private AboutFragment mAboutFragment;
    private HelpFragment mHelpFragment;
    private HistoryFragment mHistoryFragment;
    private SettingFragment mSettingFragment;
    private StatisticsFragment mStatisticsFragment;
    private Fragment currentFragment;

    private void addMapFragment() {
        if (mapFragment == null) {
            mapFragment = new AMapFragment();
        }
        if (!mapFragment.isAdded()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_main_container, mapFragment)
                    .commitAllowingStateLoss();
            currentFragment = mapFragment;
        }
    }
    public static final String TAG = "MainActivity";

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (currentFragment == fragment) {
            return;
        }
        ft.hide(currentFragment);
        if (!fragment.isAdded()) {
            ft.add(R.id.activity_main_container, fragment);
        } else {
            ft.show(fragment);
        }
        ft.commitAllowingStateLoss();
        currentFragment = fragment;
    }
}
